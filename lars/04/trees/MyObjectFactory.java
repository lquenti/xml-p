import Trees.*;

public class MyObjectFactory extends ObjectFactory {
    @Override
    public Tree createTree() {
        return new MyTree();
    }
    @Override
    public Add createAdd() {
        return new MyAdd();
    }
    @Override
    public Sub createSub() {
        return new MySub();
    }
    @Override
    public Mul createMul() {
        return new MyMul();
    }
    @Override
    public Div createDiv() {
        return new MyDiv();
    }
}
