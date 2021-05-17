package computing.client.rest;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import computing.client.RestTemplateFactory;

/**
 * Example of implementation of RestTemplateFactory
 */
public class RestServiceImpl implements RestService {

	private final String baseURLTemplate;

	public RestServiceImpl() {
		baseURLTemplate = "https://127.0.0.1:8443/webdata";
	}

	public RestServiceImpl(String endpoint) {
		baseURLTemplate = endpoint;
	}

	public static String getUrl(String templ, Object... args) {
		return String.format(templ, args);
	}

	@Override
	public ResponseEntity<List> getData() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> request = new HttpEntity<>(headers);
		return RestTemplateFactory.getInstance().getTemplate().exchange(getUrl(baseURLTemplate), HttpMethod.GET,
				request, List.class);
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

}
