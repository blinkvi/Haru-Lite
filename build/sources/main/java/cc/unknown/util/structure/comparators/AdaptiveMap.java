package cc.unknown.util.structure.comparators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdaptiveMap<K, V> extends HashMap<K, V> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final ArrayList<V> arrayList = new ArrayList<>();
    
    public AdaptiveMap() { }

    public void put(V type) {
        arrayList.add(type);
    }

    @Override
    public ArrayList<V> values() {
        ArrayList<V> collection = new ArrayList<>(super.values());
        collection.addAll(arrayList);
        return collection;
    }

    public void removeValue(V value) {
        for (Map.Entry<K, V> entry : this.entrySet()) {
            if (entry.getValue().equals(value)) {
                this.remove(entry.getKey());
                break;
            }
        }

        arrayList.remove(value);
    }
}