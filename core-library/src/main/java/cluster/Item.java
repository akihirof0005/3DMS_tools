package cluster;

import java.io.Serializable;

import glycan.Atom;

/**
 * 末端のデータ項目に対応するノードです。
 */
public class Item implements Serializable {
    private int name;
    private Atom[] vector;

    /**
     * ノードを作成します。
     * 
     * @param name
     *            ノード名
     * @param vector
     *            ベクトル
     */
    public Item(int name, Atom[] vector) {
        this.name = name;
        this.vector = vector;
    }

    /**
     * ノード名を取得します。
     * 
     * @return ノード名
     */
    public int getName() {
        return name;
    }

    /**
     * ベクトルを取得します。
     * 
     * @return ベクトル
     */
    public Atom[] getVector() {
        return vector;
    }
}
