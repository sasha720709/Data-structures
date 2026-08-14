import java.util.NoSuchElementException;
import java.util.Random;

public class ArrayListTest {

    private static int passed = 0;
    private static int failed = 0;
    private static String section = "";

    // ------------------------------------------------------------------ harness

    private static void section(String name) {
        section = name;
        System.out.println("\n── " + name + " " + "─".repeat(Math.max(0, 58 - name.length())));
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("  PASS  " + name);
        } else {
            failed++;
            System.out.println("  FAIL  " + name);
        }
    }

    private static void checkEquals(String name, Object expected, Object actual) {
        boolean ok = (expected == null) ? actual == null : expected.equals(actual);
        if (ok) {
            passed++;
            System.out.println("  PASS  " + name);
        } else {
            failed++;
            System.out.println("  FAIL  " + name + "\n          expected: " + expected
                    + "\n          actual:   " + actual);
        }
    }

    private static void expectThrows(String name, Class<? extends Throwable> expected, Runnable body) {
        Throwable caught = null;
        try {
            body.run();
        } catch (Throwable t) {
            caught = t;
        }
        if (caught == null) {
            failed++;
            System.out.println("  FAIL  " + name + "\n          expected " + expected.getSimpleName()
                    + ", nothing was thrown");
        } else if (expected.isInstance(caught)) {
            passed++;
            System.out.println("  PASS  " + name);
        } else {
            failed++;
            System.out.println("  FAIL  " + name + "\n          expected " + expected.getSimpleName()
                    + ", got " + caught.getClass().getSimpleName()
                    + ": " + caught.getMessage());
        }
    }

    /** Reads the list front-to-back through the public API into a plain array. */
    private static Object[] contents(ArrayList<Integer> list) {
        Object[] out = new Object[list.size()];
        for (int i = 0; i < list.size(); i++) out[i] = list.get(i);
        return out;
    }

    private static String render(Object[] a) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < a.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(a[i]);
        }
        return sb.append("]").toString();
    }

    // ------------------------------------------------------------------ tests

    private static void testConstruction() {
        section("Construction & the size/capacity distinction");

        ArrayList<Integer> a = new ArrayList<>();
        checkEquals("new ArrayList<>() reports size 0", 0, a.size());
        check("new ArrayList<>() is empty", a.isEmpty());
        check("size() and isEmpty() agree on a fresh list", (a.size() == 0) == a.isEmpty());
        check("capacity() is independent of size()", a.capacity() >= a.size());

        ArrayList<Integer> b = new ArrayList<>(3);
        checkEquals("new ArrayList<>(3) reports size 0, not 3", 0, b.size());
        check("new ArrayList<>(3) is empty", b.isEmpty());

        b.addLast(42);
        checkEquals("size is 1 after one addLast", 1, b.size());
        check("not empty after one addLast", !b.isEmpty());
        check("size() and isEmpty() still agree", (b.size() == 0) == b.isEmpty());
    }

    private static void testGrowth() {
        section("Growth past the initial capacity");

        ArrayList<Integer> a = new ArrayList<>(2);
        for (int i = 0; i < 50; i++) a.addLast(i);

        checkEquals("50 elements added to a capacity-2 list -> size 50", 50, a.size());
        check("capacity grew to hold them", a.capacity() >= 50);

        boolean ordered = true;
        for (int i = 0; i < 50; i++) if (!Integer.valueOf(i).equals(a.get(i))) ordered = false;
        check("all 50 elements read back in insertion order", ordered);

        // The exact boundary where a lost "+1" in ensureCapacity/resize shows up.
        ArrayList<Integer> b = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) b.addLast(i);
        checkEquals("list is exactly full at capacity", 4, b.size());
        b.addLast(99);
        checkEquals("addLast on a full list grows instead of throwing", 5, b.size());
        checkEquals("the element that triggered growth is intact", 99, b.get(4));
        checkEquals("the element before it survived the copy", 3, b.get(3));
        checkEquals("the first element survived the copy", 0, b.get(0));
    }

    private static void testBounds() {
        section("Bounds are checked against size, not capacity");

        ArrayList<Integer> empty = new ArrayList<>(10);
        expectThrows("get(0) on an empty list throws",
                IndexOutOfBoundsException.class, () -> empty.get(0));
        expectThrows("get(5) on an empty list with capacity 10 throws",
                IndexOutOfBoundsException.class, () -> empty.get(5));
        expectThrows("get(-1) throws",
                IndexOutOfBoundsException.class, () -> empty.get(-1));

        ArrayList<Integer> a = new ArrayList<>(10);
        a.addLast(1);
        a.addLast(2);
        // index 2 is unused capacity: legal slot, illegal index
        expectThrows("get(size) throws even when capacity is larger",
                IndexOutOfBoundsException.class, () -> a.get(2));
        expectThrows("set(size, v) throws even when capacity is larger",
                IndexOutOfBoundsException.class, () -> a.set(2, 7));
        expectThrows("get(capacity) throws",
                IndexOutOfBoundsException.class, () -> a.get(a.capacity()));

        a.set(0, 100);
        checkEquals("set at a valid index takes effect", 100, a.get(0));
        checkEquals("set does not change size", 2, a.size());
    }

    private static void testNullRejection() {
        section("Null rejection (the heap depends on this)");

        ArrayList<Integer> a = new ArrayList<>();
        expectThrows("addLast(null) throws IllegalArgumentException",
                IllegalArgumentException.class, () -> a.addLast(null));
        checkEquals("a rejected addLast did not change size", 0, a.size());

        a.addLast(1);
        expectThrows("set(0, null) throws IllegalArgumentException",
                IllegalArgumentException.class, () -> a.set(0, null));
        checkEquals("a rejected set left the old value in place", 1, a.get(0));
    }

    private static void testRemoveLast() {
        section("removeLast");

        ArrayList<Integer> a = new ArrayList<>();
        expectThrows("removeLast on an empty list throws",
                NoSuchElementException.class, a::removeLast);

        for (int i = 0; i < 5; i++) a.addLast(i);
        checkEquals("removeLast returns the last element", 4, a.removeLast());
        checkEquals("size decremented", 4, a.size());
        checkEquals("removeLast again returns the new last", 3, a.removeLast());
        checkEquals("remaining contents", "[0, 1, 2]", render(contents(a)));

        while (!a.isEmpty()) a.removeLast();
        checkEquals("draining leaves size 0", 0, a.size());
        check("draining leaves isEmpty true", a.isEmpty());
        expectThrows("removeLast after draining throws",
                NoSuchElementException.class, a::removeLast);

        a.addLast(7);
        checkEquals("list is reusable after being drained", 7, a.get(0));
        checkEquals("size correct after reuse", 1, a.size());
    }

    private static void testRemoveFirst() {
        section("removeFirst & the shift");

        ArrayList<Integer> a = new ArrayList<>();
        expectThrows("removeFirst on an empty list throws",
                NoSuchElementException.class, a::removeFirst);

        for (int i = 0; i < 5; i++) a.addLast(i);
        checkEquals("removeFirst returns the first element", 0, a.removeFirst());
        checkEquals("size decremented", 4, a.size());
        checkEquals("remaining elements shifted down", "[1, 2, 3, 4]", render(contents(a)));
        expectThrows("the vacated tail slot is now out of bounds",
                IndexOutOfBoundsException.class, () -> a.get(4));

        checkEquals("second removeFirst", 1, a.removeFirst());
        checkEquals("contents after two removals", "[2, 3, 4]", render(contents(a)));

        ArrayList<Integer> single = new ArrayList<>();
        single.addLast(9);
        checkEquals("removeFirst on a one-element list", 9, single.removeFirst());
        check("one-element list is empty afterwards", single.isEmpty());
    }

    private static void testCopyConstructor() {
        section("Copy constructor & independence");

        ArrayList<Integer> orig = new ArrayList<>(4);
        for (int i = 0; i < 6; i++) orig.addLast(i * 10);

        ArrayList<Integer> copy = new ArrayList<>(orig);
        checkEquals("copy has the same size", orig.size(), copy.size());
        checkEquals("copy has the same contents", render(contents(orig)), render(contents(copy)));

        copy.addLast(999);
        checkEquals("appending to the copy left the original size alone", 6, orig.size());
        copy.set(0, -1);
        checkEquals("writing to the copy left the original value alone", 0, orig.get(0));

        orig.removeLast();
        checkEquals("removing from the original left the copy alone", 7, copy.size());
        checkEquals("copy's element 5 intact", 50, copy.get(5));

        ArrayList<Integer> emptySrc = new ArrayList<>(5);
        ArrayList<Integer> emptyCopy = new ArrayList<>(emptySrc);
        checkEquals("copy of an empty list has size 0", 0, emptyCopy.size());
        emptyCopy.addLast(1);
        checkEquals("copy of an empty list is still usable", 1, emptyCopy.size());
        checkEquals("original stayed empty", 0, emptySrc.size());

        expectThrows("copying null throws IllegalArgumentException",
                IllegalArgumentException.class, () -> new ArrayList<Integer>(null));
    }

    private static void testToString() {
        section("toString shows live elements only");

        ArrayList<Integer> a = new ArrayList<>(10);
        checkEquals("empty list with spare capacity prints []", "[]", a.toString());

        a.addLast(1);
        checkEquals("one element", "[1]", a.toString());

        a.addLast(2);
        a.addLast(3);
        checkEquals("three elements", "[1, 2, 3]", a.toString());

        a.removeLast();
        checkEquals("after removeLast", "[1, 2]", a.toString());

        a.removeFirst();
        checkEquals("after removeFirst", "[2]", a.toString());

        a.removeLast();
        checkEquals("back to empty", "[]", a.toString());

        ArrayList<String> s = new ArrayList<>();
        s.addLast("a");
        s.addLast("b");
        checkEquals("works for non-numeric T", "[a, b]", s.toString());
    }

    private static void testAgainstOracle() {
        section("Randomised differential test (5000 ops vs java.util.ArrayList)");

        ArrayList<Integer> mine = new ArrayList<>(2);
        java.util.ArrayList<Integer> oracle = new java.util.ArrayList<>();
        Random rnd = new Random(20260814L);

        boolean sizeOk = true, contentsOk = true, returnOk = true;
        int op = 0;

        for (; op < 5000; op++) {
            int roll = rnd.nextInt(100);

            if (roll < 55 || oracle.isEmpty()) {
                int v = rnd.nextInt(1000);
                mine.addLast(v);
                oracle.add(v);
            } else if (roll < 80) {
                Integer expected = oracle.remove(oracle.size() - 1);
                Integer actual = mine.removeLast();
                if (!expected.equals(actual)) returnOk = false;
            } else {
                Integer expected = oracle.remove(0);
                Integer actual = mine.removeFirst();
                if (!expected.equals(actual)) returnOk = false;
            }

            if (mine.size() != oracle.size()) { sizeOk = false; break; }
            for (int i = 0; i < oracle.size(); i++) {
                if (!oracle.get(i).equals(mine.get(i))) { contentsOk = false; break; }
            }
            if (!contentsOk || !returnOk) break;
        }

        check("size matched the oracle at every step", sizeOk);
        check("removal return values matched the oracle", returnOk);
        check("full contents matched the oracle at every step", contentsOk);
        if (!sizeOk || !contentsOk || !returnOk) {
            System.out.println("          diverged at operation " + op);
            System.out.println("          oracle: " + oracle);
            System.out.println("          mine:   " + render(contents(mine)));
        }
        System.out.println("          final size: " + oracle.size() + ", capacity: " + mine.capacity());
    }

    // ------------------------------------------------------------------ main

    public static void main(String[] args) {
        System.out.println("ArrayList<T> — test run");

        testConstruction();
        testGrowth();
        testBounds();
        testNullRejection();
        testRemoveLast();
        testRemoveFirst();
        testCopyConstructor();
        testToString();
        testAgainstOracle();

        System.out.println("\n" + "═".repeat(62));
        System.out.printf("  %d passed, %d failed, %d total%n", passed, failed, passed + failed);
        System.out.println("═".repeat(62));

        if (failed > 0) System.exit(1);
    }
}