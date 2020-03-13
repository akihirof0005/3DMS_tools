package glycan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

import manipulation.FileManipulation;
import manipulation.Vector3D;

public class Glycan implements Serializable {

    public String path; // ex、～/shape/out/xxx.pdb/
    public String kcf;
    public String pdb;
    public short[] node; // 原子番号とノード（単糖の対応表）
    public String[] dirList; // path以下に含まれる.pdbファイルのパス
    public Vector3D[][] allatom; // 原子番号と座標と.pdbファイルのパスの対応表
    public char[] symbols; // 原子番号と原子の名前の対応表

    public Glycan(String path) {
        this.path = path;
    }

    public Glycan() {
    }

    /**
     * 
     * コンストラクターglycanの要素をセットする。
     * <p>
     * <br>
     * </p>
     * 
     * @param pasux 文字列 正規表現を指定する。
     * @return ファイルパス
     * @throws IOException
     */
    public static Glycan setGlycanData(String path) throws IOException {
        Glycan glycan = new Glycan(path);
        File file3 = new File(path);
        // System.out.println(path);
        // File kcf = new File("../tmp/kcf" + File.separator +
        // file3.getName().replaceAll(".pdb", ""));
        // glycan.kcf = FileManipulation.fileToString(kcf.getPath());

        File file2 = new File(path + File.separator + file3.getName());
        glycan.pdb = FileManipulation.fileToString(file2.getPath());
        String[] pathList = FileManipulation.getShapePath(path);
        glycan.dirList = pathList;
        File a = new File(path + File.separator + file3.getName());
        // System.out.println("aa" + a.getPath());
        // HashMap<shorteger, Vector3D[]> h = new HashMap<shorteger,
        // Vector3D[]>();
        // Vector3D[][] h=null;
        short atomNum = 0;
        short nodeNum = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(a));
            String c;
            while ((c = in.readLine()) != null) {
                if (c.startsWith("HETATM")) {
                    // if (c.startsWith("ATOM")) {
                    atomNum++;
                }

                if (c.split("\\s+").length > 6) {
                    nodeNum++;
                }
            }
            in = new BufferedReader(new FileReader(a));
            // ArrayList<shorteger> d = new ArrayList<shorteger>();
            // HashMap<shorteger, Character> d1 = new HashMap<shorteger,
            // Character>();
            nodeNum = atomNum;
            // System.out.println("node_count:" + nodeNum);
            // System.out.println("atom_count:" + atomNum);
            short[] d = new short[nodeNum];
            char[] d1 = new char[atomNum];
            atomNum = 0;
            nodeNum = 0;

            while ((c = in.readLine()) != null) {
                if (c.startsWith("HETATM")) {

                    d1[atomNum] = (c.split("\\s+")[10]).toCharArray()[0];
                    d[nodeNum] = 1;
                    // System.out.println(d1[atomNum]);
                    // System.out.println(d1[nodeNum]);
                    nodeNum++;
                    atomNum++;

                }

                // if (c.split("\\s+").length > 6) {

                // }
                glycan.node = d; // ノード情報をセット
                glycan.symbols = d1; // 原子情報をセット
            }
            in.close();
        } catch (IOException e) { // 入出力エラーをつかまえる
            System.err.println(e + "error"); // エラーメッセージ出力
            System.exit(1); // 終了コード 1 で終了する
        }
        Vector3D[][] h = new Vector3D[atomNum][pathList.length];
        for (short i = 0; i < pathList.length; i++) {
            File f = new File(path + pathList[i]);
            try {
                BufferedReader in = new BufferedReader(new FileReader(f));
                String c = new String();
                while ((c = in.readLine()) != null) {
                    if (c.startsWith("ATOM")) {
                        double x = Double.parseDouble(c.split("\\s+")[5]);
                        double y = Double.parseDouble(c.split("\\s+")[6]);
                        double z = Double.parseDouble(c.split("\\s+")[7]);
                        Vector3D w = new Vector3D(x, y, z);

                        h[Short.parseShort(c.split("\\s+")[1].trim()) - 1][i] = w;
                    }
                }
                in.close();
            } catch (IOException e) { // 入出力エラーをつかまえる
                System.err.println(e); // エラーメッセージ出力
                System.exit(1); // 終了コード 1 で終了する
            }
        }
        glycan.allatom = h;
        return glycan;
    }
}
