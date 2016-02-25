package com.example.vasia.mytask8.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class User {
    private String displayName;
    private String provider;

    public User(){

    }

    public User(String displayName, String provider) {
        this.displayName = displayName;
        this.provider = provider;
    }
    public String getDisplayName() {
        return displayName;
    }
    public String getProvider() {
        return provider;
    }
}
