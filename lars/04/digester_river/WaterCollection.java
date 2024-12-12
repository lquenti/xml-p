import java.util.ArrayList;

public class WaterCollection {
    public ArrayList<Water> waters = new ArrayList<>();
    public void addSea(Sea s) {
        waters.add(s);
    }
    public void addLake(Lake l) {
        waters.add(l);
    }
    public void addRiver(River r) {
        waters.add(r);
    }
}
