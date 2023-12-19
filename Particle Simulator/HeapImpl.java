import java.util.Arrays;

class HeapImpl<T extends Comparable<? super T>> implements Heap<T> {
	// Initial capacity for the heap.
	private static final int INITIAL_CAPACITY = 128;

	// The array storing the heap.
	private T[] _storage;

	// The number of elements in the heap.
	private int _numElements;

	/**
	 * Initialize a heap with a length given by the initial capacity variable and
	 * zero elements.
	 */
	@SuppressWarnings("unchecked")
	public HeapImpl() {
		_storage = (T[]) new Comparable[INITIAL_CAPACITY];
		_numElements = 0;
	}

	/**
	 * Adds a new value to the heap.
	 * 
	 * @param data the data to be added to the heap.
	 */
	@SuppressWarnings("unchecked")
	public void add(T data) {
		// Increase the length of the array if needed.
		if (_numElements == _storage.length) {
			_storage = Arrays.copyOf(_storage, _storage.length * 2);
		}

		// The index at which the new data will be added.
		int newIndex = _numElements;

		// Increase the number of elements by one.
		_numElements++;

		// Add the new data to the heap.
		_storage[newIndex] = data;

		// The index of the parent of the new data.
		int parentNodeIndex = (newIndex - 1) / 2;

		// Heapify up.
		while (_storage[parentNodeIndex].compareTo(_storage[newIndex]) < 0) {
			// Swap data in parent node with the new data.
			swap(newIndex, parentNodeIndex);

			// Exit if the root has been reached.
			if (parentNodeIndex == 0) {
				break;
			}

			// Change the index at which the new data is located.
			newIndex = parentNodeIndex;

			// The new index of the parent of the new data.
			parentNodeIndex = (newIndex - 1) / 2;
		}
	}

	/**
	 * Remove and return the first value of the heap.
	 * 
	 * @return the top value of the heap.
	 */
	public T removeFirst() {
		// If heap is empty return null.
		if (_numElements == 0) {
			return null;
		}

		// The index at which the value will be removed.
		int newIndex = 0;

		// Store the top value.
		T first = _storage[newIndex];

		// Decrease the number of elements by one.
		_numElements--;

		// Replace the top value with the last value.
		_storage[newIndex] = _storage[_numElements];

		// The indexes of the children of the now replaced top value.
		int childNodeIndexOne = (newIndex * 2) + 1;
		int childNodeIndexTwo = (newIndex * 2) + 2;

		// The largest value between the replaced top value and its two children.
		int childNodeIndexUse = findLargest(newIndex, childNodeIndexOne, childNodeIndexTwo);

		// Heapify down.
		while (_storage[newIndex].compareTo(_storage[childNodeIndexUse]) < 0) {
			// Swap the replaced top value with the data in its largest child.
			swap(newIndex, childNodeIndexUse);

			// The new location of the replaced top value
			newIndex = childNodeIndexUse;

			// The new indexes of the children of the replaced top value.
			childNodeIndexOne = (newIndex * 2) + 1;
			childNodeIndexTwo = (newIndex * 2) + 2;

			// The largest value between the replaced top value and its two new children.
			childNodeIndexUse = findLargest(newIndex, childNodeIndexOne, childNodeIndexTwo);
		}

		// Return the original root value.
		return first;
	}

	/**
	 * Find the current size of the heap.
	 * 
	 * @return the size of the heap.
	 */
	public int size() {
		// Return the number of elements that has been kept track of.
		return _numElements;
	}

	/**
	 * Swap the values at two indexes in the heap.
	 * 
	 * @param one the first index in the heap.
	 * @param two the second index in the heap.
	 */
	private void swap(int one, int two) {
		// Store the first value.
		T temp = _storage[one];

		// Change the first value to the second.
		_storage[one] = _storage[two];

		// Change the second value to the stored first.
		_storage[two] = temp;
	}

	/**
	 * Find the index of the largest value in the heap between three given indexes.
	 * 
	 * @param one   the first index in the heap.
	 * @param two   the second index in the heap.
	 * @param three the third index in the heap.
	 * @return the index of largest value in the heap between the three given
	 *         indexes.
	 */
	private int findLargest(int one, int two, int three) {
		// Set the answer to be one by default.
		int answer = one;

		// Set two to be the answer if it is within the bounds of the heap and it is
		// larger than the current answer.
		if (two < _numElements && _storage[two].compareTo(_storage[answer]) > 0) {
			answer = two;
		}

		// Set three to be the answer if it is within the bounds of the heap and it is
		// larger than the current answer.
		if (three < _numElements && _storage[three].compareTo(_storage[answer]) > 0) {
			answer = three;
		}

		// Return the stored answer.
		return answer;
	}
}
