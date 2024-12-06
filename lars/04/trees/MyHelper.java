import Trees.Add;
import Trees.Mul;
import Trees.Sub;

public class MyHelper {
    public static long evaluate_single(Object uncasted) {
        if (uncasted instanceof Long) {
            return (Long)uncasted;
        } else if (uncasted instanceof Add) {
            return ((MyAdd)uncasted).evaluate();
        } else if (uncasted instanceof Sub) {
            return ((MySub)uncasted).evaluate();
        } else if (uncasted instanceof Mul) {
            return ((MyMul)uncasted).evaluate();
        } else { // div
            return ((MyDiv)uncasted).evaluate();
        }
    }
}
