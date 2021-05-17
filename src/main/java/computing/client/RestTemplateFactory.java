package computing.client;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestTemplateFactory {

	public static final int TIMEOUT = 5000;

	private final ErrorHandler errorHandler;
	private static volatile RestTemplateFactory instance = null;
	private ClientHttpRequestFactory factory;
	private CloseableHttpClient client;

	private RestTemplateFactory() {
		initFactory();
		errorHandler = new ErrorHandler();
	}

	public static RestTemplateFactory getInstance() {
		if (instance == null) {
			instance = new RestTemplateFactory();
		}
		return instance;
	}

	public RestTemplate getTemplate() {
		RestTemplate restTempl = new RestTemplate(factory);
		restTempl.setErrorHandler(errorHandler);
		return restTempl;
	}

	public void close() {
		try {
			client.close();
		} catch (Exception e) {
			log.error("", e);
		}
	}

	private void initFactory() {

		RequestConfig config = RequestConfig.custom().setConnectTimeout(TIMEOUT).setConnectionRequestTimeout(TIMEOUT)
				.setAuthenticationEnabled(true).setSocketTimeout(TIMEOUT).build();

		client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

		factory = new HttpComponentsClientHttpRequestFactory(client);
	}

}
