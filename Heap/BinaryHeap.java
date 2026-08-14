import java.util.Comparator;
import java.util.NoSuchElementException;

public class BinaryHeap<T extends Comparable<? super T>> implements PriorityQueue<T> {
    private final ArrayList<T> array;
    private final Comparator<? super T> cmp;

    private int parent (int index) {return (index - 1) / 2;}
    private int left (int index) {return  2 * index + 1;}
    private int right (int index) {return  2 * index + 2;}

    public ArrayList<T> backingArray() {return array;}

    @Override
    public T heapMinimum() {if (array.isEmpty()) throw new NoSuchElementException("No minimum in empty queue"); return array.get(0);} //peek
    @Override
    public int  size() {return array.size();}
    @Override
    public boolean isEmpty() {return array.isEmpty();}

    private void minHeapify(int index) {
        int l = left(index);
        int r  = right(index);
        int smallest;

        if (l < array.size() && compare(array.get(l), array.get(index)) < 0) {smallest = l;}
        else smallest = index;

        if (r < array.size() && compare(array.get(r), array.get(smallest)) < 0) {smallest = r;}
        if (smallest != index) {
            T oldSmallest = array.get(smallest);

            array.set(smallest, array.get(index));
            array.set(index, oldSmallest);

            minHeapify(smallest);
        }
    }

    public void heapDecreaseKey(int index, T key) {
        if (compare(key, array.get(index)) > 0) throw new IllegalArgumentException("New key is larger then current key");

        array.set(index, key);
        while (index > 0 && compare(array.get(parent(index)), array.get(index)) >= 1) {

            T oldParent = array.get(parent(index));
            array.set(parent(index), array.get(index));
            array.set(index, oldParent);
            index = parent(index);
        }

    }

    private void buildMinHeap(int size) {
        for (int i = size / 2 - 1; i >= 0; i--) {
            minHeapify(i);
        }
    }


    private int compare(T value1, T value2) {
        return (cmp != null) ? cmp.compare(value1, value2) : value1.compareTo(value2);
    }

    public BinaryHeap() {
        this.array = new ArrayList<>();
        this.cmp = null;
    }

    public BinaryHeap(Comparator<? super T> cmp) {
        this.array = new ArrayList<>();
        this.cmp = cmp;
    }

    public BinaryHeap(ArrayList<T> values) {
        this.array = new ArrayList<>(values);
        this.cmp = null;

        buildMinHeap(values.size());
    }

    public BinaryHeap(ArrayList<T> values, Comparator<? super T> cmp) {
        this.array = new ArrayList<>(values);
        this.cmp = cmp;

        buildMinHeap(values.size());
    }

    public BinaryHeap(BinaryHeap<T> other) {
        if (other == null) throw new IllegalArgumentException("Can't copy null value");

        array = new ArrayList<>(other.array);
        cmp = other.cmp;

    }
    @Override
    public void minHeapInsert(T key) {
        array.addLast(key);
        heapDecreaseKey(array.size() - 1, key);
    }

    @Override
    public T heapExtractMin() {
        if (array.isEmpty()) throw new NoSuchElementException("Can't extract from empty heap");

        T min = array.get(0);
        array.set(0, array.get(array.size() - 1));
        array.removeLast();
        minHeapify(0);

        return min;
    }

    @Override
    public String toString() {
        return array.toString();
    }
}
