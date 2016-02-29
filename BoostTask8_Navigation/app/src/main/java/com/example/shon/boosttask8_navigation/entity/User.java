package com.example.shon.boosttask8_navigation.entity;

public class User {

    private String mUid;
    private String mDisplayName;
    private String mEmail;
    private String mProfileImageUrl;

    public User() {

    }

    public User( String mUid, String mDisplayName, String mEmail, String mProfileImageUrl) {
        this.mDisplayName = mDisplayName;
        this.mEmail = mEmail;
        this.mUid = mUid;
        this.mProfileImageUrl = mProfileImageUrl;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String mUid) {
        this.mUid = mUid;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String mDisplayName) {
        this.mDisplayName = mDisplayName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getProfileImageUrl() {
        return mProfileImageUrl;
    }

    public void setProfileImageUrl(String mProfileImageUrl) {
        this.mProfileImageUrl = mProfileImageUrl;
    }
}
