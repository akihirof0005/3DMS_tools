package manipulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FileManipulation {

    static String crlf = System.getProperty("line.separator");
    static String ll = File.separator;

    public static String getShapeHomePath(String filename) {
        String homePath = new File(".").getAbsoluteFile().getParent();
        System.out.println(homePath);
        // homePath = homePath + ll + ".." + ll + ".." + ll + "shape" + ll +
        // "out" + ll;
        homePath = homePath + ll + ".." + ll + "shape" + ll + filename + ll;
        return homePath;
    }

    public static String fileToString(String filePath) throws IOException {
        String fileData = new String();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(
                    filePath)));
            String line = null;
            while ((line = br.readLine()) != null) {
                fileData = fileData + crlf + line;
            }
        } finally {
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                }
        }
        return fileData;
    }

    public static String fileToString(File f) throws IOException {
        BufferedReader br = null;
        String a;
        try {
            br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(f)));
            StringBuffer sb = new StringBuffer();
            int c;
            while ((c = br.read()) != -1) {
                sb.append((char) c);
            }
            a = sb.toString();
        } finally {
            br.close();
        }
        return a;
    }

    /**
     * ひとつ下層のサブファイルの取得。.
     * 
     * @return ファイルパス
     */
    public static FileFilter getNotDirFileFilter() {
        return new FileFilter() {
            public boolean accept(File file) {
                return file.isFile();
            }
        };
    }

    /**
     * 
     * パス^hape/out/XXX.pdb/を引数に下層にある.pdbファイルのパスを返す。
     * <p>
     * <br>
     * </p>
     * 
     * @param pasux
     *            文字列 正規表現を指定する。
     * @return ファイルパス
     */
    public static String[] getShapePath(String path) {

        File dir = new File(path + ll + "cluster.result");
        File[] a = dir.listFiles(getFileRegexFilter("cluster.[0-9]*"));
        int glyNum = 0;
        for (int i = 0; i < a.length; i++) {
            String[] b = new File(a[i].getPath() + ll + "pdb")
                    .list(getFileExtensionFilter(".pdb"));
            for (int i2 = 0; i2 < b.length; i2++) {
                if (b[i2] != null) {
                    glyNum++;
                } else {
                    System.out.println("err!");
                }
            }
        }
        String[] list2 = new String[glyNum];
        glyNum = 0;
        for (int i = 0; i < a.length; i++) {
            String[] b = new File(a[i].getPath() + ll + "pdb")
                    .list(getFileExtensionFilter(".pdb"));
            for (int i2 = 0; i2 < b.length; i2++) {
                if (b[i2] != null) {
                    list2[glyNum] = (a[i].getPath() + ll + "pdb" + ll + b[i2])
                            .replace(path, "");
                    glyNum++;
                }
            }
        }
        return list2;
    }

    /**
     * 指定する拡張子（.***）であるファイルだけを取得する。.
     * 
     * @param extension
     *            文字列 拡張子の指定。
     * @return ファイルパス
     */
    public static FilenameFilter getFileExtensionFilter(String extension) {
        final String _extension = extension;
        return new FilenameFilter() {
            public boolean accept(File file, String name) {
                boolean ret = name.endsWith(_extension);
                return ret;
            }
        };

    }

    /**
     * ファイル名が指定する正規表現のパターンにマッチするファイルを取得する。.
     * <p>
     * <br>
     * </p>
     * 
     * @param regex
     *            文字列 正規表現を指定する。
     * @return ファイルパス
     */
    public static FilenameFilter getFileRegexFilter(String regex) {
        final String regex_ = regex;
        return new FilenameFilter() {
            public boolean accept(File file, String name) {
                boolean ret = name.matches(regex_);
                return ret;
            }
        };
    }
}
