public class River extends Water {
    public float length;
    public void setLength(String length) {
        this.length = Float.parseFloat(length);
    }
    @Override
    public float getLength() {
        return this.length;
    }
    @Override
    public String toString() {
        return "River{" + super.toString() +
                ", length=" + length +
                ", into_id='" + into_id + '\'' +
                '}';
    }
}
