import java.util.NoSuchElementException;

public class ArrayQueue<T> implements Queue<T> {
    private Object[] array;
    private int head;
    private int tail;
    private int size;
    private static final int defaultCapacity = 10;


    private void resize(int capacity) {
        int newCapacity = Math.max(capacity, (this.array.length == 0) ? 1 : this.array.length*2);

        if (capacity + 1 > this.array.length) {
            Object[] newArr = new Object[newCapacity];
            int k = 0;

            for (int i = this.head; i <= this.size; i++) {
                newArr[k] = this.array[(this.head + i) % this.array.length];
                k++;
            }

            this.head = 0;
            this.tail = this.size;

            this.array = newArr;
        }
    }

    public ArrayQueue() {
        this.array =  new Object[defaultCapacity];
        this.size = 0;
        this.head = this.tail = 0;
    }
    public ArrayQueue(int capacity) {
        if (capacity == 0) throw new IllegalArgumentException("Could not create a queue size 0");

        this.array =  new Object[capacity];
        this.size = 0;
        this.head = this.tail = 0;
    }

    public ArrayQueue(ArrayQueue<T> other) {
        if (other == null) {throw new IllegalArgumentException("Could not copy a null value");}
        this(other.array.length);

        for (int i = other.head; i < other.size(); i++) {
            this.array[(this.head + i) % this.array.length] = other.array[(other.head + i) % other.array.length];
        }

        this.size = other.size();
        this.head = other.head;
        this.tail = other.tail;
    }

    public void enqueue(T value) {
        if (value == null) throw new IllegalArgumentException("Can't insert null value");
        resize(this.size);

        this.array[tail] = value;
        this.tail = (tail + 1) % this.array.length;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (this.size == 0) throw new NoSuchElementException("Could not dequeue from an empty queue");
        T item = (T) this.array[this.head];

        this.array[this.head] = null;
        this.head = (this.head + 1) % this.array.length;
        this.size --;

        return item;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (this.size == 0) {throw new NoSuchElementException("Could not get an element from empty queue");}
        return (T) this.array[this.head];
    }

    public boolean isEmpty() {return this.size == 0;}
    public int size() {return this.size;}

    @SuppressWarnings("unchecked")
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;

        for (int i = this.head; i <= this.size; i++) {
           T p = (T) this.array[i % this.array.length];
           if (p != null) {
               if (!(first)) sb.append(", ");
               sb.append(p);
               first = false;
           }

        }
        return sb.append("]").toString();
    }
}
