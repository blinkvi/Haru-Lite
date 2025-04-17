package cc.unknown.util.structure.list;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("unchecked")
public class SHashSet<T> implements Set<T>, Serializable {
    private static final long serialVersionUID = 1L;
    private static final float LOAD_FACTOR = 0.75f;
    private static final int DEFAULT_CAPACITY = 16;

    private transient Object[] table;
    private int size = 0;
    private static final Object TOMBSTONE = new Object();

    public SHashSet() {
        this.table = new Object[DEFAULT_CAPACITY];
    }

    public SHashSet(int initialCapacity) {
        if (initialCapacity <= 0) throw new IllegalArgumentException("Capacity must be greater than 0");
        this.table = new Object[initialCapacity];
    }

    @Override
    public boolean add(T element) {
        if (contains(element)) return false;
        ensureCapacity(size + 1);
        int index = indexFor(element);
        while (table[index] != null && table[index] != TOMBSTONE) {
            index = (index + 1) % table.length;
        }
        table[index] = element;
        size++;
        return true;
    }

    @Override
    public boolean remove(Object element) {
        int index = indexFor((T) element);
        while (table[index] != null) {
            if (Objects.equals(table[index], element)) {
                table[index] = TOMBSTONE;
                size--;
                return true;
            }
            index = (index + 1) % table.length;
        }
        return false;
    }

    @Override
    public boolean contains(Object element) {
        int index = indexFor((T) element);
        while (table[index] != null) {
            if (table[index] != TOMBSTONE && Objects.equals(table[index], element)) {
                return true;
            }
            index = (index + 1) % table.length;
        }
        return false;
    }

    @Override
    public void clear() {
        Arrays.fill(table, null);
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int count = 0;

            @Override
            public boolean hasNext() {
                while (count < table.length && (table[count] == null || table[count] == TOMBSTONE)) {
                    count++;
                }
                return count < table.length;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                return (T) table[count++];
            }
        };
    }

    @Override
    public Object[] toArray() {
        return stream().toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return stream().toArray(size -> Arrays.copyOf(a, size));
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object obj : c) {
            if (!contains(obj)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean modified = false;
        for (T element : c) {
            modified |= add(element);
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        for (Object obj : this.toArray()) {
            if (!c.contains(obj)) {
                remove(obj);
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object obj : c) {
            modified |= remove(obj);
        }
        return modified;
    }

    private int indexFor(T element) {
        return Math.abs(Objects.hashCode(element)) % table.length;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > table.length * LOAD_FACTOR) {
            resize();
        }
    }

    private void resize() {
        Object[] oldTable = table;
        table = new Object[oldTable.length * 2];
        size = 0;
        for (Object obj : oldTable) {
            if (obj != null && obj != TOMBSTONE) add((T) obj);
        }
    }
}

