import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

// could have been a list with sentinel, but I'm too lazy to do this.

public class MyDoublyLinkedList<T> implements Iterable<T> {
    private int size;
    private Node<T> tail;
    private Node<T> head;
    private int modCount;

    public void checkInvariants() {
        int fwd = 0;
        for (Node<T> c = this.head; c != null; c = c.next) {
            if (c.next != null && c.next.prev != c) throw new AssertionError("Bad invariant");
            fwd ++;
        }

        int back = 0;
        for (Node<T> c = this.tail; c != null; c = c.prev) back ++;
        if (fwd != back || fwd != size) {throw new AssertionError("size= " + size + "fwd= " + fwd + "back= "+ back);}
    }

    @Override
    public Iterator<T> iterator() {
        return new DoublyLinkedListIterator();
    }
    public Iterator<T> descendingIterator() {return new DescendingIterator();}

    // function that allows descending for each traversal
    public Iterable<T> descending() {return this::descendingIterator;}

    private class DescendingIterator implements Iterator<T> {
        Node<T> prev = tail;
        Node<T> lastReturned = null;
        int expectedModCount = modCount;

        private void checkForModificationCount() {if (this.expectedModCount != modCount) {throw new ConcurrentModificationException("Modification occurred while traversing");}}

        @Override
         public boolean hasNext() {return prev != null;}

        @Override
        public T next() {
            checkForModificationCount();
            if (prev == null) {throw new NoSuchElementException("No such element");}

            this.lastReturned = this.prev;
            this.prev = this.prev.prev;

            return this.lastReturned.data;
        }

        @Override
        public void remove() {
            checkForModificationCount();
            if (this.lastReturned == null) {throw new NoSuchElementException("No such element");}
            unlink(this.lastReturned);
            this.lastReturned = null;
            this.expectedModCount = modCount;
        }
    }


    private class DoublyLinkedListIterator implements Iterator<T> {
        Node<T> next = head;
        Node<T> lastReturned = null;
        int expectedModCount = modCount;

        private void checkForModificationCount() {if (this.expectedModCount != modCount) {throw new ConcurrentModificationException("Modification occurred while traversing");}}

        @Override
        public boolean hasNext() {return next != null;}

        @Override
        public T next() {
            checkForModificationCount();
            if (next == null) throw new NoSuchElementException("No such element");
            this.lastReturned = this.next;
            this.next = this.next.next;

            return lastReturned.data;
        }

        @Override
        public void remove() {
            checkForModificationCount();
            if (this.lastReturned == null) {throw new NoSuchElementException("No such element");}
            unlink(this.lastReturned);
            this.lastReturned = null;
            this.expectedModCount = modCount;
        }

    }

    private static class Node <E> {
        E data;
        Node<E> next;
        Node<E> prev;

        public Node (E data, Node<E> prev, Node<E> next) {
            if (data == null) {throw  new IllegalArgumentException("Null value violates list invariant");}
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(E data) {
            if (data == null) {throw  new IllegalArgumentException("Null value violates list invariant");}
            this.data = data;
        }
    }

    // Default constructor
    public MyDoublyLinkedList() {
        this.size = 0;
        this.modCount = 0;
        this.head = null;
        this.tail = null;
    }

    // Simple constructor
    public MyDoublyLinkedList(T value) {
        this.head = new Node<>(value);
        this.tail = this.head;
        this.modCount = 0;
        this.size = 1;
    }

    //Constructing DLL from primitive array
    public MyDoublyLinkedList(T[] valueArray) {
        this();
        for (T value : valueArray) {this.addLast(value);}
    }

    // Copy constructor
    public MyDoublyLinkedList(MyDoublyLinkedList<T> other) {
        this();
        if (other == null) {throw new IllegalArgumentException("Can't copy null value");}
        for (T value : other) {this.addLast(value);}
    }

    // Helper methods
    private Node<T> nodeAt (int index) {
        // O(n) complexity
        if (index >= size || index < 0) {throw new IndexOutOfBoundsException("Index could not be negative or exceed the size of list");}
        Node<T> curr = (index <= size / 2) ? this.head : this.tail;

        if (index <= size / 2) {for (int i = 0; i < index; i++) {curr = curr.next;}}
        else {for (int i = size - 1; i > index; i--) {curr = curr.prev;}}
        return curr;
    }

