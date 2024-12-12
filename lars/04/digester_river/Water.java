public class Water {
    public String name;
    public String id;
    public String into_id; /* always null for seas */
    public void setName(String name) {
        this.name = name;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setInto_id(String into_id) {
        this.into_id = into_id;
    }
    /* this way I don't have to always upcast...*/
    public float getLength() {
        return 0.0f;
    }
    @Override
    public String toString() {
        /* the conditional is for seas */
        return "name='" + name + '\'' +
                ", id='" + id + '\'' +
                (into_id != null ? ", into_id='" + into_id + '\'' : "");
    }

}