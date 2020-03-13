package glycan;

import java.util.Map;
import java.util.TreeMap;

public class GetMasses {

    public Map<String, Double> Masses;

    public Map<String, Double> getMasses() {
        Masses = new TreeMap<String, Double>();

        Masses.put("H", 1.007825);
        Masses.put("C", 12.000000);
        Masses.put("N", 14.003074);
        Masses.put("O", 15.994915);
        Masses.put("F", 18.998403);
        Masses.put("Na", 22.989770);
        Masses.put("Si", 27.976928);
        Masses.put("P", 30.973763);
        Masses.put("S", 31.972072);
        Masses.put("Cl", 34.968853);
        Masses.put("K", 38.963708);

        return Masses;

    }
}
