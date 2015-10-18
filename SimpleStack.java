/**
 * A simplified Stack data structure implementation for usage in the project
 * @author Sahil Pujari (pujari@purdue.edu)
 */
import java.util.LinkedList;

public class SimpleStack<E> {

	private LinkedList<E> list = new LinkedList<E>();

	public void push(E o) {
		list.addFirst(o);
	}

	public E pop() {
		if (list.isEmpty()) {
			return null;
		}
		return list.removeFirst();
	}

	public E peek() {
		return list.getFirst();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public int size() {
		return list.size();
	}
}