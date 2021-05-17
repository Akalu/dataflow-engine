package computing.model;

import java.io.Serializable;
import java.util.HashMap;

public class Secret extends HashMap<String, String> implements Serializable {
	private static final long serialVersionUID = 4142126674185917401L;

	public String getKey() {
		if (entrySet().isEmpty()) {
			return null;
		} else {
			return this.entrySet().iterator().next().getKey();
		}
	}

	public String getValue() {
		if (entrySet().isEmpty()) {
			return null;
		} else {
			return this.entrySet().iterator().next().getValue();
		}
	}
}
