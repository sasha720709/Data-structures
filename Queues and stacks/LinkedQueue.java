public class LinkedQueue<T> implements Queue<T> {
    private final LinkedList<T> list;

    //Constructors
    public LinkedQueue() {this.list = new LinkedList<>();}
    public LinkedQueue(T value) {this.list = new LinkedList<>(value);}
    public LinkedQueue(T [] valueArray) {this.list = new LinkedList<>(valueArray);}

    public LinkedQueue(LinkedQueue<T> other) {
        if (other == null) throw new IllegalArgumentException("can't copy null value");
        this.list = new LinkedList<>(other.list);
    }

    //Methods
    @Override
    public void enqueue(T value) {this.list.addLast(value);}
    @Override
    public T dequeue() {return this.list.removeFirst();}
    @Override
    public T peek() {return this.list.getFirst();}
    @Override
    public boolean isEmpty() {return this.list.isEmpty();}
    @Override
    public int size() {return this.list.size();}

    @Override
    public String toString() {return this.list.toString();}
}
