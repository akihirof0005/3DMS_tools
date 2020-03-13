package cluster;

/**
 * 末端のデータ項目に対応するノードです。
 */
public class FalseItem implements Node {
    private short[] name = new short[1];

    /**
     * ノードを作成します。
     * 
     * @param name
     *            ノード名
     * @param vector
     *            ベクトル
     */
    public FalseItem(short name) {
        this.name[0] = name;
    }

    /**
     * ノード名を取得します。
     * 
     * @return ノード名
     */

    public short[] getNames() {
        return name;
    }

    public double getFurDist() {
        return 0;
    }

    public double getMinDist() {
        return 0;
    }
}
