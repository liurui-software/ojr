## Integrate Vault for the data collector(s)

To use vault in any data collector module, define the config class as per the yaml, keeping some basic structure intact:
	
  - Have all the instance credentials under the key "instances" and ensure that the key where the secret is to be added come before any other nested key.
  - Add the vault configuration in the yaml with the key "vault" and authType as shown in the example:
	- Example:
```
db.system: 
db.driver:  
  
instances:  
  - db.address: 
    db.username:   
    # ⬇️ key where secrets will be fetched must come before any other nested keys.
    db.password:  
      vault_secret.path: secret/kv/informix  
      vault_secret.key: informix-password  
    otel.service.name: 
    custom.poll.interval:  
      high:  
      medium: 
      low:

vault:
  connection_url: 
  token: # this is the auth type key, which can be different depending upon which auth type to use. Eg: github, approle.
  path_to_pem_file: 
  secret_refresh_rate: 
  kv_version: 

```

   - Define a config class as per the yaml, implement VaultImplementation interface and create a VaultServiceConfig object.
   - Example:
```
import com.fasterxml.jackson.annotation.JsonIgnore;  
import com.fasterxml.jackson.annotation.JsonProperty;  
import com.ojr.vault.VaultService;  
import com.ojr.vault.VaultServiceConfig;  
  
import java.util.ArrayList;  
import java.util.List;  
import java.util.Optional;  
import java.util.concurrent.ConcurrentHashMap;  
  
public class TestConfig implements VaultImplementation {  

    @JsonProperty("db.system")  
    private String dbSystem;  
    
    @JsonProperty("db.driver")  
    private String dbDriver;  
    
    private final List<ConcurrentHashMap<String, Object>> instances = new ArrayList<>();  
    
    @JsonProperty(value = "vault", required = false)  
    private VaultServiceConfig vaultConfig;  
  
    @Override  
    @JsonIgnore    
    public boolean isVaultServiceConfigPresent() {  
        return Optional.ofNullable(vaultConfig).isPresent();  
    }  
  
    @Override  
    public VaultServiceConfig getVaultServiceConfig() {  
        return vaultConfig;  
    }  
  
    public String getDbSystem() {  
        return dbSystem;  
    }  
  
    public String getDbDriver() {  
        return dbDriver;  
    }  
  
    public List<ConcurrentHashMap<String, Object>> getInstances() {  
        return instances;  
    }  
  
}
```

 - Finally, read the yaml and create the config object, which can be used in the VaultExecute.executeVault(<config>) method,
   which will create the vault client if the vault config is present, fetch the secrets and return the update config object which will have the secrets.

```
        private TestConfig dcConfig;
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.findAndRegisterModules();
        String configFile = System.getenv(CONFIG_ENV);
        if (configFile == null) {
            configFile = CONFIG_YAML;
        }
        dcConfig = objectMapper.readValue(new File(configFile), TestConfig.class);

      # ⬇️ returns the object with vault secrets updated in the config object.
        dcConfig = VaultExecute.executeVault(dcConfig);
```

### Building the module
Building the project will generate a shadow jar with the required dependencies, which can be used as the dependency in the respective Data Collector module.

To Build use the below command:

```
./gradlew shadowJar
```

### Add dependency
Once you have the jar handy, you can place it under the lib folder (sample path: `otel-dc/rdb/libs/vault-1.0.0.jar`) 
and add the dependency in your `build.gradle` file like this `implementation(files("libs/vault-1.0.0.jar"))`