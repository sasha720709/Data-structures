import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Test harness for BinaryHeap<T>. No JUnit, no -ea flag required.
 *
 *   javac List.java ArrayList.java PriorityQueue.java BinaryHeap.java BinaryHeapTest.java
 *   java BinaryHeapTest
 *
 * Exits 0 if everything passes, 1 otherwise.
 *
 * If your class is still named MinBinaryHeap, either rename it or do a
 * find-and-replace on BinaryHeap in this file.
 *
 * NOTE: java.util.* types are referenced fully-qualified where they would clash
 * with your own default-package classes (ArrayList, PriorityQueue).
 */
public class BinaryHeapTest {

    private static int passed = 0;
    private static int failed = 0;

    // ------------------------------------------------------------------ harness

    private static void section(String name) {
        System.out.println("\n── " + name + " " + "─".repeat(Math.max(0, 58 - name.length())));
    }

    private static void check(String name, boolean condition) {
        if (condition) { passed++; System.out.println("  PASS  " + name); }
        else           { failed++; System.out.println("  FAIL  " + name); }
    }

    private static void checkEquals(String name, Object expected, Object actual) {
        boolean ok = (expected == null) ? actual == null : expected.equals(actual);
        if (ok) { passed++; System.out.println("  PASS  " + name); }
        else {
            failed++;
            System.out.println("  FAIL  " + name + "\n          expected: " + expected
                    + "\n          actual:   " + actual);
        }
    }

    private static void expectThrows(String name, Class<? extends Throwable> expected, Runnable body) {
        Throwable caught = null;
        try { body.run(); } catch (Throwable t) { caught = t; }
        if (caught == null) {
            failed++;
            System.out.println("  FAIL  " + name + "\n          expected " + expected.getSimpleName()
                    + ", nothing was thrown");
        } else if (expected.isInstance(caught)) {
            passed++; System.out.println("  PASS  " + name);
        } else {
            failed++;
            System.out.println("  FAIL  " + name + "\n          expected " + expected.getSimpleName()
                    + ", got " + caught.getClass().getSimpleName()
                    + ": " + caught.getMessage());
        }
    }

    // ------------------------------------------------------- the important part

    /**
     * Walks the entire array and asserts the heap property at every index:
     * every node compares <= each of its children.
     *
     * This is the only check in the file that finds bugs you did not anticipate.
     * A heap with one inverted sign still returns plausible answers on small
     * inputs; it does not survive this.
     *
     * Requires a package-private or public accessor on BinaryHeap that exposes
     * the backing store for inspection. Add this to BinaryHeap:
     *
     *     ArrayList<T> backingArray() { return array; }   // package-private, tests only
     *
     * If you would rather not expose it, delete that line and instead have this
     * method drain a copy of the heap and check the output is sorted — weaker,
     * but it needs no accessor.
     */
    private static <T extends Comparable<? super T>> boolean verifyHeapProperty(BinaryHeap<T> heap, Comparator<? super T> order) {
        ArrayList<T> a = heap.backingArray();
        for (int i = 0; i < a.size(); i++) {
            int l = 2 * i + 1;
            int r = 2 * i + 2;
            if (l < a.size() && order.compare(a.get(i), a.get(l)) > 0) {
                System.out.println("          violation: node " + i + " (" + a.get(i)
                        + ") > left child " + l + " (" + a.get(l) + ")");
                return false;
            }
            if (r < a.size() && order.compare(a.get(i), a.get(r)) > 0) {
                System.out.println("          violation: node " + i + " (" + a.get(i)
                        + ") > right child " + r + " (" + a.get(r) + ")");
                return false;
            }
        }
        return true;
    }

    /** Drains the heap into an array. Destroys the heap. */
    private static Integer[] drain(BinaryHeap<Integer> heap) {
        Integer[] out = new Integer[heap.size()];
        for (int i = 0; i < out.length; i++) out[i] = heap.heapExtractMin();
        return out;
    }

    private static boolean isNonDecreasing(Integer[] a) {
        for (int i = 1; i < a.length; i++) if (a[i - 1] > a[i]) return false;
        return true;
    }

