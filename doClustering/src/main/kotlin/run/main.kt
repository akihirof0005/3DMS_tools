package run 

import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.PrintWriter
import java.io.BufferedWriter
import java.io.IOException

import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import java.util.TreeSet
import java.util.concurrent.CopyOnWriteArrayList

import manipulation.Vector3D

import glycan.Atom
import glycan.GetMasses
import glycan.Glycan
import glycan.Kabsch

import cluster.AverageDistanceEvaluator
import cluster.Cluster
import cluster.ClusterBuilder
import cluster.DistanceEvaluator
import cluster.FalseItem
import cluster.Item
import cluster.Node

import sskit_core.Run

fun main(args:Array<String>) {


	val basePath = System.getProperty("user.home") + "/resarch/extractData/out"
    val out = "/out"
    
    var list: MutableList<String> = mutableListOf()
    File( basePath ).listFiles().forEach{ 
        if(it.nameWithoutExtension.startsWith("shape")) {
            for( f in File(it.path+  "/shape/out" ).listFiles()){
                if(f.path.endsWith(".pdb")){
                println(f.path)
                    list.add( f.path )
                }
            }
        }
    }
    
    var index = 0
    var size = list.size
    list.parallelStream().forEach{
        println("**************" + (index++) + "/" + size)
        
        println(it)
        val base = it
	    val dir = File( base )

	    var glycan:Glycan = Glycan()
	    try {
            glycan = Run.makeCls(0, dir)  
        } catch (e:Exception) {
            e.printStackTrace()
        }
	
        //save pdb file
        val index = glycan.allatom[0].size -1
        for (i in 0..index){
            File( base + out).mkdirs()
            val f = FileWriter(base + out +  "/${i}.pdb")
            val pw = PrintWriter(BufferedWriter(f))
            var file = ""
            for (c in glycan.symbols.indices){
                //file = file + "HETATM"
		        file =( file + "ATOM" 
                        + "%7s".format(c + 1) 
                        + "%3s".format(glycan.symbols[c])  
                        + "                 " 
                        + "%7s %7s %7s".format(glycan.allatom[c][i].crd[0],
                                           glycan.allatom[c][i].crd[1],
                                           glycan.allatom[c][i].crd[2])
                        + "%24s".format(glycan.symbols[c]) 
                        + "\n")
            }
            pw.println(file)
            pw.close()
        }
    }
}