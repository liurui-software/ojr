package com.ojr.host;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ojr.core.BasicDcConfig;

public class HostDcConfig extends BasicDcConfig {
    @JsonProperty("host.system")
    private String hostSystem;

    public String getHostSystem() {
        return hostSystem;
    }
}
