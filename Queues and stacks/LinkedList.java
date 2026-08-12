import java.util.NoSuchElementException;

public class LinkedList<T> {
    private int size;
    private Node<T>  head;
    private Node<T> tail;

    private static class Node<E> {
        private final E value;
        private Node<E> next;

        public Node(E value) {
            if (value == null) throw new IllegalArgumentException("null value violates list's invariant!");
            this.value = value;
        }

        public Node(E value, Node<E> next) {
            if (value == null) throw new IllegalArgumentException("null value violates list's invariant!");
            this.value = value;
            this.next = next;
        }
    }

    // Constructors
    public LinkedList() {
        this.head = null;
        this.size = 0;
        this.tail = null;
    }

    public LinkedList(T value) {
        this.head = this.tail = new Node<>(value);
        this.size = 1;
    }
    public LinkedList (T [] valueArray) {
        if (valueArray == null) {throw new IllegalArgumentException("Can't create list from a null value");}
        this();
        for (T value : valueArray) {this.addLast(value);}
    }

    public LinkedList(LinkedList<T> other) {
        if (other == null) {throw new IllegalArgumentException("Can't create list from a null value");}
        this();
        Node<T> curr = other.head;
        while (curr != null) {
            this.addLast(curr.value);
            curr = curr.next;
        }
    }

    // insertion
    public void addLast(T value) {
        if (this.head == null) {this.head = this.tail = new Node<>(value); size ++; return;}

        this.tail.next = new Node<>(value);
        this.tail = this.tail.next;
        size++;
    }

    public void addFirst(T value) {
        this.head = new Node<>(value, this.head);
        if (this.size == 0) {this.tail = this.head;}
        this.size ++;
    }

    // Removal
    public T removeFirst() {
        Node<T> prevHead = this.head;

        if (size == 0) {throw new NoSuchElementException("Attempting deletion from empty list");}
        if  (size == 1) {this.head = this.tail = null; size--; return prevHead.value;}

        this.head = this.head.next;
        prevHead.next = null;
        this.size --;

        return prevHead.value;
    }

    public T removeLast() { // Works for O(n) thus insert to tail, delete from head. is this redundant?
        if (size == 0) {throw new  NoSuchElementException("Attempting deletion from empty list");}
        if (size == 1) {return removeFirst();}

        Node<T> curr = this.head;
        while (curr.next != this.tail) {
            curr = curr.next;
        }

        Node<T> oldTail = this.tail;
        curr.next = null;
        this.tail = curr;
        this.size --;

        return oldTail.value;
    }

    // Access
    public T getFirst() {
        if (this.head == null) throw new NoSuchElementException("No such element");
        return this.head.value;
    }

    public T getLast() {
        if (this.tail == null) throw new NoSuchElementException("No such element");
        return this.tail.value;
    }

    public boolean isEmpty() {return this.size == 0;}
    public int size() {return this.size;}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");

        Node<T> curr = this.head;
        boolean first = true;

        while (curr != null) {
            if (!first) sb.append(", ");
            sb.append(curr.value.toString());
            first = false;
            curr = curr.next;
        }

        return sb.append("]").toString();
    }
}
