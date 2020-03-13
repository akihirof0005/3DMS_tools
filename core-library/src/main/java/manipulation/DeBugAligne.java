package manipulation;

import glycan.Atom;
import glycan.GetMasses;
import glycan.Glycan;
import glycan.Kabsch;
import glycan.NodeAtom;
import glycan.Triple;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DeBugAligne {
    static int lim = 4;
    static GetMasses map = new GetMasses();
    static HashMap<String, Boolean> cTri = new HashMap<String, Boolean>();

    public static void main(String[] args) {

        File file = new File("../tmp/Cluster/listGlycan(S3)Galb1-3(Fuca1-4)GlcNAcb.pdb");
        Object glycan = new Glycan();
        glycan = readObj(file, glycan);

        File file1 = new File("../tmp/Cluster/listGlycan(3S)Galb1-4GlcNAcb.pdb");
        Object glycan1 = new Glycan();
        glycan1 = readObj(file1, glycan1);

        debugAlignement((Glycan) glycan, (Glycan) glycan1);
    }

    private static void debugAlignement(Glycan target, Glycan glycan) {

        List<Atom[]> gly_atomsT = new ArrayList<Atom[]>();
        List<Atom[]> gly_atomsG = new ArrayList<Atom[]>();

        int atomsNum = glycan.symbols.length;
        int length = glycan.allatom[0].length;
        for (int k = 0; k < length; k++) {
            Atom[] atoms = new Atom[atomsNum];
            for (int m = 0; m < atomsNum; m++) {

                atoms[m] = new Atom(String.valueOf(glycan.symbols[m]),
                        glycan.allatom[m][k]);
            }
            gly_atomsG.add(atoms);
        }

        atomsNum = target.symbols.length;
        length = target.allatom[0].length;
        for (int k = 0; k < length; k++) {
            Atom[] atoms = new Atom[atomsNum];
            for (int m = 0; m < atomsNum; m++) {

                atoms[m] = new Atom(String.valueOf(target.symbols[m]),
                        target.allatom[m][k]);
            }
            gly_atomsT.add(atoms);
        }

        ArrayList<HashSet<List>> list1 = new ArrayList<HashSet<List>>();
        for (int m = 0; m < gly_atomsT.size(); m++) {
            System.out.println(m);
            list1.add(new HashSet());
            Atom[] a = gly_atomsT.get(m);
            for (int k = 0; k < gly_atomsG.size(); k++) {
                //System.out.println(m + "  X  " + k);
                Atom[] b = gly_atomsG.get(k);
                ArrayList<int[]> aligne = Alignment(a, b,0.002);

                if (aligne != null) {
                    List Result1 = decideAlignment(aligne, a, b);
                    if (Result1 != null) {
                        list1.get(m).add((List) Result1.get(0));
                        System.out.println(Result1);
                    }
                }
            }
        }

    }

    private static Object readObj(File file, Object obj) {
        try {
            System.out.println(file);
            FileInputStream inFile;

            inFile = new FileInputStream(file);

            ObjectInputStream inObject = new ObjectInputStream(inFile);
            obj = inObject.readObject();

            inObject.close();
            inFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private static ArrayList decideAlignment(ArrayList<int[]> aligne,
            Atom[] comformationA, Atom[] comformationB) {

        int a = aligne.get(0)[0];
        double min = 100;
        int hozon = 0;
        // System.out.println(a+"aiko");
        if (a == 1) {
            Atom[] hozonAtom = comformationA;
            comformationA = comformationB;
            comformationB = hozonAtom;
        }

        // System.out.print("glycan=>");

        Atom[] cA = new Atom[aligne.get(1).length];
        for (int i = 0; i < aligne.get(1).length; i++) {
            cA[i] = comformationA[aligne.get(1)[i]];
            // System.out.print(cA[i].symbol+"_");
        }
        // System.out.println();

        // aligne.remove(0);aline.remove(0);

        Atom[] cB = new Atom[aligne.get(1).length];
        for (int i = 2; i < aligne.size(); i++) {
            // System.out.print("target=>");
            // if(aline.get(i) != aline.get(i+1)){

            for (int k = 0; k < aligne.get(i).length; k++) {
                cB[k] = comformationB[aligne.get(i)[k]];
                // System.out.print(cB[k]+"_");
            }
            Kabsch kca = null;
            Atom[] ca = copyConfomation(cA);
            Atom[] cb = copyConfomation(cB);
            kca = new Kabsch(ca, cb, map.getMasses());
            kca.align();
            double kyori = kca.getRMSD();
            if (min > kyori) {
                min = kyori;
                // System.out.println(min);
                hozon = i;
            }
            // }else{continue;};

        }
        // System.out.println("step!");
        ArrayList<Integer> hozonA = null;
        ArrayList<Integer> hozonB = null;
        int size = aligne.get(1).length;

        double kyori = 0.3 * Math.log10((size) * (size - 1) * (size - 2) * 1.2
                / 6);
        while (min > kyori && size > lim) {

            min = 100;

            for (int k = 0; k < size; k++) {
                ArrayList<Integer> copyA = new ArrayList<Integer>();
                for (int aa : aligne.get(1)) {
                    copyA.add(aa);
                }
                ArrayList<Integer> copyB = new ArrayList<Integer>();
                for (int aa : aligne.get(hozon)) {
                    copyB.add(aa);
                }
                copyA.remove(k);
                copyB.remove(k);

                for (int i = 0; i < copyA.size(); i++) {
                    cA[i] = comformationA[copyA.get(i)];
                }
                for (int i = 0; i < copyB.size(); i++) {
                    cB[i] = comformationB[copyB.get(i)];
                }
                Atom[] ca = copyConfomation(cA);
                Atom[] cb = copyConfomation(cB);
                Kabsch kca = null;
                kca = new Kabsch(ca, cb, map.getMasses());
                kca.align();
                if (min > kca.getRMSD()) {
                    min = kca.getRMSD();
                    // System.out.println(min);
                    hozonA = copyA;
                    hozonB = copyB;
                }
            }

            size = hozonA.size();
            kyori = 0.3 * Math
                    .log10(1.2 * (size) * (size - 1) * (size - 2) / 6);
            int[] loopA = new int[hozonA.size()];
            int[] loopB = new int[hozonB.size()];
            for (int i = 0; i < loopA.length; i++) {
                loopA[i] = hozonA.get(i);
            }
            for (int i = 0; i < loopB.length; i++) {
                loopB[i] = hozonB.get(i);
            }
            aligne.set(1, loopA);
            aligne.set(hozon, loopB);
        }
        if (min < kyori) {
            ArrayList ret2 = new ArrayList();
            ArrayList<Integer> a_aligne = new ArrayList<Integer>();
            for (int aa : aligne.get(1)) {
                a_aligne.add(aa);
            }
            ArrayList<Integer> b_aligne = new ArrayList<Integer>();
            for (int aa : aligne.get(hozon)) {
                b_aligne.add(aa);
            }
            // System.out.println(min);
            aligne = null;
            if (a == 0) {
                ret2.add(a_aligne);
                ret2.add(b_aligne);
            } else {
                ret2.add(b_aligne);
                ret2.add(a_aligne);
            }

            for (int i = 0; i < a_aligne.size(); i++) {
                cA[i] = comformationA[a_aligne.get(i)];
                cB[i] = comformationB[b_aligne.get(i)];
            }
            Atom[] ca = copyConfomation(cA);
            Atom[] cb = copyConfomation(cB);
            Kabsch kca = null;
            kca = new Kabsch(ca, cb, map.getMasses());
            kca.align();
            Vector3D mass = kca.getCenterOfMass();
            kca.rotateAtomContainer(comformationB);
            // System.out.println(mass);

            for (int i = 0; i < comformationB.length; i++) {
                Atom atom = comformationB[i];
                atom.Vector3D = new Vector3D(atom.Vector3D.crd[0]
                        + mass.crd[0], atom.Vector3D.crd[1] + mass.crd[1],
                        atom.Vector3D.crd[2] + mass.crd[2]);

            }

            ArrayList ret = new ArrayList();
            ret.add(copyConfomation(comformationA));
            ret.add(copyConfomation(comformationB));

            ret2.add(ret);
            // System.out.println(size +"_"+kyori+">"+min);
            return ret2;
        } else {
            return null;
        }
    }

    public static NodeAtom[] toArrayNA(HashSet<NodeAtom> HA) {
        ArrayList<NodeAtom> ret = new ArrayList<NodeAtom>(HA);
        NodeAtom[] ret2 = new NodeAtom[ret.size()];
        for (int i = 0; i < ret.size(); i++) {
            ret2[i] = ret.get(i);
        }
        return ret2;
    }

    public static ArrayList<int[]> Alignment(Atom[] a, Atom[] b, double limite) {

      //List<List<Triple>> t = new ArrayList<List<Triple>>();
      List<Triple> tA = new ArrayList<Triple>();
      List<Triple> tB = new ArrayList<Triple>();
        HashMap<Integer, HashSet<Integer>> hozon2 = new HashMap<Integer, HashSet<Integer>>();
        HashMap<Integer, HashSet<Integer>> hozon3 = new HashMap<Integer, HashSet<Integer>>();
        int num = a.length;
        if (a.length < b.length) {
            num = b.length;
        }
        for (int x = 0; x < num; x++) {
            hozon2.put(x, new HashSet<Integer>());
            hozon3.put(x, new HashSet<Integer>());
        }
        tA =(makeT(makeNA(a)));
        tB =(makeT(makeNA(b)));
        
        List<HashSet<Integer>> hozon = new ArrayList<HashSet<Integer>>();

        hozon.add(new HashSet<Integer>());
        hozon.add(new HashSet<Integer>());

        for (int x = 0; x < tA.size(); x++) {
            for (Triple t2 : tB) {
                List<List<Integer>> kari = agreeTri(tA.get(x), t2, limite);
                
                if (kari.size() > 1) {
                    hozon.get(0).addAll((Collection) kari.get(0));
                    hozon.get(1).addAll((Collection) kari.get(1));
                    hozon2.get(kari.get(0).get(0)).add(kari.get(1).get(0));
                    hozon2.get(kari.get(0).get(1)).add(kari.get(1).get(1));
                    hozon2.get(kari.get(0).get(2)).add(kari.get(1).get(2));
                    hozon3.get(kari.get(1).get(0)).add(kari.get(0).get(0));
                    hozon3.get(kari.get(1).get(1)).add(kari.get(0).get(1));
                    hozon3.get(kari.get(1).get(2)).add(kari.get(0).get(2));
                }
            }
        }
        if (!(hozon.get(0).size() > 3 && hozon.get(1).size() > 3)) {
            return null;
        }
        List<Integer> test = new ArrayList<Integer>();
        for (int i : hozon2.keySet()) {
            if (hozon2.get(i).equals(new HashSet<Integer>())) {
                test.add(i);
            }
        }
        for (int i : test) {
            hozon2.remove(i);
        }
        test = new ArrayList<Integer>();
        for (int i : hozon3.keySet()) {
            if (hozon3.get(i).equals(new HashSet<Integer>())) {
                test.add(i);
            }
        }
        for (int i : test) {
            hozon3.remove(i);
        }
        int[] hantei = new int[1];
        hantei[0] = 0;
        ArrayList<int[]> ret2 = new ArrayList<int[]>();
      //  ArrayList<int[]> ret3 = new ArrayList<int[]>();
       // ret.add(hantei);
        
        if (hozon2.keySet().size() > hozon3.keySet().size()) {
            hozon2 = hozon3;
            hantei[0]=1;//ret.get(0)[0] = 1;
        }
        ret2.add(hantei);
        // System.out.println(hozon2);
        int[] keyArray = new int[hozon2.keySet().size()];
        int i2 = 0;
        for (int num1 : hozon2.keySet()) {
            keyArray[i2] = num1;
            i2++;
        }
        ret2.add(keyArray);
        ret2.add(new int[keyArray.length]);
        for (int k = 0; k < keyArray.length; k++) {
            ArrayList hinan = new ArrayList();
            hinan.addAll(hozon2.get(keyArray[k]));
            for (int i = 0; i < hinan.size(); i++) {
                int size = ret2.size();
                if (i > 0) {
                    for (int n = 2; n < size; n++) {
                        int[] hinan2 = ret2.get(n).clone();
                        hinan2[k] = (Integer) hinan.get(i);
                        ret2.add(hinan2);
                        hinan2 = null;
                    }
                } else {
                    for (int m = 2; m < size; m++) {
                        ret2.get(m)[k] = (Integer) hinan.get(i);
                    }
                }
            }
        }

        // List<HashSet> r = agreePori(hozon2, Cls);
        //HashSet<int[]> ret2 = new HashSet<int[]>();
        //ret2.addAll(ret);
        //System.out.println(ret.size());
        //ArrayList<int[]> ret = new ArrayList<int[]>();
        //ret.add(hantei);ret(ret2);
        //System.out.println(ret.size());
        return ret2;

    }

    private static List<List<Integer>> agreeTri(Triple from, Triple to,
            double limit) {
        ArrayList<List<Integer>> ret = new ArrayList<List<Integer>>();

        int[] i1 = { 0, 1, 2 };
        int[] i2 = { 0, 2, 1 };
        int[] i3 = { 1, 0, 2 };
        int[] i4 = { 1, 2, 0 };
        int[] i5 = { 2, 0, 1 };
        int[] i6 = { 2, 1, 0 };

        List<int[]> list = new ArrayList<int[]>();
        list.add(i1);
        list.add(i2);
        list.add(i3);
        list.add(i4);
        list.add(i5);
        list.add(i6);
        double f[] = from.distance;
        double t[] = to.distance;
        NodeAtom[] fn = from.NAs;
        NodeAtom[] tn = to.NAs;
        for (int[] i : list) {

            if ((agree(f[0], t[i[0]], limit) && (agree(f[1], t[i[1]], limit)
                    && (agree(f[2], t[i[2]], limit)) && (fn[0].atom
                    .symbol.equals(tn[i[0]].atom.symbol)
                    && fn[1].atom.symbol
                            .equals(tn[i[1]].atom.symbol) && fn[2]
                    .atom.symbol.equals(tn[i[2]].atom.symbol))))) {
                // if (agree(f[0] , t[i[0]]) && agree(f[1] ,t[i[1]]) && agree(
                // f[2] , t[i[2]]) ) {
                ArrayList<Integer> ret1 = new ArrayList<Integer>();
                ArrayList<Integer> ret2 = new ArrayList<Integer>();
                ret1.add(fn[0].number);
                ret1.add(fn[1].number);
                ret1.add(fn[2].number);
                ret2.add(tn[i[0]].number);
                ret2.add(tn[i[1]].number);
                ret2.add(tn[i[2]].number);
                ret.add(ret1);
                ret.add(ret2);

            }
        }
        return ret;
    }

    private static boolean agree(double d1, double d2, double limit) {
        if (Math.abs(d1 - d2) < limit) {
            return true;
        }
        return false;
    }

    private static NodeAtom[] makeNA(Atom[] a) {
        int num = 0;
        for (int k = 0; k < a.length; k++) {
            // if (!a[k].symbol.equalsIgnoreCase("H")) {
            if (!a[k].symbol.equalsIgnoreCase("H")
                    && !a[k].symbol.equalsIgnoreCase("C")) {
                num++;
            }
        }
        NodeAtom[] ns = new NodeAtom[num];
        num = 0;
        for (int k = 0; k < a.length; k++) {
            // if (!a[k].symbol.equalsIgnoreCase("H")) {
            if (!a[k].symbol.equalsIgnoreCase("H")
                    && !a[k].symbol.equalsIgnoreCase("C")) {
                ns[num] = new NodeAtom(k, a[k]);
                num++;
            }
        }
        return ns;
    }

    private static List<Triple> makeT(NodeAtom[] ns) {
        int[] a = new int[ns.length];
        for (int i = 0; i < ns.length; i++) {
            a[i] = i;
        }
        ArrayList<int[]> combi = manipulation.CombinationGenerator.subSet(a);
        ArrayList<Triple> t = new ArrayList<Triple>();
        for (int i = 0; i < combi.size(); i++) {
            int[] j = combi.get(i);
            Triple hinan = null;
            if (!ns[j[0]].atom.symbol.equals("C")
                    && !ns[j[1]].atom.symbol.equals("C")
                    && !ns[j[2]].atom.symbol.equals("C")) {
                hinan = new Triple(ns[j[0]], ns[j[1]], ns[j[2]], lim);
            }
            if (hinan == null || hinan.distance == null) {
                hinan = null;
                continue;
            }
            t.add(hinan);
        }
        return t;
    }

    private static Atom[] copyConfomation(Atom[] c) {
        Atom[] ret = new Atom[c.length];
        for (int i = 0; i < c.length; i++) {
            Vector3D v = new Vector3D(c[i].Vector3D.crd[0], c[i]
                    .Vector3D.crd[1], c[i].Vector3D.crd[2]);
            ret[i] = new Atom(c[i].symbol, v);
        }
        return ret;
    }
}
