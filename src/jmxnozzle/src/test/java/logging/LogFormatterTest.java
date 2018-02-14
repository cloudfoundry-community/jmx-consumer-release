package logging;


import org.junit.Before;
import org.junit.Test;
import org.cloudfoundry.logging.*;

import static org.assertj.core.api.Assertions.assertThat;

public class LogFormatterTest {
    private LogFormatter logging;
    private String deviceVendor;
    private String deviceProduct;
    private String deviceVersion;
    private int severity;

    @Before
    public void setUp() {
        deviceVendor = "some-vendor";
        deviceProduct = "some-product";
        deviceVersion = "some-version";
        severity = 99;
        logging = new LogFormatter(deviceVendor, deviceProduct, deviceVersion, severity);
    }

    @Test
    public void itHandlesNullArguments(){
        logging.formatSignatureId("some-method", null);
    }

    @Test
    public void itHandlesANullArgument(){
        logging.formatSignatureId("some-method", new Object[]{null});
    }

    @Test
    public void formatOutputsCorrectFieldOrder() {
        String methodName = "some-method";
        String signatureId = "some-signature-id";

        String value = logging.format(signatureId, methodName, "some-extensions");
        String[] fields = splitCEFMessage(value);

        assertThat(fields.length).isEqualTo(8);

        assertThat(fields[0]).isEqualTo("CEF:0");
        assertThat(fields[1]).isEqualTo(deviceVendor);
        assertThat(fields[2]).isEqualTo(deviceProduct);
        assertThat(fields[3]).isEqualTo(deviceVersion);
        assertThat(fields[4]).isEqualTo(signatureId);
        assertThat(fields[5]).isEqualTo(methodName);
        assertThat(Integer.parseInt(fields[6])).isEqualTo(severity);
        assertThat(fields[7]).isEqualTo("some-extensions");
    }

    private String[] splitCEFMessage(String message) {
        return message.split("(?<!\\\\)\\|");
    }

    @Test
    public void formatSignatureId() {
        String methodName = "some-method";
        Object[] args = {"param1", new Integer(12)};
        String signatureId = logging.formatSignatureId(methodName, args);

        assertThat(signatureId).isEqualTo("some-method param1 12");
    }

    @Test
    public void formatSignatureIdWithArray() {
        String methodName = "some-method";
        Object[] args = {new String[]{"param1", "param2"}, new Integer[]{12, 13}};
        String signatureId = logging.formatSignatureId(methodName, args);

        assertThat(signatureId).isEqualTo("some-method [param1, param2] [12, 13]");
    }

    @Test
    public void formatEscapesSignatureIdWithPipe() {
        String methodName = "some-method";
        Object[] args = {"param1|2", new Integer(12)};
        String signatureId = logging.formatSignatureId(methodName, args);

        String value = logging.format(signatureId, methodName, "some-extensions");
        String[] fields = splitCEFMessage(value);

        assertThat(fields[4]).isEqualTo("some-method param1\\|2 12");
    }

    @Test
    public void formatExtensions() {
        Long requestTime = System.currentTimeMillis();
        String methodName = "some-method";
        String user = "some-user";
        String signatureId = "some-signature-id";
        String result = "success";
        String reason = "ok";
        String clientIP = "127.0.0.1";
        String serverIP = "8.8.8.8";

        String extensions = logging.formatExtensions(requestTime, methodName, user, signatureId, result, reason, serverIP);

        String expected = String.format("rt=%d requestMethod=%s suser=%s cs1Label=userAuthenticationMechanism cs1=Java Security Manager request=%s cs2Label=result cs2=%s cs3Label=reason cs3=%s dproc=jmx_nozzle dst=%s",
                requestTime, methodName, user, signatureId, result, reason, serverIP);
        assertThat(extensions).isEqualTo(expected);
    }

    @Test
    public void formatEscapesExtensionSpecialChars() {
        Long requestTime = System.currentTimeMillis();
        String methodName = "some-method";
        String user = "some=user";
        String signatureId = "some=special\\signature|id";
        String result = "success";
        String reason = "ok";
        String clientIP = "127.0.0.1";
        String serverIP = "8.8.8.8";

        String extensions = logging.formatExtensions(requestTime, methodName, user, signatureId, result, reason, serverIP);

        String expected = String.format("rt=%d requestMethod=%s suser=%s cs1Label=userAuthenticationMechanism cs1=Java Security Manager request=%s cs2Label=result cs2=%s cs3Label=reason cs3=%s dproc=jmx_nozzle dst=%s",
                requestTime, methodName, user, "some\\=special\\\\signature\\|id", result, reason, serverIP);

        String value = logging.format("some-signature-id", "some-method", extensions);
        String[] fields = splitCEFMessage(value);

        assertThat(fields[7]).isEqualTo(expected);
    }
}