package sskit_core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import glycan.Atom;
import glycan.GetMasses;
import glycan.Kabsch;
import manipulation.Alignment;
import manipulation.FileManipulation;
import manipulation.Vector3D;

import cluster.AverageDistanceEvaluator;
import cluster.Cluster;
import cluster.ClusterBuilder;
import cluster.DistanceEvaluator;
import cluster.FalseItem;
import cluster.Item;
import cluster.Node;

import glycan.*;

public class Run {

  public static Atom[] pdb2atoms(String pdbStr) throws IOException {

    // System.out.println(str);
    String[] strlist = pdbStr.split("\n");

    // Atom[] ret = new Atom[strlist.length - 2];
    List<Atom> list = new ArrayList<Atom>();
    // int num = 0;
    for (int i = 0; i < strlist.length - 2; i++) {
      if (strlist[i].startsWith("ATOM")) {
        String line = strlist[i];
        // System.out.println(strlist[i]);
        String[] lineIndex = line.split(" +", 0);
        //	 System.out.println("aaaa" + lineIndex[2]);
        double x = Double.parseDouble(lineIndex[3]);
        double y = Double.parseDouble(lineIndex[4]);
        double z = Double.parseDouble(lineIndex[5]);
        Vector3D w = new Vector3D(x, y, z);
        Atom a = new Atom(lineIndex[6], w);
        list.add(a);
        // num++;
      } else {

      }
    }
    Atom[] ret = list.toArray(new Atom[list.size()]);
    return ret;
  }

  public List<Number> AlignmentMain(String pdbStr1, String pdbStr2, String base, double lim) throws IOException {
    List<Number> ret = new ArrayList<Number>();
    double dist = 0.0;
    int size = 0;
    Atom[] target1 = pdb2atoms(FileManipulation.fileToString(pdbStr1));
    Atom[] target2 = pdb2atoms(FileManipulation.fileToString(pdbStr2));
    ArrayList<int[]> aligne = Alignment.Alignment(target1, target2, lim);

    while(aligne.get(0)[0] == Integer.MAX_VALUE){
      lim = lim + 0.01;
      aligne = Alignment.Alignment(target1, target2, lim);
      if(lim > 10){
        break;
      }
    }

    if (aligne.get(0)[0] != Integer.MAX_VALUE) {
      List Result1 = decideAlignment(aligne, target1, target2);

      if (Result1.size() != 2) {
        dist = (double)Result1.get(3);
        size = (int)Result1.get(4);
      } else {
        dist = (double)Result1.get(0);
        size = (int)Result1.get(1);
      }
    }else {
      //TODO:ここで何らかの値を返すこと。
      if(aligne.size() != 1){
        List res = decideAlignment(aligne, target1, target2);
        dist = (double)res.get(0);
        size = (int)res.get(1);
      }else{
        dist = 0.0;
        size = 0; 
      }
    }
    ret.add(size);
    ret.add(dist);
    if(ret == null){
     dist = 0.0;
      size = 0;
    }
    return ret;
  }

  private static ArrayList decideAlignment(ArrayList<int[]> aligne, Atom[] comformationA, Atom[] comformationB) {

    int a = aligne.get(0)[0];
    double min = 100;
    int hozon = 0;
    // System.out.println(a+"aiko");
    if (a == 1) {
      Atom[] hozonAtom = comformationA;
      comformationA = comformationB;
      comformationB = hozonAtom;
    }

    Atom[] cA = new Atom[aligne.get(1).length];
    for (int i = 0; i < aligne.get(1).length; i++) {
      cA[i] = comformationA[aligne.get(1)[i]];
    }
    Atom[] cB = new Atom[aligne.get(1).length];
    for (int i = 2; i < aligne.size(); i++) {

      for (int k = 0; k < aligne.get(i).length; k++) {
        cB[k] = comformationB[aligne.get(i)[k]];
      }
      Kabsch kca = null;
      Atom[] ca = copyConfomation(cA);
      Atom[] cb = copyConfomation(cB);
      GetMasses map = new GetMasses();
      kca = new Kabsch(ca, cb, map.getMasses());
      kca.align();
      if (min > kca.getRMSD()) {
        min = kca.getRMSD();
        //	System.out.println(kca.getRMSD());
        hozon = i;
      }

    }

    ArrayList<Integer> hozonA = null;
    ArrayList<Integer> hozonB = null;
    int size = aligne.get(1).length;
    ArrayList<ArrayList<Integer>> aligne2 = new ArrayList<ArrayList<Integer>>(2);

    //Adjustment monossacharide
    double kyori = 0.1 * Math.log10((size) * (size - 1) * (size - 2) * 1.2 / 6) + 1;
    //~2019
    //double kyori = 0.3 * Math.log10((size) * (size - 1) * (size - 2) * 1.2 / 6);
    while (min < kyori && size > 4) {

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
        HashSet setB = new HashSet(copyB);

        for (int i = 0; i < copyA.size(); i++) {
          cA[i] = comformationA[copyA.get(i)];
        }
        for (int i = 0; i < copyB.size(); i++) {
          cB[i] = comformationB[copyB.get(i)];
        }
        Atom[] ca = copyConfomation(cA);
        Atom[] cb = copyConfomation(cB);
        Kabsch kca = null;
        GetMasses map = new GetMasses();
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
      //	kyori = 0.3 * Math.log10(1.2 * (size) * (size - 1) * (size - 2) / 6);
      kyori = 0.1 * Math.log10((size) * (size - 1) * (size - 2) * 1.2 / 6) + 1;
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
    //		System.out.println("Mitei" + size +"_"+kyori+">"+min);
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
      GetMasses map = new GetMasses();
      kca = new Kabsch(ca, cb, map.getMasses());
      kca.align();
      Vector3D mass = kca.getCenterOfMass();
      kca.rotateAtomContainer(comformationB);
      // System.out.println(mass);

      for (int i = 0; i < comformationB.length; i++) {
        Atom atom = comformationB[i];
        atom.Vector3D = new Vector3D(atom.Vector3D.crd[0] + mass.crd[0], atom.Vector3D.crd[1] + mass.crd[1],
            atom.Vector3D.crd[2] + mass.crd[2]);

      }

      ArrayList ret = new ArrayList();
      ret.add(copyConfomation(comformationA));
      ret.add(copyConfomation(comformationB));

      ret2.add(ret);
      //      	System.out.println(size );
      ret2.add(min);
      ret2.add(size);
      return ret2;
    } else {
      //if(size == 3) min = min + 0.1 * Math.log10((4) * (4 - 1) * (4 - 2) * 1.2 / 6) + 1;
      ArrayList ret = new ArrayList();
      ret.add(min);
      ret.add(size);
      return ret;
    }
  }

