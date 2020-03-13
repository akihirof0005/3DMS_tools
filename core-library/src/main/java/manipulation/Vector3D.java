package manipulation;

import java.io.Serializable;

import manipulation.Vector3D;

/**
 * 3次のベクトル計算.
 * 
 * @author rings:akihiro
 * 
 */

public class Vector3D implements Serializable {
    public double[] crd; // (x,y,z)：座標(coordinate)

    // コンストラクタ #0
    public Vector3D() {
        crd = new double[3];
        crd[0] = 0.0d;
        crd[1] = 0.0d;
        crd[2] = 0.0d;
    }

    public Vector3D(double x, double y, double z) {
        crd = new double[3];
        crd[0] = x;
        crd[1] = y;
        crd[2] = z;
    }

    // ベクトルの正規化
    public static Vector3D norm(Vector3D v0) {
        double norm;
        norm = 0.0f;
        for (int i = 0; i < 3; i++) {
            norm = norm + v0.crd[i] * v0.crd[i];
        }
        norm = (double) Math.sqrt((double) norm);
        for (int i = 0; i < 3; i++) {
            v0.crd[i] = v0.crd[i] / norm;
        }
        return v0;
    }

    // 逆ベクトル
    public static Vector3D mainasu(Vector3D v0) {
        for (int i = 0; i < 3; i++) {
            v0.crd[i] = v0.crd[i] * -1;
        }
        return v0;
    }

    // 和： v2 = v0 + v1
    public static void add(Vector3D v0, Vector3D v1, Vector3D v2) {
        for (int i = 0; i < 3; i++) {
            v2.crd[i] = v0.crd[i] + v1.crd[i];
        }
    }

    // 差： v2 = v0 - v1
    public static void sub(Vector3D v0, Vector3D v1, Vector3D v2) {
        for (int i = 0; i < 3; i++) {
            v2.crd[i] = v0.crd[i] - v1.crd[i];
        }
    }

    // 内積： v0 ・ v1
    public static double scprod(Vector3D v0, Vector3D v1) {
        double prod;
        prod = 0.0f;
        for (int i = 0; i < 3; i++) {
            prod = prod + v0.crd[i] * v1.crd[i];
        }
        return prod;
    }

    // 外積： v2 = v0 X v1

    public static void veprod(Vector3D v0, Vector3D v1, Vector3D v2) {
        v2.crd[0] = v0.crd[1] * v1.crd[2] - v1.crd[1] * v0.crd[2];
        v2.crd[1] = v0.crd[2] * v1.crd[0] - v1.crd[2] * v0.crd[0];
        v2.crd[2] = v0.crd[0] * v1.crd[1] - v1.crd[0] * v0.crd[1];
    }

}
