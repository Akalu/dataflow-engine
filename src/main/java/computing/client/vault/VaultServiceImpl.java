package computing.client.vault;

import java.util.Properties;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.google.gson.Gson;

import computing.boot.Configurator;
import computing.client.RestTemplateFactory;
import computing.model.Secret;
import computing.model.SecretTO;

public class VaultServiceImpl implements VaultService {

	private static final String APP_PATH = "/boot.properties";

	private String baseURLTemplate;

	public VaultServiceImpl() {
		Properties prop = Configurator.loadConfig(APP_PATH);
		if (prop.containsKey("vaultservice.url")) {
			baseURLTemplate = prop.getProperty("vaultservice.url");
		} else {
			throw new IllegalArgumentException("Mandatory property vaultservice.url is not found");
		}
	}

	public VaultServiceImpl(String endpoint) {
		baseURLTemplate = endpoint;
	}

	public static String getUrl(String templ, Object... args) {
		return String.format(templ, args);
	}

	@Override
	public ResponseEntity<Secret> getSecret(SecretTO req) {
		Gson g = new Gson();
		HttpEntity<?> request = new HttpEntity<>(g.toJson(req), getHeaders());

		return RestTemplateFactory.getInstance().getTemplate().exchange(getUrl(baseURLTemplate), HttpMethod.POST,
				request, Secret.class);
	}

	@Override
	public String version() {
		HttpEntity<?> request = new HttpEntity<>(getHeaders());

		ResponseEntity<String> response = RestTemplateFactory.getInstance().getTemplate()
				.exchange(getUrl(baseURLTemplate), HttpMethod.GET, request, String.class);
		return response.getBody();
	}

	private HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;

	}

}
