import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class WaterGraph {
    private HashMap<String, Pair<Water, HashSet<String>>> xss;
    public WaterGraph(
            ArrayList<Water> waters
    ) {
        this.xss = new HashMap<>();

        // one-directional
        for (Water w : waters) {
            xss.put(w.id, Pair.createPair(w, new HashSet<>()));
            if (w.into_id != null) {
                xss.get(w.id).getSecond().add(w.into_id);
            }
        }
        this.invertEdges();
    }

    private void invertEdges() {
        HashMap<String, Pair<Water, HashSet<String>>> invertedXss = new HashMap<>();

        // add empty nodes
        for (String id : xss.keySet()) {
            Water originalWater = xss.get(id).getFirst();
            invertedXss.put(id, Pair.createPair(originalWater, new HashSet<>()));
        }

        // add inverted edges
        for (String fromId : xss.keySet()) {
            for (String toId : xss.get(fromId).getSecond()) {
                invertedXss.get(toId).getSecond().add(fromId);
            }
        }

        this.xss = invertedXss;
    }

    public float getNetworkOfWater(String id) {
        float ret = 0.0f;
        var pair = this.xss.get(id);
        System.out.println(pair.getFirst());
        ret += pair.getFirst().getLength();
        for (String key : pair.getSecond()) {
            ret += this.getNetworkOfWater(key);
        }
        return ret;
    }
}