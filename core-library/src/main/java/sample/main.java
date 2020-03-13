package sample;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

import manipulation.Vector3D;

import glycan.Atom;
import glycan.GetMasses;
import glycan.Glycan;
import glycan.Kabsch;

import manipulation.Vector3D;
import cluster.AverageDistanceEvaluator;
import cluster.Cluster;
import cluster.ClusterBuilder;
import cluster.DistanceEvaluator;
import cluster.FalseItem;
import cluster.Item;
import cluster.Node;

import sskit_core.Run;

public class main {
	public static void main(String[] srg) {
	//	try{
//				try {
//			Run.pdb2atoms( System.getProperty("user.home") + "/b-D-Neup5Gc.pdb");
//	} catch (IOException e) {
//				e.printStackTrace();
//			}
		
		
//		}
//	public static void backup(String[] srg) {
		// String str = "error";
		String outPath = System.getProperty("user.home") + "/out/";
		String path1 = System.getProperty("user.home")
				+ "/resarch/extractData/out/shape_a-6-deoxy-D-Gulp.pdb/shape/out/a-6-deoxy-D-Gulp.pdb/out/";
		String path2 = System.getProperty("user.home")
				+ "/resarch/extractData/out/shape_a-D-Bacp.pdb/shape/out/a-D-Bacp.pdb/out/";

		File dir1 = new File(path1);
		String f1[] = dir1.list();
		File dir2 = new File(path2);
		String f2[] = dir2.list();

		List<String[]> dataSet = new ArrayList<String[]>();
		for (int i = 0; i < f1.length; i++) {
			for (int k = 0; k < f2.length; k++) {
				String data1 = path1 + f1[i];
				String data2 = path2 + f2[k];
				String[] data = { data1, data2 };
				dataSet.add(data);
			}
		}

    System.out.println(path1 + "-" + path2);
		dataSet.parallelStream().forEach(data -> {
      List distance = null;
			Run run = new Run();
			try {
				distance = run.AlignmentMain(data[0], data[1], outPath, 5);
			} catch (IOException e) {
				e.printStackTrace();
			}
      if( !Double.isNaN((double)distance.get(0)) ){ System.out.println((double)distance.get(0));}
		});
	}

	public static void clustering(String[] srg) {
		File dir = new File(System.getProperty("user.home")
				+ "/resarch/extractData/out/shape_[][a-D-Glcp]{}.pdb/shape/out/[][a-D-Glcp]{}.pdb/");
		Glycan glycan = new Glycan();
		try {
			glycan = Run.makeCls(0, dir);
		} catch (Exception e) {
			System.out.println("error");
		}
		System.out.println(glycan.allatom[1].length);
	}
}
