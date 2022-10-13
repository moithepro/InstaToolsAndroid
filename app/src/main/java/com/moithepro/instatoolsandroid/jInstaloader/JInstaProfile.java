package com.moithepro.instatoolsandroid.jInstaloader;

import java.io.Serializable;

import androidx.annotation.Nullable;

public class JInstaProfile implements Serializable {
    private String username;
    private String fullName;
    private long id;
    private long followers;
    private long following;
    private String profilePictureUrl;
    private boolean verified;

    public JInstaProfile(String username, String fullName, long id, long followers, long following, String profilePictureUrl, boolean verified) {
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

    public long getId() {
        return id;
    }

    public long getFollowers() {
        return followers;
    }

    public long getFollowing() {
        return following;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof JInstaProfile))
            return false;
        else return ((JInstaProfile) obj).username.equals(this.username);
    }

    public boolean isVerified() {
        return verified;
    }
}
