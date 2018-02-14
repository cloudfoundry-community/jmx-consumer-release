package logging;

import java.util.Arrays;
import java.util.StringJoiner;

public class LogFormatter {
    private String deviceVendor;
    private String deviceProduct;
    private String deviceVersion;
    private int severity;

    private static final String LOG_FORMAT = "CEF:0|%s|%s|%s|%s|%s|%d|%s";

    private static final String EXTENSION_FORMAT = "rt=%d requestMethod=%s suser=%s cs1Label=userAuthenticationMechanism"
        + " cs1=Java Security Manager request=%s cs2Label=result cs2=%s cs3Label=reason cs3=%s dproc=jmx_nozzle dst=%s";

    public LogFormatter(String deviceVendor, String deviceProduct, String deviceVersion, int severity) {
        this.deviceVendor = deviceVendor;
        this.deviceProduct = deviceProduct;
        this.deviceVersion = deviceVersion;
        this.severity = severity;
    }

    public String format(String signatureId, String methodName, String extensions) {
        return String.format(LOG_FORMAT, deviceVendor, deviceProduct, deviceVersion, escapeField(signatureId), methodName, severity, extensions);
    }

    public String formatSignatureId(String methodName, Object[] args) {
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(methodName);

        if (args != null) {
            for (Object arg : args) {
                joiner.add(nullHandlingToString(arg));
            }
        }

        return joiner.toString();
    }

    private String nullHandlingToString(Object obj){
        if(obj == null){
            return "";
        }else if(obj instanceof Object[]){
            return Arrays.toString((Object[])obj);
        }
        return obj.toString();
    }

    public String formatExtensions(long requestTime, String methodName, String user, String signatureId, String result, String reason, String serverIP) {
        return String.format(EXTENSION_FORMAT, requestTime, methodName, user, escapeExtension(signatureId), result, reason, serverIP);
    }

    private String escapeField(String field) {
        return field.replace("|", "\\|");
    }

    private String escapeExtension(String value) {
        return escapeField(value.replace("\\", "\\\\").replace("=", "\\="));
    }
}
