package computing.core;

import java.util.UUID;
import org.codejargon.feather.Provides;

import computing.core.Binding;
import computing.core.Configuration;

public class TestConfig implements Configuration {
	
	@Provides
    Binding getBinding() {
		String bpId = UUID.randomUUID().toString();
		Binding binding = new Binding();
		binding.add("bpId", bpId);
		return binding;
    }


}
