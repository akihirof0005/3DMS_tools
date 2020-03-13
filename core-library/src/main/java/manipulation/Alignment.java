package manipulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import glycan.Atom;
import glycan.NodeAtom;
import glycan.Triple;

public class Alignment {
  final static int lim = 4;

  public static NodeAtom[] makeNA(Atom[] a) {
    int num = 0;
    for (int k = 0; k < a.length; k++) {
      //if (!a[k].symbol.equalsIgnoreCase("H")) {
      if (!a[k].symbol.equalsIgnoreCase("H")
          && !a[k].symbol.equalsIgnoreCase("C")) {
        num++;
          }
      }
    NodeAtom[] ns = new NodeAtom[num];
    num = 0;
    for (int k = 0; k < a.length; k++) {
    //if (!a[k].symbol.equalsIgnoreCase("H")) {
      if (!a[k].symbol.equalsIgnoreCase("H")
         && !a[k].symbol.equalsIgnoreCase("C")) {
        ns[num] = new NodeAtom(k, a[k]);
        num++;
          }
      }
    //System.out.println(num);
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
if (!ns[j[0]].atom.symbol.equals("C")){
//        if (!ns[j[0]].atom.symbol.equals("C")
//            && !ns[j[1]].atom.symbol.equals("C")){
//            && !ns[j[2]].atom.symbol.equals("C")) {
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

    public static ArrayList<int[]> Alignment(Atom[] a, Atom[] b,double limite) {
      ArrayList<Atom[]> cls = new ArrayList<Atom[]>();
      cls.add(a);
      cls.add(b);
      return Alignment_2(cls , limite);
    }

    public static ArrayList<int[]> Alignment_2(List<Atom[]> Cls, double limite) {

      List<List<Triple>> t = new ArrayList<List<Triple>>();
      HashMap<Integer, HashSet<Integer>> hozon2 = new HashMap<Integer, HashSet<Integer>>();
      HashMap<Integer, HashSet<Integer>> hozon3 = new HashMap<Integer, HashSet<Integer>>();
      int num = Cls.get(0).length;
      if (Cls.get(0).length < Cls.get(1).length) {
        num = Cls.get(1).length;
      }
      for (int x = 0; x < num; x++) {
        hozon2.put(x, new HashSet<Integer>());
        hozon3.put(x, new HashSet<Integer>());
      }
      for (int i1 = 0; i1 < Cls.size(); i1++) {
        t.add(makeT(makeNA(Cls.get(i1))));
      }

      List<HashSet<Integer>> hozon = new ArrayList<HashSet<Integer>>();

      hozon.add(new HashSet<Integer>());
      hozon.add(new HashSet<Integer>());

      HashMap<String,Integer> hozon5 = new HashMap<String,Integer>();

      for (int x = 0; x < t.get(0).size(); x++) {
        for (Triple t2 : t.get(1)) {
          List<List<Integer>> kari = agreeTri(t.get(0).get(x), t2, limite);
          //                            if(kari.size() > 1) {
          //                              for(int i = 0 ; i < 3 ; i++){
          //                                      if(null != hozon5.get(kari.get(0).get(i)+"-"+kari.get(1).get(i))){
          //                                        hozon.get(0).addAll((Collection) kari.get(0));
          //                                        hozon.get(1).addAll((Collection) kari.get(1));
          //                                          hozon2.get(kari.get(0).get(i)).add(kari.get(1).get(i));
          //                                          hozon3.get(kari.get(1).get(i)).add(kari.get(0).get(i));
          //                                          }else{
          //                                          hozon5.put(kari.get(0).get(i)+"-"+kari.get(1).get(i),1);
          //                                      }
          //                                  }
          //                              }
          if(kari.size() > 1) {
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
        int[] hantei = new int[1];
        hantei[0] = Integer.MAX_VALUE;
        ArrayList<int[]> ret = new ArrayList<int[]>();
        ret.add(hantei);
        if(!(hozon.get(0).size() == 3 && hozon.get(1).size() ==3)){
          //(new ArrayList<Integer>(hozon.get(1)));
          return ret;
        }
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
      ArrayList<int[]> ret = new ArrayList<int[]>();
      ret.add(hantei);

      if (hozon2.keySet().size() > hozon3.keySet().size()) {
        hozon2 = hozon3;
        ret.get(0)[0] = 1;
      }
      // System.out.println(hozon2);
      int[] keyArray = new int[hozon2.keySet().size()];
      int i2 = 0;
      for (int a : hozon2.keySet()) {
        keyArray[i2] = a;
        i2++;
      }
      ret.add(keyArray);
      ret.add(new int[keyArray.length]);
      for (int k = 0; k < keyArray.length; k++) {
        ArrayList hinan = new ArrayList();
        hinan.addAll(hozon2.get(keyArray[k]));
        for (int i = 0; i < hinan.size(); i++) {
          int size = ret.size();
          if (i > 0) {
            for (int n = 2; n < size; n++) {
              int[] hinan2 = ret.get(n).clone();
              hinan2[k] = (Integer) hinan.get(i);
              ret.add(hinan2);
              hinan2 = null;
            }
          } else {
            for (int m = 2; m < size; m++) {
              ret.get(m)[k] = (Integer) hinan.get(i);
            }
          }
        }
      }

      // List<HashSet> r = agreePori(hozon2, Cls);

      return ret;

    }

    private static List<HashSet> agreePori(
        HashMap<Integer, HashSet<Integer>> hozon2, List<Atom[]> Cls) {
      List<HashSet> list = null;
      ArrayList<Integer> a = new ArrayList<Integer>();
      List<ArrayList<Integer>> b = new ArrayList<ArrayList<Integer>>();
      for (int i : hozon2.keySet()) {
        if (hozon2.get(i).equals(new HashSet<Integer>())) {
          a.add(i);
          for (int ii = 0; ii < hozon2.get(i).size(); ii++) {

            Iterator<Integer> iterator = hozon2.get(i).iterator();
            int c = 0;
            while (iterator.hasNext()) {
              if (c > 1) {
                b.get(c).addAll((b.get(c - 1)));
                b.get(c).remove(b.get(c - 1).size() - 1);
              }
              b.get(c).add(((Integer) iterator.next()).intValue());
              c++;
            }
          }
        }
      }

      NodeAtom[] a3 = makeNA(Cls.get(0));
      NodeAtom[] b3 = makeNA(Cls.get(1));
      int sub = a.size();
      int l = b.size();

      if (sub > l) {
        sub = b.size();

        b3 = makeNA(Cls.get(0));
        a3 = makeNA(Cls.get(1));

      }
      a.add(0);
      // b.add(0);
      ArrayList<int[]> aa = CombinationGenerator.subSet(a);
      // ArrayList<int[]> bb = subSet(b, 0, b.size(), sub, new int[sub],
      // new ArrayList<int[]>());
      // list = agreePori2(aa, a3, bb, b3);

      // sub--;
      // }
      return list;

    }

    private static List<HashSet> agreePori2(ArrayList<int[]> aa, NodeAtom[] a3,
        ArrayList<int[]> bb, NodeAtom[] b3) {
      List ret2 = new ArrayList();
      List<List<Integer>> d = null;
      System.out.println(aa.size() + "  " + bb.size());
      int[] a = aa.get(0);
      ArrayList<int[]> aaa = manipulation.CombinationGenerator.subSet(a);
      for (int[] b : bb) {
        ArrayList<int[]> bbb = manipulation.CombinationGenerator.subSet(b);
        HashSet set = new HashSet();
        for (int i = 0; i < bbb.size(); i++) {

          for (int[] a2 : aaa) {

            d = agreeTri2(a3[a2[0]], a3[a2[1]], a3[a2[2]],
                b3[bbb.get(i)[0]], b3[bbb.get(i)[1]],
                b3[bbb.get(i)[2]]);
            if (d.size() >= 2) {
              for (int dd : d.get(1)) {
                set.add(dd);
                continue;
              }
            }
          }

          // System.out.println(bbb.size());
          if (set.size() == a.length) {
            ret2.add(set);
          }
          set = new HashSet();
        }
        // if(set.size()!=0)

      }
      HashSet ha = new HashSet();
      for (int i : a) {
        ha.add(i);
      }
      if (ret2.size() != 0) {
        ret2.add(ha);
      }
      return ret2;
    }

    private static List<List<Integer>> agreeTri2(NodeAtom a1, NodeAtom a2,
        NodeAtom a3, NodeAtom b1, NodeAtom b2, NodeAtom b3) {
      Triple a = new Triple(a1, a2, a3, 100);
      Triple b = new Triple(b1, b2, b3, 100);

      List<List<Integer>> list = agreeTri(a, b, 0.1);
      return list;
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

    }
