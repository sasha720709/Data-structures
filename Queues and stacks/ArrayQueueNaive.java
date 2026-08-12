import java.util.NoSuchElementException;

public class ArrayQueueNaive<T> implements Queue<T>{
    private final DynamicArray<T> array;

    public ArrayQueueNaive() {this.array = new DynamicArray<>();}
    public ArrayQueueNaive(int capacity) {this.array = new DynamicArray<>(capacity);}
    public ArrayQueueNaive(ArrayQueueNaive<T> other) {
        if (other == null) throw new IllegalArgumentException("can't copy null value");
        this.array = new DynamicArray<>(other.array);
    }

    @Override
    public void enqueue(T value) {this.array.addLast(value);}

    @Override
    public T dequeue() {return this.array.removeFirst();}

    @Override
    public T peek() {
        if (this.array.isEmpty()) throw new NoSuchElementException("The queue is empty");
        return this.array.get(0);
    }

    @Override
    public boolean isEmpty() {return this.array.isEmpty();}
    @Override
    public int size() {return this.array.size();}

    @Override
    public String toString() {return this.array.toString();}
}