    private void linkBefore(Node<T> node, T item) {
        // O(1) complexity;
        if (size == 0) {this.head = this.tail = new Node<>(item); size++; return;}
        Node<T> prev = node.prev;
        Node<T> newNode = new Node<>(item, prev, node);
        node.prev = newNode;
        if (prev == null) head = newNode;
        else prev.next = newNode;

        this.size ++;
        this.modCount ++;
    }

    private T unlink(Node<T> node) {
        // O(1)
        if (this.size == 0) {throw new NoSuchElementException("attempt of deletion of an item from empty list");}
        if (this.size == 1) {this.tail = this.head = null; size --; return node.data;}

        if (node == this.head) {this.head = this.head.next; this.head.prev = null; this.size--; this.modCount ++; return node.data;}
        if (node == this.tail) {this.tail = this.tail.prev; this.tail.next = null; this.size--; this.modCount ++; return node.data;}

        node.prev.next = node.next;
        node.next.prev = node.prev;

        this.size --;
        this.modCount ++;

        return node.data;
    }

    // primitive methods
    public int size() {return this.size;}
    public boolean isEmpty() {return this.size == 0;}
    public void clear() {this.tail = null; this.head = null; this.size = 0;} // o(1) - faster and

    // Insertion
    public void addFirst(T item) {linkBefore(this.head, item);} // O(1)

    public void addLast(T item) {
        Node<T> newNode = new Node<>(item);
        if (size == 0) {this.tail = this.head = newNode; size++; return;}
        this.tail.next = newNode; newNode.prev = this.tail; this.tail = newNode;
        size++;
        this.modCount ++;
    } // O(1)

    public void add(int index, T item) {
        if (index == size) {addLast(item); return;}
        Node<T> atIndex = nodeAt(index);
        linkBefore(atIndex, item);
    } // O(n)


    // Removal
    public T removeFirst() {return unlink(this.head);} // O(1)
    public T removeLast() {return unlink(this.tail);} // O(1)

    public T remove(int index) {
        Node<T> atIndex = nodeAt(index);
        return unlink(atIndex);
    } // O(n)

    public boolean remove(T item) {

        Iterator<T> iter = this.iterator();

        while (iter.hasNext()) {
            if (item.equals(iter.next())) {iter.remove(); return true;}
        }

        return false;
    }

    // Access
    public T get(int index) {
        return nodeAt(index).data;
    } // O(n)

    public T set(int index, T item) {
        Node<T> atIndex = nodeAt(index);
        T oldValue = atIndex.data;
        atIndex.data = item;
        return oldValue;
    } //O(n)

    public int indexOf(T item) {
        int i = 0;
        for (T value: this) {if (item.equals(value)) {return i;} i++;}
        return -1;
    }

    public int lastIndexOf(T item) {
        int i = this.size - 1;

        for (T value : descending()) {
            if (Objects.equals(item, value)) {return i;}
            i--;
        }
        return -1;
    }

    public boolean contains(T item) {
        for (T value : this) {if (item.equals(value)) return true;}
        return false;
    }

    // Structural
    public void reverse() {
        // O(n)
        if (size == 0) {throw new AssertionError("List is empty - nothing to reverse");}

        Node<T> cur = this.head;

        while (cur != null) {
            Node<T> oldNext = cur.next;

            cur.next = cur.prev;
            cur.prev = oldNext;

            cur = oldNext;
        }

        Node<T> oldHead = this.head;
        this.head = this.tail;
        this.tail = oldHead;
        modCount ++;

    }


    // Object contract
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (T item : this) {
            if (!first) sb.append(", ");
            sb.append(item.toString());
            first = false;

        }

        return sb.append("]").toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MyDoublyLinkedList)) return false;
        MyDoublyLinkedList<?> other = (MyDoublyLinkedList<?>) o;
        if (this.size != other.size) return false;

        Iterator<T> a = this.iterator();
        Iterator<?> b = other.iterator();
        while (a.hasNext() && b.hasNext()) {
            if (!Objects.equals(a.next(), b.next())) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = 1;
        for (T item : this) h = 31 * h + Objects.hashCode(item);
        return h;
    }
}
