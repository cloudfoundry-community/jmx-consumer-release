package org.cloudfoundry.jmxconsumer.jmx;

import org.cloudfoundry.jmxconsumer.Config;
import sun.security.acl.PrincipalImpl;

import javax.management.remote.JMXAuthenticator;
import javax.management.remote.JMXPrincipal;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.security.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static org.bouncycastle.cms.RecipientId.password;

public class JmxShaFileAuthenticator implements JMXAuthenticator {
    private Properties userCredentials;

    public JmxShaFileAuthenticator(String passwordFile) throws IOException {
        FileInputStream fis;
        fis = new FileInputStream(passwordFile);
        try {
            final BufferedInputStream bis = new BufferedInputStream(fis);
            try {
                userCredentials = new Properties();
                userCredentials.load(bis);
            } finally {
                bis.close();
            }
        } finally {
            fis.close();
        }
    }

    @Override
    public Subject authenticate(Object credentials) {
        if (!(credentials instanceof String[])) {
            throw new SecurityException("Was expected credentials String[2] object");
        }
        String[] usernamePassword = (String[]) credentials;
        if (usernamePassword.length != 2 || usernamePassword[0] == null || usernamePassword[1] == null) {
            authenticationFailure();
        }
        String username = usernamePassword[0];
        String password = usernamePassword[1];

        String passwordForUser = userCredentials.getProperty(username);

        if (passwordForUser == null) {
            authenticationFailure();
        }
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            password = DatatypeConverter.printHexBinary(crypt.digest(password.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            authenticationFailure();
        }
        if (!passwordForUser.equalsIgnoreCase(password)) {
            authenticationFailure();
        }

        Set<Principal> principals = new HashSet<Principal>();
        principals.add(new JMXPrincipal(username));
        return new Subject(true, principals, Collections.emptySet(), Collections.emptySet());
    }

    private static void authenticationFailure()
            throws SecurityException {
        final String msg = "Authentication failed. Invalid username or password";
        throw new SecurityException(msg);
    }

}
