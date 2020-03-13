package cluster;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 階層的クラスタリングを実行するクラスです。
 */
public class ClusterBuilder {
    private DistanceEvaluator distanceEvaluator;

    /**
     * 指定された距離関数に基づいてビルダを作成します。
     * 
     * @param distanceEvaluator 距離関数
     */
    public ClusterBuilder(DistanceEvaluator distanceEvaluator) {
        this.distanceEvaluator = distanceEvaluator;
    }

    /**
     * クラスタリングを実行し、ノード階層を構築します。
     * 
     * @param table
     * @param nodes クラスタリング対象のノードリスト
     * @return クラスタリング結果の最上位ノード
     */
    public Node build(double[][] table, List<? extends Node> nodes) {
        long start = System.currentTimeMillis();
        // ノードが1つに集約されるまで繰り返す
        int max = nodes.size();
        int count = 1;
        while (nodes.size() > 1) {
            long stop = System.currentTimeMillis();
           // if ((int) (nodes.size() * 0.1) * 10 == nodes.size()) {
             //   System.out.println(count + "/" + nodes.size());
            //}
            count++;
            Node merge1 = null;
            Node merge2 = null;
            double minDist = Double.MAX_VALUE;
            double furDist = 0;
            // 距離が最小となるノード対を探す
            for (int i = 0; i < nodes.size(); i++) {
                Node n1 = (Node) nodes.get(i);
                for (int j = i + 1; j < nodes.size(); j++) {
                    Node n2 = nodes.get(j);
                    double[] dist = distanceEvaluator.distance(table, n1, n2);
                    if (dist[0] < minDist) {

                        minDist = dist[0];
                        furDist = dist[1];

                        merge1 = n1;
                        merge2 = n2;
                    }

                }
            }
            // 次ステップ用のノードリストを作成

            List<Node> nextNodes = new CopyOnWriteArrayList<Node>();

            for (Node node : nodes) {
                // 統合対象にならなかったノードを追加
                if (node != merge1 && node != merge2) {
                    nextNodes.add(node);
                }
            }
            // 統合対象のノード対をクラスタ化して追加
            Cluster newClus = new Cluster(merge1, merge2);
            newClus.setFurDist(furDist);
            newClus.setMinDist(minDist);
            nextNodes.add(newClus);
            nodes = nextNodes;

        }
        long stop = System.currentTimeMillis();
        // System.out.println((int) (((stop - start) / 1000 / 60 / 60)) + "時間" + (int)
        // (((stop - start) / 1000) / 60 % 60)
        // + "分かかった。");
        return nodes.get(0);
    }

}
