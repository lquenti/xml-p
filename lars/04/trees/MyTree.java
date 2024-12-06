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

    public String expression() {
        if (this.getAdd() != null) {
            return ((MyAdd)this.getAdd()).expression();
        } else if (this.getSub() != null) {
            return ((MySub)this.getSub()).expression();
        } else if (this.getMul() != null) {
            return ((MyMul)this.getMul()).expression();
        } else if (this.getDiv() != null) {
            return ((MyDiv)this.getDiv()).expression();
        } else {
            return String.valueOf(this.getNum());
        }
    }

    public String tree() {
        if (this.getAdd() != null) {
            return ((MyAdd)this.getAdd()).tree();
        } else if (this.getSub() != null) {
            return ((MySub)this.getSub()).tree();
        } else if (this.getMul() != null) {
            return ((MyMul)this.getMul()).tree();
        } else if (this.getDiv() != null) {
            return ((MyDiv)this.getDiv()).tree();
        } else {
            return "<tr><td>" + this.getNum() + "</td></tr>";
        }
    }
}
