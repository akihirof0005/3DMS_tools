# sskit-core-library

## Build library
$ gradle makJar  
find your sskit-core-library in ./build/libs/

## Reference

Method  
Run.Alignment(String para1,String para2,String para3,double para4)  
or  
Run run = new Run();
run.Alignment(String para1,String para2,String para3,double para4)  
para1 : the path of pdb file.  
para2 : the path of pdb file.  
para3 : the directory of output file.  
para4 : value of scattering for matching length between atom and atom.    

Example, For pare-wise alignment.  
```java
import sskit_core.Run;

public class main {
	public static void main(String[] args) {
        String outPath = "";
        String path1 = "";
        String path2 = "";
        
		try {
			Run.AlignmentMain(path1, path2, outPath, 0.01);
		} catch (IOException e) {
			e.printStackTrace();
        }
    }
}

```
Example, use parallelStream().
```java
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sskit_core.Run;

public class main {
	public static void main(String[] args) {
		String outPath = System.getProperty("user.home") + "/~~~/";
		String path1 = System.getProperty("user.home") + "/~~~/";
        String path2 = System.getProperty("user.home") + "/~~~/";

		File dir1 = new File(path1);
		String f1[] = dir1.list();
		File dir2 = new File(path2);
		String f2[] = dir2.list();

		List<String[]> dataSet = new ArrayList<String[]>();
		for (int i = 0; i < f1.length; i++) {
			for (int k = 0; k < f2.length; k++) {
				String data1 = path1 + f1[i];
				String data2 = path2 + f2[k];
				String[] data = {data1,data2};
				dataSet.add(data);
			}
		}

		dataSet.parallelStream().forEach(data -> {
				Run run = new Run();
				try {
					run.AlignmentMain(data[0], data[1], outPath, 0.01);
				} catch (IOException e) {
					e.printStackTrace();
				}
		} );
	}
}

```