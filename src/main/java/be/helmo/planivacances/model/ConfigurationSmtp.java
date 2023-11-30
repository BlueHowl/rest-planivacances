package be.helmo.planivacances.model;

import javax.validation.constraints.NotNull;

public class ConfigurationSmtp {
    @NotNull
    private String smtpHost;

    private int smtpPort;

    @NotNull
    private String smtpUsername;

    @NotNull
    private String smtpPassword;

    public String getSmtpHost() {
        return smtpHost;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public String getSmtpUsername() {
        return smtpUsername;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }
}

