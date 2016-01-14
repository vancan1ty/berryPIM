package com.cvberry.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * [CB 1/7/2016] Like the other class, but doing things much less securely (PBFK2 is just too slow).
 */
public class PasswordHasherLite {

    SecureRandom random;
    MessageDigest digest;
    public PasswordHasherLite() throws NoSuchAlgorithmException {
        random = new SecureRandom();
        digest = MessageDigest.getInstance("MD5");
    }

    public static final int SALT_BYTE_SIZE = 24;

    public String createUsernameSaltHashStr(String username, byte[] password)
            throws UnsupportedEncodingException {
        // Generate a random salt
        byte[] salt = new byte[SALT_BYTE_SIZE];
        random.nextBytes(salt);

        byte[] md5results = md5(salt,password);

        // format salt:hash
        return username+":"+toHex(salt) + ":" + toHex(md5results);
    }


    public boolean validatePassword(byte[] givenPassword, byte[] salt, byte[] correctHash) throws UnsupportedEncodingException {
        byte[] candidateHash = md5(salt,givenPassword);
        return Arrays.equals(correctHash,candidateHash);
    }

    public byte[] md5(byte[] salt, byte[] password) throws UnsupportedEncodingException {
        byte[] joinedBytes = new byte[salt.length+password.length];
        System.arraycopy(salt,0,joinedBytes,0,salt.length);
        System.arraycopy(password,0,joinedBytes,salt.length,password.length); //concatenate salt and password arrays.

        // Hash the salt+password
        byte[] candidateHash = digest.digest(joinedBytes);
        return candidateHash;
    }

    public static AuthInfoHolder retrieveSaltAndHash(String combinedStr) throws UnsupportedEncodingException {
        // Decode the hash into its parameters
        String[] params = combinedStr.split(":");
        String username = params[0];
        byte[] salt = fromHex(params[1]);
        byte[] hash = fromHex(params[2]);
        return new AuthInfoHolder(username,salt,hash,null);
    }

    /**
     * Converts a string of hexadecimal characters into a byte array.
     *
     * @param   hex         the hex string
     * @return              the hex string decoded into a byte array
     */
    private static byte[] fromHex(String hex)
    {
        byte[] binary = new byte[hex.length() / 2];
        for(int i = 0; i < binary.length; i++)
        {
            binary[i] = (byte)Integer.parseInt(hex.substring(2*i, 2*i+2), 16);
        }
        return binary;
    }

    /**
     * Converts a byte array into a hexadecimal string.
     *
     * @param   array       the byte array to convert
     * @return              a length*2 character string encoding the byte array
     */
    private static String toHex(byte[] array)
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
            return String.format("%0" + paddingLength + "d", 0) + hex;
        else
            return hex;
    }


    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        PasswordHasherLite liteHasher = new PasswordHasherLite();
        System.out.println(liteHasher.createUsernameSaltHashStr(args[0],args[1].getBytes("UTF-8")));
    }


}
