package manipulation;

import glycan.Atom;
import glycan.NodeAtom;

import java.util.ArrayList;

public class CombinationGenerator {
    final int n;
    long x;

    public  CombinationGenerator(int n, int k) {
        this.n = n;
        x = lowBit(k);

    }

    public long lowBit(int n) {
        return (1L << n) - 1;
    }

    public String toString() {
        String s = "";
        long y = x;
        for (int i = 1; i <= n; i++) {
            if ((y & 1) == 1)
                s += i + "_";
            y >>>= 1;
        }
        return s;
    }

    public boolean next() {
        long smallest = x & -x;
        long ripple = x + smallest;
        long new_smallest = ripple & -ripple;
        long ones = ((new_smallest / smallest) >> 1) - 1;
        x = ripple | ones;
        return (x & ~lowBit(n)) == 0;
    }

    static ArrayList<int[]> subSet(ArrayList<Integer> list) {
        CombinationGenerator c = new CombinationGenerator(list.size(), 3);
        ArrayList<int[]> ret = new ArrayList<int[]>();
        do {
            String[] array = c.toString().split("_");
            int[] array2 = new int[array.length];
            for (int i = 0; i < array2.length; i++) {
                array2[i] = list.get(Integer.parseInt(array[i]) - 1);
            }
            ret.add(array2);
        } while (c.next());
        return ret;
    }
    public static ArrayList<Atom[]> subSet(Atom[] list ,int num) {
        CombinationGenerator c = new CombinationGenerator(list.length, num);
        ArrayList<Atom[]> ret = new ArrayList<Atom[]>();
        do {
            String[] array = c.toString().split("_");
            Atom[] array2 = new Atom[array.length];
            for (int i = 0; i < array2.length; i++) {
                array2[i] = list[Integer.parseInt(array[i]) - 1];
            }
            ret.add(array2);
        } while (c.next());
        return ret;
    }
    public static ArrayList<NodeAtom[]> subSet(NodeAtom[] list ,int num) {
        if(list.length<num){return null;}
        CombinationGenerator c = new CombinationGenerator(list.length, num);
        ArrayList<NodeAtom[]> ret = new ArrayList<NodeAtom[]>();
        do {
            String[] array = c.toString().split("_");
            NodeAtom[] array2 = new NodeAtom[array.length];
            for (int i = 0; i < array2.length; i++) {
                array2[i] = list[Integer.parseInt(array[i]) - 1];
            }
            ret.add(array2);
        } while (c.next());
        return ret;
    }
    static ArrayList<int[]> subSet(int[] list) {
        CombinationGenerator c = new CombinationGenerator(list.length, 3);
        ArrayList<int[]> ret = new ArrayList<int[]>();
        do {
            String[] array = c.toString().split("_");
            int[] array2 = new int[array.length];
            for (int i = 0; i < array2.length; i++) {
                array2[i] = list[Integer.parseInt(array[i]) - 1];
            }
            ret.add(array2);
        } while (c.next());
        return ret;
    }
}
