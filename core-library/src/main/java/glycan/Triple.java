package glycan;


public class Triple {

    public NodeAtom NAs[] = new NodeAtom[3];
    public double[] distance = new double[3];

    public Triple(NodeAtom e1, NodeAtom e2, NodeAtom e3, int lim) {
        double[] d = new double[3];
        NAs[0] = e1;
        NAs[1] = e2;
        NAs[2] = e3;
        for (int i = 0; i < 3; i++) {
            d[i] = e1.atom.Vector3D.crd[i]
                    - e2.atom.Vector3D.crd[i];
        }
        distance[0] = Math.sqrt(d[0] * d[0] + d[1] * d[1] + d[2] * d[2]);

        for (int i = 0; i < 3; i++) {
            d[i] = e2.atom.Vector3D.crd[i]
                    - e3.atom.Vector3D.crd[i];
        }
        distance[1] = Math.sqrt(d[0] * d[0] + d[1] * d[1] + d[2] * d[2]);
        for (int i = 0; i < 3; i++) {
            d[i] = e3.atom.Vector3D.crd[i]
                    - e1.atom.Vector3D.crd[i];
        }
        distance[2] = Math.sqrt(d[0] * d[0] + d[1] * d[1] + d[2] * d[2]);

        for (int i = 0; i < 3; i++) {
            if (distance[i] > lim) {
                distance = null;
                NAs = null;
                break;
            }

        }
    }
    public String getName() {
        String ret = NAs[0].number+"_"+NAs[1].number+"_"+NAs[2].number;
        return ret;
    }

}