  private static void convertPDB(List result, String filename) {

    String line = "";

    for (int k = 0; k < 2; k++) {

      Atom[] atoms = (Atom[]) (((ArrayList<Atom[]>) result.get(2)).get(k));

      for (int i = 0; i < atoms.length; i++) {

        line = line + "HETATM";
        line = line + String.format("%5s", (i + 1));
        line = line + String.format("%3s", (atoms[i].symbol)) + "                 ";

        double d = atoms[i].Vector3D.crd[0];
        double d1 = atoms[i].Vector3D.crd[1];
        double d2 = atoms[i].Vector3D.crd[2];
        String x = String.format("%.3f", d);
        String y = String.format("%.3f", d1);
        String z = String.format("%.3f", d2);
        line = line + String.format("%7s %7s %7s", x, y, z);
        line = line + String.format("%24s", (atoms[i].symbol));
        line = line + "\n";
      }
      File clusterFile = new File(filename);
      clusterFile.mkdir();

      try {
        File filePdb1 = new File(filename + k + ".pdb");
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(filePdb1)));

        pw.println(line);

        pw.close();
      } catch (IOException e) {
        System.out.println(e);
      }
      line = "";

    }
    // Date date = new Date();File file1 = new File(base + "output" + ll +
    // "time" +
    // date.getTime()); File clusterFile = new File(base + "output" + ll +
    // "time" +
    // ll);
    // if (clusterFile.renameTo(file1)) {
    // ファイル名変更成功
    // } else {
    // ファイル名変更失敗
    // System.out.println("ファイル名変更失敗");
    // }
    // clusterFile.mkdir();
    return;
  }

  private static Atom[] copyConfomation(Atom[] c) {
    Atom[] ret = new Atom[c.length];
    for (int i = 0; i < c.length; i++) {
      Vector3D v = new Vector3D(c[i].Vector3D.crd[0], c[i].Vector3D.crd[1], c[i].Vector3D.crd[2]);
      ret[i] = new Atom(c[i].symbol, v);
    }
    return ret;
  }

  // クラスタリング

  public static Glycan makeCls(int i, File b) throws IOException, ClassNotFoundException {
    String glyName = b.getName();// b.getPath()のクラスタリング
    String path = b.getPath();
    File file = new File("resultGlycan" + glyName);
    //System.out.println(file);

    Glycan glycan1 = Glycan.setGlycanData(path);// Glycanの生成完了
    // System.out.println("step2");
    DistanceEvaluator evaluator;
    while (true) {
      evaluator = new AverageDistanceEvaluator();// 平均距離法に基づく階層的クラスタリングを準備
      break;
    }
    List<FalseItem> atom2 = new CopyOnWriteArrayList<FalseItem>();
    Set<Short> node = new TreeSet<Short>();
    for (int l = 0; l < glycan1.node.length; l++) {
      node.add(glycan1.node[l]);
    }

    List<Item> atom1 = getVecterList(glycan1, node, atom2);// 局所構造のリストから距離行列の生成

    double[][] table1 = setTableRMSD(glycan1, atom1);
    System.out.println("setTableRMSD...");
    // System.out.println("クラスタ独立の条件値を入力してください！コンマ区切りで繰り返します！");
    // String p = r.readLine();
    double p = 1d;

    ClusterBuilder builder = new ClusterBuilder(evaluator);

    // クラスタリングを実行
    Node result = builder.build(table1, atom2);

    // System.out.println("</pre><P>");
    // クラスタリング結果を表示

    // List<Glycan> clusters = new CopyOnWriteArrayList<Glycan>();
    List<short[]> clusterPdb = new CopyOnWriteArrayList<short[]>();

    List<HashMap<Integer, Vector3D[]>> clusters = new CopyOnWriteArrayList<HashMap<Integer, Vector3D[]>>();
    output_A(glycan1, result, 0, p, clusters, clusterPdb);
    // clusterPdb(glycan1, clusterPdb, atom1);
    return makeNewGlycan(glycan1, clusterPdb, atom1);

  }

  private static Glycan makeNewGlycan(Glycan glycan, List<short[]> clusterPdb, List<Item> atom1) {
    Vector3D[][] allatom = new Vector3D[glycan.symbols.length][clusterPdb.size()];

    for (int clusNum = 0; clusNum < clusterPdb.size(); clusNum++) {
      short[] a = clusterPdb.get(clusNum);
      for (int m = 0; m < glycan.symbols.length; m++) {
        allatom[m][clusNum] = glycan.allatom[m][a[0]];
      }

    }
    glycan.allatom = allatom;
    return glycan;
  }

  private static List<Item> getVecterList(Glycan glycan1, Set<Short> node, List<FalseItem> a) {
    List<Item> atom1 = new CopyOnWriteArrayList<Item>();
    // HashMap<String, Atom[]> atom1 = new HashMap<String, Atom[]>();
    for (short i = 0; i < glycan1.dirList.length; i++) {
      Atom[] atoms = null;
      List<Atom> hinan = new CopyOnWriteArrayList<Atom>();

      for (int d = 0; d < glycan1.node.length; d++) {
        atoms = new Atom[glycan1.node.length];// コードが汚い

        char text = glycan1.symbols[d];
        //	System.out.println(text);
        for (Iterator<Short> k = node.iterator(); k.hasNext();) {
          if (glycan1.node[d] == k.next() && text != 'C' && text != 'H') {
            //System.out.println(String.valueOf(text));
            atoms[d] = new Atom(String.valueOf(text), new Vector3D(glycan1.allatom[d][i].crd[0],
                  glycan1.allatom[d][i].crd[1], glycan1.allatom[d][i].crd[2]));
            if ((Vector3D) glycan1.allatom[d][i] == null) {
              System.out.println("err");
            }
            hinan.add(atoms[d]);

          }
        }
        atoms = new Atom[hinan.size()];
        for (int r = 0; r < hinan.size(); r++)
          atoms[r] = hinan.get(r);

      }
      atom1.add(new Item(i, atoms));
      a.add(new FalseItem(i));
    }
    return atom1;

  }

  private static double[][] setTableRMSD(Glycan glycan1, List<Item> atom1) {
    double[][] kyoriGyouretu = new double[glycan1.dirList.length][glycan1.dirList.length];
    for (int i = 1; i < glycan1.dirList.length; i++) {
      for (int k = 0; k < i; k++) {
        Kabsch kca = null;
        Atom[] _a = atom1.get(i).getVector();
        Atom[] _b = atom1.get(k).getVector();
        GetMasses map = new GetMasses();
        kca = new Kabsch(_b, _a, map.getMasses());
        kca.align();

        kyoriGyouretu[k][i] = kca.getRMSD();
        kyoriGyouretu[i][k] = kyoriGyouretu[k][i];
        // System.out.println(kyoriGyouretu[k][i]);
      }
    }
    return kyoriGyouretu;
  }

  private static void output_A(Glycan glycan1, Node result, int depth, double v,
      List<HashMap<Integer, Vector3D[]>> clusters, List<short[]> clusterPdb) {

    HashMap<Integer, Vector3D[]> a1 = new HashMap<Integer, Vector3D[]>();
    if (result instanceof Cluster) {

      Cluster cluster = (Cluster) result;
      double v1 = (cluster.getLeft().getFurDist() + cluster.getRight().getFurDist())
        / (2 * (cluster.getMinDist()));

      if (depth == 0 || v1 > v || cluster.getRight() == null) {
        output_A(glycan1, cluster.getLeft(), depth + 1, v, clusters, clusterPdb);
        output_A(glycan1, cluster.getRight(), depth + 1, v, clusters, clusterPdb);

      } else {
        clusterPdb.add(cluster.getNames());

        a1 = new HashMap<Integer, Vector3D[]>();
        Vector3D[] path3D;

        for (int atomNum = 0; atomNum < glycan1.symbols.length; atomNum++) {
          Vector3D[] value = glycan1.allatom[atomNum];
          path3D = new Vector3D[cluster.getNames().length];

          for (int k = 0; k < cluster.getNames().length; k++) {
            path3D[k] = value[cluster.getNames()[k]];
          }
          a1.put(atomNum, path3D);

        }
        clusters.add(a1);
      }
    }
  }

}
