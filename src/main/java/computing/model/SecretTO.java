package computing.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class SecretTO implements Serializable {
	private static final long serialVersionUID = -1542738379837993189L;

	private String uuid;
	private String alias;
	private String key;
	private String value;

	public SecretTO() {
	}

	public SecretTO(String alias) {
		this.alias = alias;
	}


}
