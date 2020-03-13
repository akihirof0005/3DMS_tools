package cluster;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * クラスタリングによって形成される階層ノードです。
 */

public interface Node extends Serializable {
    /**
     * ノードに含まれる全てのベクトルを返します。
     * 
     * @return ベクトルのリスト
     */
    short[] getNames();

    double getMinDist();

    double getFurDist();

}
