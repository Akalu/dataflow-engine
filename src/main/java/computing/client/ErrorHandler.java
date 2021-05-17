package computing.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorHandler implements ResponseErrorHandler {

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		log.debug("======= Start Error report ========");
		Gson g = new Gson();
		String json = new BufferedReader(new InputStreamReader(response.getBody())).lines()
				.collect(Collectors.joining("\n"));
		ErrorResponse result = g.fromJson(json, ErrorResponse.class);
		log.debug(result.getDescription());
		log.debug("======= End Error report ========");
	}

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return !response.getStatusCode().is2xxSuccessful();
	}
}
