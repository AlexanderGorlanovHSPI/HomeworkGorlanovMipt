package practiceWithGeneric;

public class Pair<K,V> {
    K key;
    V value;

    public Pair (K key, V value){
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public  Pair<V,K> swap(){
        return new Pair<>(value, key);
    }

    @Override
    public String toString(){
        String keyToString = (key == null) ? ("null") : key.toString();
        String valToString = (value == null) ? ("null") : value.toString();
        return String.format("Pair{key=%s, value=%s}", keyToString, valToString);
    }

    public static void main(String[] args){
        final Pair<String, Integer> pair = new Pair<>("Age", 25);
        System.out.println(pair);
        final Pair<Integer, String> swapped = pair.swap();
        System.out.println(swapped);
    }
}
