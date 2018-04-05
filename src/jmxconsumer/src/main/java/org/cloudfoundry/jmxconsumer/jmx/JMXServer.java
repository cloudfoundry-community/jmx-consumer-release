package org.cloudfoundry.jmxconsumer.jmx;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.cloudfoundry.logging.LoggingInterceptor;

import javax.management.JMException;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.MBeanServerForwarder;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import static javax.management.remote.rmi.RMIConnectorServer.RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE;
import static javax.management.remote.rmi.RMIConnectorServer.RMI_SERVER_SOCKET_FACTORY_ATTRIBUTE;

public class JMXServer {
    private Map<String, Object> env = new HashMap<>();
    private JMXConnectorServer jmxConnectorServer;
    private int registryPort;
    private int serverPort;
    Registry registry;

    public JMXServer(int registryPort, int serverPort, String passwordFile, String accessFile,
                     String certFile, String keyFile) throws Exception {
        this.registryPort = registryPort;
        this.serverPort = serverPort;
        if (certFile != null) {
            addSSLToServer(certFile, keyFile);
        }

        this.env.put("jmx.remote.authenticator", new JmxShaFileAuthenticator(passwordFile));
    }

    private void addSSLToServer(String certFile, String keyFile) throws Exception {
        final String password = "";
        Security.addProvider(new BouncyCastleProvider());

        String certString = readStringFromFile(certFile);
        X509CertificateHolder cert = (X509CertificateHolder) parsePEMObject(certString);
        String keyString = readStringFromFile(keyFile);
        PEMKeyPair pkey = (PEMKeyPair) parsePEMObject(keyString);

        if (pkey == null) {
            throw new Exception("Missing private key");
        }

        KeyStore keyStore = initializeKeyStore(cert, password, pkey);
        KeyManagerFactory keyManagerFactory = initializeKeyManagerFactory(password, keyStore);
        TrustManagerFactory trustManagerFactory = initializeTrustManagerFactory(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

        SslRMIClientSocketFactory clientSocketFactory = new SslRMIClientSocketFactory();
        SslRMIServerSocketFactory serverSocketFactory = new SslRMIServerSocketFactory(
                sslContext,
                new String[]{"TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256","TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384"},
                new String[]{"TLSv1.2"},
                false);

        this.env.put(RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE, clientSocketFactory);
        this.env.put(RMI_SERVER_SOCKET_FACTORY_ATTRIBUTE, serverSocketFactory);
    }

    private TrustManagerFactory initializeTrustManagerFactory(KeyStore keyStore) throws Exception {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("PKIX");
        trustManagerFactory.init(keyStore);
        return trustManagerFactory;
    }

    private KeyManagerFactory initializeKeyManagerFactory(String password, KeyStore keyStore) throws Exception {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("PKIX");
        keyManagerFactory.init(keyStore, password.toCharArray());
        return keyManagerFactory;
    }

    private KeyStore initializeKeyStore(X509CertificateHolder cert, String password, PEMKeyPair pkey) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null);
        X509Certificate certificate = new JcaX509CertificateConverter().getCertificate(cert);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
        keyStore.setCertificateEntry("service-provider-cert", certificate);
        keyStore.setKeyEntry("service-provider-cert", converter.getKeyPair(pkey).getPrivate(), password.toCharArray(), new Certificate[]{certificate});
        return keyStore;
    }

    private String readStringFromFile(String filename) throws IOException {
        return(new String(Files.readAllBytes(Paths.get(filename))));
    }

    private Object parsePEMObject(String certString) throws IOException {
        PEMParser reader = new PEMParser(new InputStreamReader(new ByteArrayInputStream(certString.getBytes())));
        return reader.readObject();
    }

    public void start() throws JMException, IOException {
        registry = LocateRegistry.createRegistry(registryPort);

        jmxConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(
                new JMXServiceURL(String.format("service:jmx:rmi://localhost:%d/jndi/rmi://localhost:%d/jmxrmi", serverPort, registryPort)),
                this.env,
                ManagementFactory.getPlatformMBeanServer()
        );


        MBeanServerForwarder proxy = LoggingInterceptor.newProxyInstance();
        jmxConnectorServer.setMBeanServerForwarder(proxy);

        jmxConnectorServer.start();
    }

    public void stop() throws IOException {
        jmxConnectorServer.stop();
        UnicastRemoteObject.unexportObject(registry, true);

    }

}
