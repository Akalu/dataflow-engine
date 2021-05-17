package computing.client.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;

public interface RestService {

	String getVersion();

	ResponseEntity<List> getData();
}
