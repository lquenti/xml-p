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

    public static String expression_single(Object uncasted) {
        if (uncasted instanceof Long) {
            return String.valueOf(uncasted);
        } else if (uncasted instanceof Add) {
            return ((MyAdd)uncasted).expression();
        } else if (uncasted instanceof Sub) {
            return ((MySub)uncasted).expression();
        } else if (uncasted instanceof Mul) {
            return ((MyMul)uncasted).expression();
        } else { // div
            return ((MyDiv)uncasted).expression();
        }
    }

    public static String tree_single(Object uncasted) {
        if (uncasted instanceof Long) {
            return "<tr><td>" + uncasted + "</td></tr>";
        } else if (uncasted instanceof Add) {
            return ((MyAdd)uncasted).tree();
        } else if (uncasted instanceof Sub) {
            return ((MySub)uncasted).tree();
        } else if (uncasted instanceof Mul) {
            return ((MyMul)uncasted).tree();
        } else { // div
            return ((MyDiv)uncasted).tree();
        }
    }
}
