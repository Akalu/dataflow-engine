package computing.client.vault;

import org.springframework.http.ResponseEntity;

import computing.model.Secret;
import computing.model.SecretTO;

public interface VaultService {
	ResponseEntity<Secret> getSecret(SecretTO request);

	String version();
}
