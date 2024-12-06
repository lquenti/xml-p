import Trees.Mul;

public class MyMul extends Mul {
    public long evaluate() {
        Object uncasted_left = this.getAddOrSubOrMul().get(0);
        Object uncasted_right = this.getAddOrSubOrMul().get(1);

        return MyHelper.evaluate_single(uncasted_left) * MyHelper.evaluate_single(uncasted_right);
    }

}
