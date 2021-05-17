package computing.core;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import lombok.Data;
import lombok.ToString;

/**
 * The holder for element of history Time is tracked automatically
 */
@Data
@ToString
public class Event<T> {
	// the latest future object
	private CompletableFuture<T> future;
	private Boolean hasData;

	private final Class<?> className;
	private final Date date;
	private Throwable error;

	public Event(Class<?> className) {
		this.date = Date.from(Instant.now());
		this.hasData = true;
		this.className = className;
	}

	public Event(Class<?> className, CompletableFuture<T> future) {
		this.future = future;
		this.hasData = true;
		this.date = Date.from(Instant.now());
		this.className = className;
	}

	public Event(Class<?> className, Throwable error) {
		this.hasData = false;
		this.error = error;
		this.date = Date.from(Instant.now());
		this.className = className;
	}

	public Optional<Throwable> getError() {
		return Optional.ofNullable(error);
	}

	public void setError(Throwable error) {
		this.error = error;
	}

}
