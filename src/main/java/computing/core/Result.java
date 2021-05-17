package computing.core;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Used to track all future objects
 * 
 * @param <T> - type of transfer object
 */
public class Result<T> {

	// max length of history
	public static final int CAPACITY = 100;

	// autonumber
	private final String id;

	// history of changes
	private final Deque<Event<T>> history;

	public Result() {
		this.id = UUID.randomUUID().toString();
		this.history = new LinkedList<>();
	}

	public synchronized void updateHistory(Class<?> className, CompletableFuture<T> future) {
		if (history.size() > CAPACITY) {
			history.pollFirst();
		}
		history.add(new Event<T>(className, future));
	}

	public synchronized void updateHistory(Class<?> className, Exception e) {
		if (history.size() > CAPACITY) {
			history.pollFirst();
		}
		history.add(new Event<T>(className, e));
	}

	public synchronized Event<T> pollEvent() {
		return history.getLast();
	}

	/**
	 * Used to analyze successfully finished segments of chain
	 * 
	 * @return
	 */
	public synchronized Event<T> pollEventWithData() {
		Iterator<Event<T>> interator = history.descendingIterator();
		while (interator.hasNext()) {
			Event<T> elem = interator.next();
			if (Boolean.TRUE.equals(elem.getHasData())) {
				return elem;
			}
		}
		return null;
	}

	public List<Event<T>> getHistory() {
		return new ArrayList<>(history);
	}

	public String getId() {
		return id;
	}

	/*
	 * output in json form
	 */
	@Override
	public String toString() {
		return "{" + "id=" + id + ", " + "history=" + history.toString() + '}';
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		Result<T> other = (Result<T>) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
