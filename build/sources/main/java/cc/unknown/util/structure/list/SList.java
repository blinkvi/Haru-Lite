package cc.unknown.util.structure.list;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class SList<T> implements Iterable<T>, Serializable {
    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_CAPACITY = 10;

    private Object[] elements;
    private int size = 0;

    public SList() {
        elements = new Object[DEFAULT_CAPACITY];
    }

    public SList(int initialCapacity) {
        if (initialCapacity <= 0) throw new IllegalArgumentException("Capacity must be greater than 0");
        elements = new Object[initialCapacity];
    }

    public SList(Collection<? extends T> collection) {
        elements = collection.toArray();
        size = elements.length;
    }

    public void add(T element) {
        ensureCapacityInternal(size + 1);
        elements[size++] = element;
    }

    public void addAll(T... elements) {
        if (elements.length == 0) return;
        ensureCapacityInternal(size + elements.length);
        System.arraycopy(elements, 0, this.elements, size, elements.length);
        size += elements.length;
    }

    public boolean addAll(Collection<? extends T> collection) {
        if (collection.isEmpty()) return false;
        ensureCapacityInternal(size + collection.size());
        System.arraycopy(collection.toArray(), 0, elements, size, collection.size());
        size += collection.size();
        return true;
    }

    public boolean remove(T element) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(elements[i], element)) {
                fastRemove(i);
                return true;
            }
        }
        return false;
    }

    public boolean removeIf(Predicate<? super T> filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        for (int i = 0; i < size; i++) {
            if (filter.test(elementData(i))) {
                fastRemove(i);
                i--;
                removed = true;
            }
        }
        return removed;
    }
    
    public T get(int index) {
        checkIndex(index);
        return elementData(index);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean contains(T element) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(elements[i], element)) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        Arrays.fill(elements, 0, size, null);
        size = 0;
    }

    public Stream<T> stream() {
        return Arrays.stream(toArray());
    }

    public T[] toArray() {
        return Arrays.copyOf(elements, size, (Class<T[]>) elements.getClass());
    }

    public <E> E[] toArray(E[] a) {
        if (a.length < size) {
            return (E[]) Arrays.copyOf(elements, size, a.getClass());
        }
        System.arraycopy(elements, 0, a, 0, size);
        if (a.length > size) a[size] = null;
        return a;
    }

    private void ensureCapacityInternal(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = Math.max(minCapacity, elements.length * 2);
            elements = Arrays.copyOf(elements, newCapacity);
        }
    }

    public void fastRemove(int index) {
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elements, index + 1, elements, index, numMoved);
        }
        elements[--size] = null;
    }
    
    public File fastRemoveFile(int index) {
        checkIndex(index);
        File removedFile = (File) elements[index];

        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elements, index + 1, elements, index, numMoved);
        }

        elements[--size] = null;
        return removedFile;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    private T elementData(int index) {
        return (T) elements[index];
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                return elementData(cursor++);
            }
        };
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        for (int i = 0; i < size; i++) {
            action.accept(elementData(i));
        }
    }
}
