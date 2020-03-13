package glycan;

import java.io.Serializable;

public  class NodeAtom implements Serializable {
    public Atom atom;
    public int number;

    public NodeAtom(int a, Atom b) {
        atom = b;
        number = a;
    }
}
