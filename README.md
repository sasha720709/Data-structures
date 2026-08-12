# Data Structures in Java

Data structures implemented from scratch, without `java.util` collections, as a way of learning how they actually work.

This is a study repository. The goal is understanding, not production use — for real code, use `java.util`.

## Why

Reading about a doubly linked list takes ten minutes. Writing one exposes everything the description skips: what happens when the list has one element, what `remove` does to an in-progress iteration, why `head.prev` being `null` forces a branch into every method that touches the front.

Each structure is built up to a full public API rather than stopping at the core operations, because the awkward methods are where the design decisions surface.

## Contents

| Structure | Notes |
|---|---|
| `MySinglyLinkedList<T>` | Forward-only traversal, `Iterable` |
| `MyDoublyLinkedList<T>` | Null-terminated, `Iterable`, fail-fast iterator with O(1) `remove()` |
| `DynamicArray<T>` | `Array<T>`, `Object[]`-backed, doubling growth, deliberately no `Node<T>` wrapper |
| `LinkedStack<T>` | `Stack<T>`, composes a singly linked list by wrapping it, not extending it |
| `ArrayStack<T>` | `Stack<T>`, composes `DynamicArray<T>` |
| `LinkedQueue<T>` | `Queue<T>`, composes a singly linked list, tail-enqueue / head-dequeue |
| `ArrayQueueNaive<T>` | `Queue<T>`, O(n) dequeue by design — built specifically to feel that cost, not meant to be kept |
| `ArrayQueue<T>` | `Queue<T>`, circular buffer, `size` counter resolves the `head == tail` ambiguity |

> Naming note: the stack/queue work uses `LinkedList<T>` (no `My` prefix) as the backing class, which doesn't match the `MySinglyLinkedList<T>` / `MyDoublyLinkedList<T>` convention above — worth reconciling one way or the other before this grows further.
