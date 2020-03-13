package cluster;

/**
 * 最短距離法に基づく距離関数の実装です。
 * 
 */

public class NearestDistanceEvaluator implements DistanceEvaluator {
    public double[] distance(double[][] table, Node n1, Node n2) {
        double[] DistSq = { Double.MAX_VALUE, 0 };
        // double[] DistSq =
        // {table[n1.getNames()[0]][n2.getNames()[0]],table[n1.getNames()[0]][n2.getNames()[0]]
        // };
        for (int v1 = 0; v1 < n1.getNames().length; v1++) {
            for (int v2 = 0; v2 < n2.getNames().length; v2++) {
                System.out.println(v2);
                // 全てのベクトルの組み合わせに対して距離を計算
                double distSq = table[n1.getNames()[v1]][n2.getNames()[v2]];
                if (distSq < DistSq[0]) {
                    DistSq[0] = distSq;
                }
                if (distSq > DistSq[1]) {
                    DistSq[1] = distSq;
                }
            }
        }
        return DistSq;
    }
}
