import java.util.NoSuchElementException;

public class ArrayList<T> implements List<T> {
    private int size;
    private Object[] array;
    private final static int defaultCapacity = 10;

    private void resize(int capacity) {
        Object[] tempArr;
        int newCapacity = Math.max(capacity, (this.array.length == 0) ? 1 : this.array.length*2);

        if (capacity > this.array.length) {
            tempArr = new Object[newCapacity];
            System.arraycopy(this.array, 0, tempArr, 0, this.array.length);
            this.array = tempArr;
        }
    }

    private void checkBounds(int index) {
        if (index >= this.size || index < 0) throw new IndexOutOfBoundsException("Index out of bounds");
    }
    private void checkArgument(T value) {
        if (value == null) throw new IllegalArgumentException("null value is not acceptable");
    }

    public ArrayList() {this(defaultCapacity);}
    public ArrayList(int capacity) {this.array = new Object[capacity]; this.size = 0;}

    @SuppressWarnings("unchecked")
    public ArrayList(ArrayList<T> other) {
        if (other == null) throw new IllegalArgumentException("null value can't be copied");
        this(other.size());

        for (T p : (T[]) other.array) {if (p != null) this.addLast(p);}
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(int index) {checkBounds(index); return (T) this.array[index];}

    @Override
    public void set(int index, T value) {
        checkArgument(value);
        checkBounds(index);
        this.array[index] = value;
    }

    @Override
    public int size() {return this.size;}
    public int capacity() {return this.array.length;}
    @Override
    public boolean isEmpty() {return this.size == 0;}

    @Override
    public void addLast(T value) {
        checkArgument(value);
        resize(this.size + 1);
        this.array[this.size] = value;
        this.size ++;
    }
    @Override
    @SuppressWarnings("unchecked")
    public T removeLast() {
        if (size == 0) throw new NoSuchElementException("can't remove last element from an empty array");
        T oldValue = (T) this.array[size - 1];
        this.array[size - 1] = null;
        this.size --;

        return oldValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T removeFirst() {
        if (size == 0) throw new NoSuchElementException("can't remove last element from an empty array");
        T oldValue = (T) this.array[0];

        for (int i = 1; i < this.size; i++) {this.array[i-1] = this.array[i];}
        this.array[size - 1] = null;
        this.size --;

        return oldValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;

        for (Object value : this.array) {
            if (!(first) && value != null) sb.append(", ");
            if (value != null) sb.append(value);
            first = false;
        }
        return sb.append("]").toString();
    }
}

