import java.util.NoSuchElementException;

// List invariant - each node has none Null value;
public class MyLinkedList<T> {
    private int size;
    private Node<T> head; // Head pointer
    private Node<T> tail; // Tail pointer

    private static class Node<E> {
        private E value;
        private Node<E> next;

        // Value based constructor
        public Node(E value, Node<E> next) {
            if (value == null) {throw new IllegalArgumentException("null value violates list's invariant");}

            this.next = next;
            this.value = value;
        }

        // another value based constructor
        public Node(E value) {
            if (value == null) {throw new IllegalArgumentException("null value violates list's invariant");}
            this.value = value;
        }
    }

    //Constructors
    public MyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public MyLinkedList(T value) {
        this.head = new Node<>(value);
        this.tail = this.head;
        this.size = 1;
    }

    public MyLinkedList(MyLinkedList<T> other) {
        this();
        if (other != null) {
            Node<T> curr = other.head;
            while (curr != null) {
                this.addLast(curr.value);
                curr = curr.next;
            }
        }
    }

    public void addFirst(T value) {
        this.head = new Node<>(value, this.head);
        if (this.size == 0) {this.tail = this.head;}
        this.size ++;
    }

    public void addLast(T value) {
        if (this.head == null) {this.head = this.tail = new Node<>(value); size ++; return;}

        this.tail.next = new Node<>(value);
        this.tail = this.tail.next;
        size++;

    }

    public void add(int index, T value) {
        if (index < 0 || index > size) {throw new IndexOutOfBoundsException("Index Out of Bound");}
        if (index == 0) {addFirst(value); return;}
        Node<T> curr = this.head;

        for (int i = 0; i < index - 1; i ++) {curr = curr.next;}
        curr.next = new Node<>(value, curr.next);
        size ++;

        if (index == this.size - 1) {
            this.tail = curr.next;
        }

    }


    public T removeFirst() {
        Node<T> prevHead = this.head;

        if (size == 0) {throw new NoSuchElementException("Attempting deletion from empty list");}
        if  (size == 1) {this.head = this.tail = null; size--; return prevHead.value;}

        this.head = this.head.next;
        prevHead.next = null;
        this.size --;

        return prevHead.value;
    }

    public T removeLast() {
        if (size == 0) {throw new  NoSuchElementException("Attempting deletion from empty list");}
        if (size == 1) {T deleted = removeFirst(); this.size --; return deleted;}

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

    public T remove(int index) {
        if (size == 0) {throw new  NoSuchElementException("Attempting deletion from empty list");}
        if (index < 0 || index >= size) {throw new IndexOutOfBoundsException("Index out of bounds");}
        if (index == 0) return removeFirst();

        Node<T> curr = this.head;

        for (int i = 0; i < index - 1; i++) {
            curr = curr.next;
        }

        if (curr.next == this.tail) {return removeLast();}

        Node<T> deleteNode = curr.next;
        curr.next = curr.next.next;
        this.size --;
        return deleteNode.value;
    }

    public boolean remove(T value) {
        if (size == 0) {throw new  NoSuchElementException("Attempting deletion from empty list");}
        if (value.equals(head.value)) { removeFirst(); return true; }

        Node<T> curr =  this.head;

        while (curr.next != null) {
            if (value.equals(curr.next.value)) {
                if (curr.next == this.tail) {this.tail = curr;}
                curr.next = curr.next.next;
                size --;
                return true;
            }
            curr = curr.next;
        }

        return false;
    }

    public T get(int index) {
        if (index >= size || index < 0) {throw new IndexOutOfBoundsException("Index out of bound");}
        Node<T> curr = this.head;

        for (int i = 0; i < index; i++) {curr = curr.next;}
        return curr.value;
    }

    public T set(int index, T value) {
        if (index >= size || index < 0) {throw new IndexOutOfBoundsException("Index out of bound");}
        Node<T> curr = this.head;

        for (int i = 0; i < index; i++) {curr = curr.next;}
        T old = curr.value;
        curr.value = value;

        return old;
    }

    public T getFirst() {
        if (this.size == 0) {throw new NoSuchElementException("The list is empty");}
        return this.head.value;
    }
    public T getLast() {
        if (this.size == 0) {throw new NoSuchElementException("The list is empty");}
        return this.tail.value;
    }

    public boolean contains(T value) {
        if (value == null) {throw new IllegalArgumentException("List must not contain null elements");}

        Node<T> curr = this.head;

        while (curr != null) {
            if (value.equals(curr.value)) {return true;}
            curr = curr.next;
        }

        return false;
    }

    public int indexOf(T value) {
        if (value == null) {throw new IllegalArgumentException("List can not contain null elements");}

        int index = 0;
        Node<T> curr = this.head;

        while (curr != null) {
            if (value.equals(curr.value)) {return index;}
            else {curr = curr.next; index ++;}
        }
        return -1;
    }



    public int size() {
        return this.size;
    }
    public boolean isEmpty() {
        return size == 0;
    }
    public void clear() {
        this.head = null;
        this.size = 0;
        this.tail = null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<T> curr = this.head;

        while (curr != null) {
            sb.append(curr.value);
            if (curr.next != null) {sb.append(", ");}
            curr = curr.next;
        }

        return sb.append("]").toString();
    }
}