    private static String render(Object[] a) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < a.length; i++) { if (i > 0) sb.append(", "); sb.append(a[i]); }
        return sb.append("]").toString();
    }

    private static final Comparator<Integer> NATURAL = Comparator.naturalOrder();

    // ------------------------------------------------------------------ tests

    private static void testEmptyAndSingleton() {
        section("Empty heap and single element");

        BinaryHeap<Integer> h = new BinaryHeap<>();
        checkEquals("fresh heap has size 0", 0, h.size());
        check("fresh heap is empty", h.isEmpty());
        expectThrows("heapExtractMin on empty throws",
                NoSuchElementException.class, h::heapExtractMin);
        expectThrows("heapMinimum on empty throws",
                NoSuchElementException.class, h::heapMinimum);

        h.minHeapInsert(42);
        checkEquals("size 1 after one insert", 1, h.size());
        check("not empty after one insert", !h.isEmpty());
        checkEquals("heapMinimum returns the only element", 42, h.heapMinimum());
        checkEquals("heapMinimum did not remove it", 1, h.size());
        checkEquals("heapExtractMin returns it", 42, h.heapExtractMin());
        checkEquals("size back to 0", 0, h.size());
        check("empty again", h.isEmpty());

        // The size==1 extract path: last element moved to root, then heapify on an
        // array that just became empty. Off-by-one here reads past the end.
        expectThrows("extract after draining throws again",
                NoSuchElementException.class, h::heapExtractMin);

        h.minHeapInsert(7);
        checkEquals("heap is reusable after draining", 7, h.heapExtractMin());
    }

    private static void testMinimumIsNonDestructive() {
        section("heapMinimum is O(1) and non-destructive");

        BinaryHeap<Integer> h = new BinaryHeap<>();
        int[] vals = {50, 20, 80, 10, 30};
        for (int v : vals) h.minHeapInsert(v);

        checkEquals("heapMinimum returns the smallest", 10, h.heapMinimum());
        checkEquals("calling it did not change size", 5, h.size());
        checkEquals("calling it twice gives the same answer", 10, h.heapMinimum());
        checkEquals("still size 5", 5, h.size());
        checkEquals("extract then minimum gives the next smallest", 10, h.heapExtractMin());
        checkEquals("new minimum", 20, h.heapMinimum());
    }

    private static void testInsertExtractOrder() {
        section("Insert-then-drain produces sorted output");

        // ascending input
        BinaryHeap<Integer> asc = new BinaryHeap<>();
        for (int i = 0; i < 200; i++) asc.minHeapInsert(i);
        check("heap property holds after 200 ascending inserts", verifyHeapProperty(asc, NATURAL));
        Integer[] out = drain(asc);
        check("ascending input drains sorted", isNonDecreasing(out));
        checkEquals("all 200 came back", 200, out.length);

        // descending input — opposite worst case for the ascent loop
        BinaryHeap<Integer> desc = new BinaryHeap<>();
        for (int i = 199; i >= 0; i--) desc.minHeapInsert(i);
        check("heap property holds after 200 descending inserts", verifyHeapProperty(desc, NATURAL));
        check("descending input drains sorted", isNonDecreasing(drain(desc)));

        // random input
        Random rnd = new Random(1L);
        BinaryHeap<Integer> rand = new BinaryHeap<>();
        for (int i = 0; i < 1000; i++) rand.minHeapInsert(rnd.nextInt(10_000));
        check("heap property holds after 1000 random inserts", verifyHeapProperty(rand, NATURAL));
        Integer[] rout = drain(rand);
        check("random input drains sorted", isNonDecreasing(rout));
        checkEquals("all 1000 came back", 1000, rout.length);

        // duplicates: the heap guarantees nothing about their relative order,
        // only that they all come out and the sequence is non-decreasing
        BinaryHeap<Integer> dup = new BinaryHeap<>();
        for (int i = 0; i < 50; i++) dup.minHeapInsert(5);
        Integer[] dout = drain(dup);
        checkEquals("50 identical keys all come back", 50, dout.length);
        check("all of them are 5", isNonDecreasing(dout) && dout[0] == 5 && dout[49] == 5);
    }

    private static void testBuildFromArray() {
        section("O(n) construction from an existing array");

        ArrayList<Integer> src = new ArrayList<>();
        int[] vals = {9, 4, 7, 1, 8, 3, 6, 2, 5};
        for (int v : vals) src.addLast(v);

        BinaryHeap<Integer> h = new BinaryHeap<>(src);
        check("heap property holds immediately after construction", verifyHeapProperty(h, NATURAL));
        checkEquals("size matches the source", 9, h.size());
        checkEquals("minimum is correct", 1, h.heapMinimum());
        Integer[] out = drain(h);
        checkEquals("drains fully sorted", "[1, 2, 3, 4, 5, 6, 7, 8, 9]", render(out));

        // The source must not be aliased or mutated. Storing the caller's
        // reference means they can corrupt the heap from outside.
        ArrayList<Integer> src2 = new ArrayList<>();
        for (int v : new int[]{9, 4, 7, 1}) src2.addLast(v);
        String before = src2.toString();
        BinaryHeap<Integer> h2 = new BinaryHeap<>(src2);
        checkEquals("construction did not reorder the caller's array", before, src2.toString());
        src2.addLast(-99);
        checkEquals("appending to the caller's array did not change the heap", 4, h2.size());
        checkEquals("and did not change its minimum", 1, h2.heapMinimum());

        // single element and empty input
        ArrayList<Integer> one = new ArrayList<>();
        one.addLast(5);
        BinaryHeap<Integer> h3 = new BinaryHeap<>(one);
        checkEquals("single-element build", 5, h3.heapExtractMin());

        ArrayList<Integer> none = new ArrayList<>();
        BinaryHeap<Integer> h4 = new BinaryHeap<>(none);
        checkEquals("empty build gives an empty heap", 0, h4.size());
        h4.minHeapInsert(1);
        checkEquals("empty-built heap is usable", 1, h4.heapExtractMin());

        // build must agree with repeated insertion
        Random rnd = new Random(2L);
        int[] payload = new int[500];
        for (int i = 0; i < payload.length; i++) payload[i] = rnd.nextInt(1000);

        ArrayList<Integer> bulk = new ArrayList<>();
        BinaryHeap<Integer> byInsert = new BinaryHeap<>();
        for (int v : payload) { bulk.addLast(v); byInsert.minHeapInsert(v); }
        BinaryHeap<Integer> byBuild = new BinaryHeap<>(bulk);

        check("bulk build satisfies the heap property", verifyHeapProperty(byBuild, NATURAL));
        checkEquals("bulk build and repeated insert produce identical output",
                render(drain(byInsert)), render(drain(byBuild)));
    }

    private static void testDecreaseKey() {
        section("heapDecreaseKey");

        BinaryHeap<Integer> h = new BinaryHeap<>();
        for (int v : new int[]{10, 20, 30, 40, 50, 60, 70}) h.minHeapInsert(v);

        // lower an interior node below the current minimum: it must reach the root
        h.heapDecreaseKey(h.size() - 1, 1);
        checkEquals("a key lowered below the minimum becomes the new minimum", 1, h.heapMinimum());
        check("heap property holds after decrease", verifyHeapProperty(h, NATURAL));
        checkEquals("size unchanged by decrease", 7, h.size());

        // a decrease that does not need to move
        BinaryHeap<Integer> h2 = new BinaryHeap<>();
        for (int v : new int[]{10, 20, 30, 40}) h2.minHeapInsert(v);
        int before = h2.heapMinimum();
        h2.heapDecreaseKey(h2.size() - 1, 35);
        checkEquals("a decrease that does not reach the root leaves the min alone", before, h2.heapMinimum());
        check("heap property still holds", verifyHeapProperty(h2, NATURAL));

        // increasing a key is a caller error
        BinaryHeap<Integer> h3 = new BinaryHeap<>();
        for (int v : new int[]{10, 20, 30}) h3.minHeapInsert(v);
        expectThrows("increasing a key throws IllegalArgumentException",
                IllegalArgumentException.class, () -> h3.heapDecreaseKey(1, 9999));
        check("the rejected call left the heap intact", verifyHeapProperty(h3, NATURAL));
        checkEquals("and left its size alone", 3, h3.size());

        expectThrows("out-of-range index throws",
                IndexOutOfBoundsException.class, () -> h3.heapDecreaseKey(99, 1));
        expectThrows("negative index throws",
                IndexOutOfBoundsException.class, () -> h3.heapDecreaseKey(-1, 1));
    }

    private static void testComparator() {
        section("Comparator: same class, opposite ordering");

        Comparator<Integer> reverse = Comparator.reverseOrder();
        BinaryHeap<Integer> max = new BinaryHeap<>(reverse);
        for (int v : new int[]{5, 1, 9, 3, 7}) max.minHeapInsert(v);

        checkEquals("with reverseOrder, the 'minimum' is the largest", 9, max.heapMinimum());
        check("heap property holds under the reversed comparator", verifyHeapProperty(max, reverse));
        checkEquals("extraction is descending", 9, max.heapExtractMin());
        checkEquals("then the next largest", 7, max.heapExtractMin());

        // the mirror-image test: any sign flip anywhere breaks this
        Random rnd = new Random(3L);
        int[] payload = new int[500];
        for (int i = 0; i < payload.length; i++) payload[i] = rnd.nextInt(1000);

        BinaryHeap<Integer> mn = new BinaryHeap<>();
        BinaryHeap<Integer> mx = new BinaryHeap<>(reverse);
        for (int v : payload) { mn.minHeapInsert(v); mx.minHeapInsert(v); }

        Integer[] ascending = drain(mn);
        Integer[] descending = drain(mx);
        boolean mirrored = ascending.length == descending.length;
        for (int i = 0; mirrored && i < ascending.length; i++) {
            if (!ascending[i].equals(descending[descending.length - 1 - i])) mirrored = false;
        }
        check("max-heap output is exactly the reverse of min-heap output", mirrored);

        // a comparator on a type whose natural order differs
        Comparator<String> byLength = Comparator.comparingInt(String::length);
        BinaryHeap<String> s = new BinaryHeap<>(byLength);
        for (String w : new String[]{"aaaa", "b", "ccc", "dd"}) s.minHeapInsert(w);
        checkEquals("orders by the comparator, not the natural order", "b", s.heapExtractMin());
        checkEquals("then the next shortest", "dd", s.heapExtractMin());
    }

    private static void testNaturalOrderingOfStrings() {
        section("String keys — catches compare(...) == -1");

        // String.compareTo returns character differences, not -1/0/1.
        // "a".compareTo("c") is -2, so any check against == -1 fails here.
        BinaryHeap<String> h = new BinaryHeap<>();
        for (String w : new String[]{"pear", "apple", "cherry", "banana", "date"}) h.minHeapInsert(w);

        check("heap property holds for String keys", verifyHeapProperty(h, Comparator.<String>naturalOrder()));
        checkEquals("minimum is alphabetically first", "apple", h.heapMinimum());

        StringBuilder sb = new StringBuilder();
        while (!h.isEmpty()) { if (sb.length() > 0) sb.append(" "); sb.append(h.heapExtractMin()); }
        checkEquals("drains in alphabetical order", "apple banana cherry date pear", sb.toString());

        // characters far apart in the alphabet: differences well outside -1/0/1
        BinaryHeap<String> h2 = new BinaryHeap<>();
        for (String w : new String[]{"z", "a", "m"}) h2.minHeapInsert(w);
        checkEquals("wide character gaps still order correctly", "a", h2.heapExtractMin());
        checkEquals("and the next", "m", h2.heapExtractMin());
        checkEquals("and the last", "z", h2.heapExtractMin());
    }

    private static void testNullAndCopy() {
        section("Null rejection and the copy constructor");

        BinaryHeap<Integer> h = new BinaryHeap<>();
        expectThrows("inserting null throws",
                IllegalArgumentException.class, () -> h.minHeapInsert(null));
        checkEquals("the rejected insert did not change size", 0, h.size());

        for (int v : new int[]{5, 3, 8, 1, 9}) h.minHeapInsert(v);

        BinaryHeap<Integer> copy = new BinaryHeap<>(h);
        checkEquals("copy has the same size", h.size(), copy.size());
        checkEquals("copy has the same minimum", h.heapMinimum(), copy.heapMinimum());
        check("copy satisfies the heap property", verifyHeapProperty(copy, NATURAL));

        copy.heapExtractMin();
        checkEquals("extracting from the copy left the original size alone", 5, h.size());
        checkEquals("and left the original minimum alone", 1, h.heapMinimum());

        h.minHeapInsert(-100);
        checkEquals("inserting into the original left the copy alone", 4, copy.size());
        checkEquals("and left the copy's minimum alone", 3, copy.heapMinimum());

        expectThrows("copying null throws",
                IllegalArgumentException.class, () -> new BinaryHeap<Integer>((BinaryHeap<Integer>) null));

        // the comparator must survive copying
        BinaryHeap<Integer> max = new BinaryHeap<>(Comparator.reverseOrder());
        for (int v : new int[]{1, 5, 3}) max.minHeapInsert(v);
        BinaryHeap<Integer> maxCopy = new BinaryHeap<>(max);
        checkEquals("the copy kept the comparator", 5, maxCopy.heapMinimum());
    }

    /**
     * The test that earns its keep. Random interleaved operations, heap property
     * verified after every single one, with a java.util.PriorityQueue as an oracle
     * for the extraction sequence. Fixed seed, so any failure reproduces.
     */
    private static void testRandomisedInvariant() {
        section("Randomised: 10 000 ops, invariant checked after each");

        BinaryHeap<Integer> mine = new BinaryHeap<>();
        java.util.PriorityQueue<Integer> oracle = new java.util.PriorityQueue<>();
        Random rnd = new Random(20260814L);

        boolean invariantOk = true, sizeOk = true, valueOk = true;
        int op = 0, maxSize = 0;

        for (; op < 10_000; op++) {
            if (rnd.nextInt(100) < 60 || oracle.isEmpty()) {
                int v = rnd.nextInt(5_000);
                mine.minHeapInsert(v);
                oracle.add(v);
            } else {
                Integer expected = oracle.poll();
                Integer actual = mine.heapExtractMin();
                if (!expected.equals(actual)) {
                    valueOk = false;
                    System.out.println("          op " + op + ": expected " + expected + ", got " + actual);
                }
            }

            if (mine.size() != oracle.size()) {
                sizeOk = false;
                System.out.println("          op " + op + ": size " + mine.size()
                        + " vs oracle " + oracle.size());
            }
            if (!mine.isEmpty() && !mine.heapMinimum().equals(oracle.peek())) {
                valueOk = false;
                System.out.println("          op " + op + ": minimum " + mine.heapMinimum()
                        + " vs oracle " + oracle.peek());
            }
            if (!verifyHeapProperty(mine, NATURAL)) {
                invariantOk = false;
                System.out.println("          op " + op + ": heap property violated");
            }

            maxSize = Math.max(maxSize, mine.size());
            if (!invariantOk || !sizeOk || !valueOk) break;
        }

        check("heap property held after every one of 10 000 operations", invariantOk);
        check("size tracked the oracle throughout", sizeOk);
        check("every extraction and minimum matched the oracle", valueOk);
        System.out.println("          completed " + op + " ops, peak size " + maxSize);
    }

    // ------------------------------------------------------------------ main

    public static void main(String[] args) {
        System.out.println("BinaryHeap<T> — test run");

        testEmptyAndSingleton();
        testMinimumIsNonDestructive();
        testInsertExtractOrder();
        testBuildFromArray();
        testDecreaseKey();
        testComparator();
        testNaturalOrderingOfStrings();
        testNullAndCopy();
        testRandomisedInvariant();

        System.out.println("\n" + "═".repeat(62));
        System.out.printf("  %d passed, %d failed, %d total%n", passed, failed, passed + failed);
        System.out.println("═".repeat(62));

        if (failed > 0) System.exit(1);
    }
}