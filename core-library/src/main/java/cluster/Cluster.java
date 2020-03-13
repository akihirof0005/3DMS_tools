package cluster;

/**
 * クラスタに対応するノードです。
 */

public class Cluster implements Node {
    private Node left;
    private Node right;
    private double max;
    private double minDist;
    private short[] cachedNames;

    /**
     * ノードを作成します。
     * 
     * @param left
     *            子ノード(左)
     * @param right
     *            子ノード(右)
     */
    public Cluster(Node left, Node right) {
        this.left = left;
        this.right = right;
    }

    /**
     * 子ノード(左)を取得します。
     * 
     * @return ノード
     */
    public Node getLeft() {
        return left;
    }

    /**
     * 子ノード(右)を取得します。
     * 
     * @return ノード
     */
    public Node getRight() {
        return right;
    }

    public short[] getNames() {
        // 高速化のため結果をキャッシュする
        if (cachedNames == null) {
            cachedNames = new short[left.getNames().length
                    + right.getNames().length];
            // leftノードとrightノードのベクトル集合を連結
            System.arraycopy(left.getNames(), 0, cachedNames, 0,
                    left.getNames().length);
            System.arraycopy(right.getNames(), 0, cachedNames,
                    left.getNames().length, right.getNames().length);
        }
        return cachedNames;
    }

    public double getFurDist() {
        return max;
    }

    public void setFurDist(double max) {
        this.max = max;
    }

    public double getMinDist() {
        return minDist;
    }

    public void setMinDist(double minDist) {
        this.minDist = minDist;
    }

}
