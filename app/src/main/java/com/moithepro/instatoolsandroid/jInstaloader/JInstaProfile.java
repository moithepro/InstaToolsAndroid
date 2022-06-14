package com.moithepro.instatoolsandroid.jInstaloader;

public class JInstaProfile {
    private String username;
    private String fullName;
    private String id;
    private int followers;
    private int following;
    private String profilePictureUrl;
    private boolean verified;

    public JInstaProfile(String username, String fullName, String id, int followers, int following, String profilePictureUrl, boolean verified) {
        this.username = username;
        this.fullName = fullName;
        this.id = id;
        this.followers = followers;
        this.following = following;
        this.profilePictureUrl = profilePictureUrl;
        this.verified = verified;
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }

    public int getFollowers() {
        return followers;
    }

    public int getFollowing() {
        return following;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
}
