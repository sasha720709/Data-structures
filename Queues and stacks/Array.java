public interface Array<T> {
    T get(int i);
    void set(int i, T value);
    int size();
    boolean isEmpty();
    void addLast(T value);
    T removeLast();
    T removeFirst();
    int capacity();
}
