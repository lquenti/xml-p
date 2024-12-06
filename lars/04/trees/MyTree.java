import Trees.Tree;

public class MyTree extends Tree {
    public long evaluate() {
        if (this.getAdd() != null) {
            return ((MyAdd)this.getAdd()).evaluate();
        } else if (this.getSub() != null) {
            return ((MySub)this.getSub()).evaluate();
        } else if (this.getMul() != null) {
            return ((MyMul)this.getMul()).evaluate();
        } else if (this.getDiv() != null) {
            return ((MyDiv)this.getDiv()).evaluate();
        } else {
            return this.getNum();
        }

    }
}
