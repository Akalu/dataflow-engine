package computing.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class Listing {
	
	@Getter
	private final List<String> list;

	public Listing() {
		list = new ArrayList<>();
	}

	public String add(String string) {
		list.add(string);
		return string;
	}

	public String toString() {
		return list.toString();
	}

}
