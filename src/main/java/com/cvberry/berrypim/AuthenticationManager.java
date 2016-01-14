package com.cvberry.berrypim;

import com.cvberry.util.AuthInfoHolder;
import com.cvberry.util.PasswordHasherLite;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by vancan1ty on 1/7/2016.
 *
 * My implementation of an authentication management solution.
 * automatically detect if password file berrypim_passwords.txt is on classpath.
 * if not, then disable authentication functionality and just let all requests through.
 *
 */

public class AuthenticationManager {

    private Map<String,AuthInfoHolder> authMap;
    PasswordHasherLite pwHasher;
    public boolean authenticationFeatureEnabled;

    public AuthenticationManager(String passwordFilePath) throws IOException, NoSuchAlgorithmException {

        String pwFileStr= readPWFileToString(passwordFilePath);
        if(pwFileStr==null) {//then the person who started the app is not requesting authentication abilities.
            authenticationFeatureEnabled = false;
        } else {
            authenticationFeatureEnabled = true;
            authMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            pwHasher = new PasswordHasherLite();
            BufferedReader strReader = new BufferedReader(new StringReader(pwFileStr));
            String line = strReader.readLine();
            while (line != null) {
                AuthInfoHolder holder = PasswordHasherLite.retrieveSaltAndHash(line);
                authMap.put(holder.username,holder);
                line = strReader.readLine();
            }
        }
    }

    /**
     * [CB] designed so that I can mock this method.
     * @return
     * @throws IOException
     */
    public String readPWFileToString(String passwordFilePath) throws IOException {
        //String configStr = Utility.convertStreamToString(ConfigXMLFileFinder.getConfigXMLStream());
        //String filePath = Utility.runXPathOnString(configStr,"/config/pwfile/text()").trim();
        if(passwordFilePath == null) {
            return null;//indicates that we should not enforce auth on this session.
        }
        File passwordFile = new File(passwordFilePath);
        if(passwordFile == null || !passwordFile.exists()) {
            throw new FileNotFoundException("couldn't locate specified passwords file!");
        }

        StringBuilder out = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(passwordFile));
        String line = reader.readLine();
        while(line != null) {
            if(!line.isEmpty()) {
                out.append(line+"\n");
            }
            line = reader.readLine();
        }
        return out.toString();

    }

    public boolean authenticateUser(String username, String givenPassword) throws UnsupportedEncodingException {
        if(!authMap.containsKey(username)) {
            return false;
        }

        AuthInfoHolder holder = authMap.get(username);
        byte[] givenPasswordBytes = givenPassword.getBytes("UTF-8");
        boolean pwValid = pwHasher.validatePassword(givenPasswordBytes,holder.salt,holder.hash);
        if(pwValid) {
            holder.truePassword = givenPassword;
        }
        return pwValid;
    }

    public AuthInfoHolder getAuthInfoForUsername(String username) {
       return this.authMap.get(username);
    }

}
