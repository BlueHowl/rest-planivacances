package be.helmo.planivacances.model;

import javax.validation.constraints.NotNull;

public class ConfigurationPusher {
    @NotNull
    private String appId;

    @NotNull
    private String key;

    @NotNull
    private String secret;

    @NotNull
    private String cluster;

    public String getAppId() {
        return appId;
    }

    public String getKey() {
        return key;
    }

    public String getSecret() {
        return secret;
    }

    public String getCluster() {
        return cluster;
    }
}
