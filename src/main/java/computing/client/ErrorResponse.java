package computing.client;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * Object with details about error
 */
@Data
public class ErrorResponse implements Serializable {

	private static final long serialVersionUID = -6423957500347638879L;

	@SerializedName("error")
	private String error;

	@SerializedName("error_description")
	private String description;

}
