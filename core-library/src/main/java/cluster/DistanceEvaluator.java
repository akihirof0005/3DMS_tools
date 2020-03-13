package cluster;

/**
 * 
 * ノード間の距離関数を示すインターフェイスです。
 */
public interface DistanceEvaluator {

    /**
     * ノード間の距離を計算します。
     * 
     * @param table
     * @param n1
     *            ノード1
     * @param n2
     *            ノード2
     * @return 距離
     */
    double[] distance(double[][] table, Node n1, Node n2);
}
