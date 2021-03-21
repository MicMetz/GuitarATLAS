/*
 #############################################################################
 ###                                                                       ###
 ### Title:         Guitar Hero                                            ###
 ###                                                                       ###
 ### Files:         FixedSizeQueue.java            	                       ###
 ### Author(s):     Michael Metz (mime9599@colorado.edu)                   ###
 ### Semester:      Spring 2021                                            ###
 ### Written:       March 16, 2021                                         ###
 ### Description:   Simulates plucking a guitar string using the           ###
 ###				 Karplus-Strong algorithm                              ###
 ### License:                                                              ###
 ### Credits:                                                              ###
 #############################################################################
 */

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A generic Queue with a fixed capacity implemented via a linked-list
 * following a FIFO policy. It would actually be more efficient to implement
 * this using an array (i.e., as a ring buffer), but we will implement it as
 * a linked-list instead since the implementation is simpler. Re-implementing this
 * as a ring buffer would be a great exercise if you want to push yourself - if so,
 * see the course slides as a reference.
 *
 * @param <T> The type of object that the FixedSizeQueue will hold
 * @author Michael M
 */
public class FixedSizeQueue<T> implements Iterable<T> {

	/**
	 * Internal node class for the linked-list data structure
	 *
	 * @author Michael M
	 */
	private class Node {
		T    value; // The data the node holds
		Node next;  // A pointer to the next node in the list (or null if this is the last node)


		/**
		 * Create a new Node
		 *
		 * @param value The value this node should store
		 */
		public Node(T value) {
			this.value = value;
			this.next = null;
		}
	}

	// Instance data
	private Node      head, tail; // Keep track of the first and last node in the list
	private final int capacity; // Keep track of how many nodes are in the list and our total capacity



	/**
	 * Create a new FixedSizeQueue with a specified capacity
	 * <p>
	 * Pseudocode:
	 * All you have to do here is set the capacity instance variable
	 *
	 * @param capacity How many items this queue will be able to hold
	 */
	public FixedSizeQueue(int capacity) {
		this.capacity = capacity;
		this.head = this.tail = null;
	}



	/**
	 * Get the number of nodes in the queue
	 *
	 * @return the number of nodes in the queue
	 */
	public int size() {
		if (isEmpty()) {
			System.out.println("The que is empty.");
			return 0;
		} else {
			Node iter = this.head;
			int  i    = 0;
			while (iter != tail) {
				iter = iter.next;
				i++;
			}

			return i;
		}
	}


	/**
	 * Check if the queue is empty
	 *
	 * @return true if the queue is empty (has no nodes), false otherwise
	 */
	public boolean isEmpty() {
		return this.head == null;
	}



	/* Check if the queue is empty
	 *
	 * @return true if the queue is full (size == capacity), false otherwise
	 */
	public boolean isFull() {
		int check = size();
		return check == capacity-1;
	}



	/**
	 * Add a new node based on a given value to the end of the queue.
	 * Psuedocode:
	 * 1. If the queue is already full (i.e., if the size equals the capacity):
	 * A. Throw a new IllegalStateException with a helpful message (e.g., stating
	 * that the queue is already full)
	 * [if you are struggling, see the syntax in dequeue below for an example
	 * of how to throw exceptions and provide a message]
	 * 2. If the list is currently empty:
	 * A. Create a new Node based on the passed in value
	 * B. Set your head and tail pointers to this new node
	 * 3. Else:
	 * A. Save the old tail pointer to a temporary variable
	 * B. Create a new Node based on the passed in value and set the tail
	 * to be this new node
	 * C. Set the .next value of the temporary variable (which is equal to the
	 * node that used to be the tail node) to point to the new tail node
	 * 4. Increment the size counter
	 *
	 * @param value The given value, which will be used to create a new node that is added
	 *              to the end of the queue.
	 * @throws IllegalStateException if the queue is already full (i.e., if the size equals
	 *                               the capacity)
	 */
	public void enqueue(T value) {
/*		if (isFull()) { System.out.println("Error - " + "can't enqueue. The queue is full."); }
		else {*/
		if (isEmpty()) {
			this.head = new Node(value);
			this.tail = this.head;
		} else {
			if (!isFull()) {
				Node temp = new Node(value);
				this.tail.next = temp;
				this.tail = temp;
			}
		}
	}



	/**
	 * Removes the first node in the list and returns its value.
	 * Pseudocode:
	 * 1. Create a temporary variable to store the head node
	 * 2. Set the head node to be the next node in the queue
	 * 3. Decrement the size counter
	 * 4. Return the value in the temporary variable that stores the
	 * old head node
	 *
	 * @return The value of the first node in the list
	 * @throws NoSuchElementException if the list is empty
	 */
	public T dequeue() {
		T val = null;

		if (!isEmpty()) {
			val = this.head.value;
			this.head = this.head.next;
			if (this.head == null) {
				this.tail = null;
			}
		}
		return val;
	}



	/**
	 * Return the value of the first node in the queue without removing that node from the
	 * queue.
	 * <p>
	 * Pseudocode:
	 * 1. Check if the queue is empty and, if so, throw a NoSuchElementException just as
	 * in dequeue
	 * <p>
	 * 2. Return the value stored in the head node
	 *
	 * @return The value of the first node in the queue
	 * @throws NoSuchElementException if the queue is empty
	 */
	public T peek() {
		//		if (size == 0) throw new NoSuchElementException("Error - " + "can't peek as the queue is empty!");
		if (isEmpty()) { return null; }
		return this.head.value;
	}



	/**
	 * TODO
	 */
	@Override
	public Iterator<T> iterator() {
		return new ListIterator();
	}



	/**
	 * TODO
	 * expand
	 */
	private class ListIterator implements Iterator<T> {

		// The current node we are iterating over (start at the front of the queue)
		private Node current = head;



		// The Iterator methods
		public boolean hasNext() {
			return current != null;
		}



		public void remove() {  /* not supported */ }



		public T next() {
			T value = current.value;
			current = current.next;
			return value;
		}

	}
}
