import java.util.NoSuchElementException;

public class ArrayStack<T> implements Stack<T> {
    private final DynamicArray<T> array;

    public ArrayStack() {this.array = new DynamicArray<>();}
    public ArrayStack(int capacity) {this.array = new DynamicArray<>(capacity);}
    public ArrayStack(ArrayStack<T> other) {
        if (other == null) throw new IllegalArgumentException("can't copy null value");
        this.array = new DynamicArray<>(other.array);
    }

    @Override
    public void push(T value) {array.addLast(value);}

    @Override
    public T pop() {return array.removeLast();}

    @Override
    public T peek() {
        if (this.array.isEmpty()) throw new NoSuchElementException("The stack is empty");
        return array.get(array.size() - 1);
    }

    @Override
    public boolean isEmpty() {return array.isEmpty();}

    @Override
    public int size() {return array.size();}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;

        for (int i = this.array.size() - 1; i >= 0; i--) {
            T curr = this.array.get(i);
            if (!(first)) sb.append(", ");
            if (curr != null) sb.append(curr);
            first = false;
        }

        return sb.append("]").toString();
    }
}
