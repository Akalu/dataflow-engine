package computing.model;

import java.util.UUID;

import javax.inject.Singleton;

import org.codejargon.feather.Provides;

import computing.client.rest.RestService;
import computing.client.rest.RestServiceImpl;
import computing.client.vault.VaultService;
import computing.client.vault.VaultServiceImpl;
import computing.core.Binding;
import computing.core.Configuration;

/**
 * Defines providers for objects to be instantiated on classes marked
 * with @Inject annotation
 */
public class DefaultConfiguration implements Configuration {

	@Provides
	@Singleton
	VaultService vaultService() {
		return new VaultServiceImpl();
	}

	@Provides
	@Singleton
	RestService restService() {
		return new RestServiceImpl();
	}

	@Provides
	@Singleton
	Binding getBinding() {
		String bpId = UUID.randomUUID().toString();
		Binding binding = new Binding();
		binding.add("bpId", bpId);
		return binding;
	}

}
