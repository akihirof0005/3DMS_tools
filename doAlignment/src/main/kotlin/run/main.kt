package run 
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.BufferedWriter

import sskit_core.Run
import manipulation.FileManipulation
import com.google.gson.*

fun main(args:Array<String>) {

  var path = System.getProperty("user.home") + "/resarch/extractData/out/"
  val fList = File(path).listFiles().sorted()
  var file = ""

  for (i in fList.indices) {
    for(k in fList.indices){
      if(i<k){
        val name1 = fList[i].nameWithoutExtension +".pdb"
        val n1 = name1.split("_")[1]

        val name2 = fList[k].nameWithoutExtension +".pdb"
        val n2 = name2.split("_")[1]

        println("${n1.split(".")[0]}_${n2.split(".")[0]}")
        if(File(System.getProperty("user.home") + "/resarch/dist/${n1.split(".")[0]}_${n2.split(".")[0]}.json").exists()){
          continue
        }

        val dir1 = File(System.getProperty("user.home") + "/resarch/extractData/out/${name1}/shape/out/${n1}/out/")
        var list1: MutableList<String> = mutableListOf()
        dir1.listFiles().forEach{list1.add(it.path)}

        val dir2 = File(System.getProperty("user.home") + "/resarch/extractData/out/${name2}/shape/out/${n2}/out/")
        var list2: MutableList<String> = mutableListOf()
        dir2.listFiles().forEach{list2.add(it.path)}

        var list: MutableList<List<String>> = mutableListOf()

        for ( l1 in list1) { for ( l2 in list2) { 
          //list.add(listOf(FileManipulation.fileToString(l1),FileManipulation.fileToString(l2)))
          list.add(listOf(l1,l2))
        }}

        val listDist: ArrayList<List<Number>> = arrayListOf()
        var num = 0

        list.parallelStream().forEach{
          val run = Run()
          listDist.add( run.AlignmentMain(it[0],it[1],System.getProperty("user.home") +"/resarch/out_norm/${n1}_${n2}/",0.1) )
        }

        val gson = Gson()
        val dis: String = gson.toJson(listDist)
        val fil = FileWriter( File(System.getProperty("user.home") + "/resarch/dist/${n1.split(".")[0]}_${n2.split(".")[0]}.json"))
        val pw = PrintWriter(BufferedWriter(fil))
        pw.println(dis)
        pw.close()
      }
    }
  }
  println("Done!")
}
