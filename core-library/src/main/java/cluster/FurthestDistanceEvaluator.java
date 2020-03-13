package cluster;

/**
 * 最長距離法に基づく距離関数の実装です。
 * 
 */

public class FurthestDistanceEvaluator implements DistanceEvaluator {
    public double[] distance(double[][] table, Node n1, Node n2) {
        double[] maxDistSq = { 0, 0 };
        int a;
        int b;
        int c;
        for (int v1 = 0; v1 < n1.getNames().length; v1++) {
            for (int v2 = 0; v2 < n2.getNames().length; v2++) {
                // 全てのベクトルの組み合わせに対して距離を計算
                a = n1.getNames()[v1];
                b = n2.getNames()[v2];
                if (b < a) {
                    c = a;
                    a = b;
                    b = c;
                }
                double distSq = Math.sqrt(table[a][b]);
                if (distSq > maxDistSq[0]) {
                    maxDistSq[0] = distSq;
                    maxDistSq[1] = distSq;
                }
            }
        }
        return maxDistSq;
    }
}
