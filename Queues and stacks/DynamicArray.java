import java.util.NoSuchElementException;

public class DynamicArray<T> implements Array<T> {
    private int size;
    private Object [] array;
    private static final int defaultCapacity= 10;

    public DynamicArray() {
        this(defaultCapacity);
    }

    public DynamicArray(int capacity) {
        this.array = new Object[capacity];
        this.size = 0;
    }

    @SuppressWarnings("unchecked")
    public DynamicArray(DynamicArray<T> other) {
        this();
        for (int i = 0; i < other.array.length; i++) {
            addLast((T) other.array[i]);
        }
        this.size = other.size;
    }

    private void checkBounds(int index) {
        if (index >= this.size || index < 0) throw new IndexOutOfBoundsException("index out of bounds, use addLast");
    }

    private void ensureCapacity(int capacity) {
        Object[] tempArr;
        int newCapacity = Math.max(capacity, (this.array.length == 0) ? 1 : this.array.length*2);

        if (capacity > this.array.length) {
            tempArr = new Object[newCapacity];
            for (int i = 0; i < this.array.length; i++) {tempArr[i] = this.array[i];}
            this.array = tempArr;
        }
    }

    @Override
    public int size() {return this.size;}
    @Override
    public int capacity() {return this.array.length;}

    @Override
    @SuppressWarnings("unchecked")
    public T get(int index) {checkBounds(index); return (T) this.array[index];}

    @Override
    public void set(int index, T value) {checkBounds(index); this.array[index] = value;}

    @Override
    public boolean isEmpty() {return this.size == 0;}

    @Override
    public void addLast(T value) {
        ensureCapacity(this.size + 1);
        this.array[size] = value;
        this.size ++;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T removeLast() {
        if (this.size == 0) throw new NoSuchElementException("Could not handle deletion from an empty array");
        T oldValue = (T) this.array[size - 1];
        this.array[size - 1] = null;
        this.size --;
        return oldValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T removeFirst() {
        if (this.size == 0) {throw new NoSuchElementException("Could not handle deletion from an empty array");}

        T oldValue = (T) this.array[0];
        for (int i = 1; i < this.size; i++) {this.array[i-1] = this.array[i];}
        this.array[this.size - 1] = null;
        this.size--;
        return oldValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;

        for (Object value : this.array) {
            if (!(first)) sb.append(", ");
            if (value != null) sb.append(value);
            first = false;
        }

        return sb.append("]").toString();
    }
}
