public class LinkedStack<T> implements Stack<T> {
//     Composition - to restrain access to the Linked List's methods
    private final LinkedList<T> list;

    // Constructors
    public LinkedStack() {this.list = new LinkedList<>();}
    public LinkedStack(T value) {this.list = new LinkedList<>(value);}
    public LinkedStack(T[] valueArray) {
        this();
        for (T value: valueArray) {push(value);}
    }
    public LinkedStack(LinkedStack<T> other) {
        if (other == null) throw new IllegalArgumentException("Can't copy null value");
        this.list = new LinkedList<>(other.list);
    }

    // Methods
    @Override
    public void push(T value) {this.list.addFirst(value);}

    @Override
    public T pop() {return this.list.removeFirst();}

    @Override
    public T peek() {return this.list.getFirst();}

    @Override
    public boolean isEmpty() {return this.list.isEmpty();}

    @Override
    public int size() {return this.list.size();}

    @Override
    public String toString() {
        return this.list.toString();
    }
}
