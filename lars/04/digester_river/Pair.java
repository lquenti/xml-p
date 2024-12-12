/* ISNT IT CRAZY THAT JAVA DOESNT HAVE A PAIR CLASS?
 * <https://stackoverflow.com/questions/5303539/didnt-java-once-have-a-pair-class>
 */
public class Pair<K, V> {
    private final K fst;
    private final V snd;

    public static <K, V> Pair<K, V> createPair(K fst, V snd) {
        return new Pair<K, V>(fst, snd);
    }

    public Pair(K fst, V snd) {
        this.fst = fst;
        this.snd = snd;
    }

    public K getFirst() {
        return this.fst;
    }

    public V getSecond() {
        return this.snd;
    }

    @Override
    public String toString() {
        return "(" + this.fst + ", " + this.snd + ")";
    }
}
