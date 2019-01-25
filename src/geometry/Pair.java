package geometry;

public class Pair<A,B> implements Cloneable{
    private A left;
    private B right;

    public Pair (A left, B right){
        this.left = left;
        this.right = right;
    }

    public A getLeft() {
        return this.left;
    }

    public B getRight(){
        return this.right;
    }

    public void setLeft(A left) {
        this.left = left;
    }

    public void setRight(B right) {
        this.right = right;
    }

}
