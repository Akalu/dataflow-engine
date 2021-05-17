package computing.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

public class Binding implements Serializable {
	private static final long serialVersionUID = 2357113637981299253L;

	@Getter
	private final Map<String, String> data = new HashMap<>();

	public String get(String key) {
		return this.data.getOrDefault(key, null);
	}

	public String add(String key, String value) {
		return this.data.put(key, value);
	}

	@Override
	public String toString() {
		return this.data.toString();
	}
}
