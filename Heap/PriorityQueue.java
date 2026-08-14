public interface PriorityQueue<T> {   // the ADT — any implementation
    void minHeapInsert(T key);
    T    heapExtractMin();
    T    heapMinimum();
    void heapDecreaseKey(int index, T key);
    int  size();
    boolean isEmpty();
}
