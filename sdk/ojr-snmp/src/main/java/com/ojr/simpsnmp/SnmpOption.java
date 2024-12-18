package com.ojr.simpsnmp;

import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityLevel;

public class SnmpOption {
    private String community;
    private int retries;
    private int timeout;
    private int version;
    private int securityLevel;
    private String authPassword;
    private String privacyPassword;
    private String securityName;
    private String authType;
    private String privacyType;

    public SnmpOption() {
        this.community = "public";
        this.retries = 3;
        this.timeout = 450;
        this.version = SnmpConstants.version2c; //1
        this.securityLevel = SecurityLevel.NOAUTH_NOPRIV; //1
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        if (version == SnmpConstants.version3) {
            this.version = SnmpConstants.version3;
        } else {
            this.version = SnmpConstants.version2c;
        }
    }

    public int getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(int securityLevel) {
        if (securityLevel < SecurityLevel.NOAUTH_NOPRIV || securityLevel > SecurityLevel.AUTH_PRIV) {
            this.securityLevel = SecurityLevel.NOAUTH_NOPRIV;
        } else {
            this.securityLevel = securityLevel;
        }
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }

    public String getPrivacyPassword() {
        return privacyPassword;
    }

    public void setPrivacyPassword(String privacyPassword) {
        this.privacyPassword = privacyPassword;
    }

    public String getSecurityName() {
        return securityName;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getPrivacyType() {
        return privacyType;
    }

    public void setPrivacyType(String privacyType) {
        this.privacyType = privacyType;
    }
}
