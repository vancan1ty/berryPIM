package com.cvberry.util;

/**
 * Created by vancan1ty on 1/11/2016.
 */
public class AuthInfoHolder {
    public String username;
    public byte[] salt;
    public byte[] hash;

    //optional, should only be stored in memory and never written to disk.  Stored so that it can be forwarded
    //to git commands and the like.  Not optimal from a security perspective, but good enough I think.
    public String truePassword;

    public AuthInfoHolder(String username, byte[] salt, byte[] hash, String truePassword) {
        this.username = username;
        this.salt = salt;
        this.hash = hash;
        this.truePassword = truePassword;
    }
}
