package org.cloudfoundry.logging;

import org.cloudfoundry.jmxnozzle.Config;

import javax.management.MBeanServer;
import javax.management.remote.JMXPrincipal;
import javax.management.remote.MBeanServerForwarder;
import javax.security.auth.Subject;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.Set;
import java.util.logging.Logger;

public class LoggingInterceptor implements InvocationHandler {
    boolean loggingEnabled = Config.getSecurityLoggingEnabled();
    private final String VENDOR = "cloud_foundry";
    private final String PRODUCT = "jmx_nozzle";
    private final int SEVERITY = 5;
    private MBeanServer mbs;
    private String serverIpAddress ;
    private LogFormatter securityLogFormatter;

    private static final Logger logger = Logger.getLogger(LoggingInterceptor.class.getName());

    public LoggingInterceptor(){
        super();
        try{
            serverIpAddress = InetAddress.getLocalHost().getHostAddress();
        }catch(UnknownHostException e){
            e.printStackTrace();
        }

         securityLogFormatter = new LogFormatter(VENDOR, PRODUCT,  Config.getVersion(), SEVERITY);
    }

    public static MBeanServerForwarder newProxyInstance() {

        final InvocationHandler handler = new LoggingInterceptor();

        final Class[] interfaces =
                new Class[] {MBeanServerForwarder.class};

        Object proxy = Proxy.newProxyInstance(
                MBeanServerForwarder.class.getClassLoader(),
                interfaces,
                handler);

        return MBeanServerForwarder.class.cast(proxy);
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        String methodName, identity, result, reason;
        methodName ="";
        result = "Success";
        reason = "OK";
        identity = "";

        try {
            methodName = method.getName();

            if (methodName.equals("getMBeanServer")) {
                return mbs;
            }

            if (methodName.equals("setMBeanServer")) {
                if (args[0] == null) {
                    throw new IllegalArgumentException("Null MBeanServer");
                }
                if (mbs != null) {
                    throw new IllegalArgumentException("MBeanServer object " +
                            "already initialized");
                }
                mbs = (MBeanServer) args[0];
                return null;
            }

            SubjectGetter sg = new SubjectGetter();
            Subject subject = sg.getSubject();

            // Allow operations performed locally on behalf of the connector server itself

            if (subject == null) {
                return method.invoke(mbs, args);
            }

            Set<JMXPrincipal> principals;
            try{
                principals = subject.getPrincipals(JMXPrincipal.class);
                if (principals.isEmpty()) {
                    throw new SecurityException("Access denied");
                }
                Principal principal = principals.iterator().next();
                identity = principal.getName();
            } catch (NullPointerException e) {
                throw new SecurityException("Access denied");
            }

            return method.invoke(mbs, args);
        }catch (Exception e) {
            result = "Fail";
            reason = "Error";
            throw e;
        }finally{
            if (loggingEnabled) {
                try {
                    String secLogSigId = securityLogFormatter.formatSignatureId(methodName, args);
                    String secLogExts = securityLogFormatter.formatExtensions(System.currentTimeMillis(), methodName, identity, secLogSigId, result, reason, serverIpAddress);
                    String secLog = securityLogFormatter.format(secLogSigId, methodName, secLogExts);
                    logger.info(secLog);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
