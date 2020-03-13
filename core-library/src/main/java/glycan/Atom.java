package glycan;

import java.io.Serializable;

import manipulation.Vector3D;

public class Atom implements Serializable {

    public String symbol;
    public manipulation.Vector3D Vector3D;

    public Atom(String valueOf, Vector3D vector3d2) {
        symbol = valueOf;
        Vector3D = vector3d2;
    }
}
