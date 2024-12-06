import Trees.Sub;

public class MySub extends Sub {
    public long evaluate() {
        Object uncasted_left = this.getAddOrSubOrMul().get(0);
        Object uncasted_right = this.getAddOrSubOrMul().get(1);

        return MyHelper.evaluate_single(uncasted_left) - MyHelper.evaluate_single(uncasted_right);
    }

    public String expression() {
        Object uncasted_left = this.getAddOrSubOrMul().get(0);
        Object uncasted_right = this.getAddOrSubOrMul().get(1);

        String left = MyHelper.expression_single(uncasted_left);
        String right = MyHelper.expression_single(uncasted_right);

        // Add parentheses around sub-expressions for clarity
        return "(" + left + " - " + right + ")";
    }
    public String tree() {
        Object uncasted_left = this.getAddOrSubOrMul().get(0);
        Object uncasted_right = this.getAddOrSubOrMul().get(1);

        String left = MyHelper.tree_single(uncasted_left);
        String right = MyHelper.tree_single(uncasted_right);

        // Create an HTML table for this node with two children
        return "<table border='1'><tr><td colspan='2'>-</td></tr>" +
                "<tr><td>" + left + "</td><td>" + right + "</td></tr></table>";
    }
}
