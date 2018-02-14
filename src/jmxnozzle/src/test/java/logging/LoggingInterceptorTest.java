package logging;

import org.junit.Before;
import org.junit.Test;

import org.cloudfoundry.logging.*;

import javax.management.MBeanServer;
import javax.management.remote.JMXPrincipal;
import javax.security.auth.Subject;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class LoggingInterceptorTest {
    Object proxy;
    Method method;
    Object[] args;
    LoggingInterceptor handler;
    MBeanServer   mbs;
    SubjectGetter sg;
    Subject subject;


    @Before
    public void setUp() throws Exception {
        proxy = new Object();

        args = new Object[1];
        mbs = mock(MBeanServer.class);
        args[0] = mbs;
        handler = spy(new LoggingInterceptor());
        sg = mock(SubjectGetter.class);
        subject = mock(Subject.class);

        when(mbs.getDefaultDomain()).thenReturn("my-domain");
    }

    private void setsMBSServer() throws Throwable {
        method = TestHelper.class.getMethod("setMBeanServer");
        Object setResult = handler.invoke(proxy, method, args);
        assertNull(setResult);
    }

    @Test
    public void setsAndGetsMBSServer() throws Throwable {
        setsMBSServer();

        method = TestHelper.class.getMethod("getMBeanServer");
        Object getResult = handler.invoke(proxy, method, args);
        assertEquals(getResult, mbs);
    }

    @Test
    public void throwsIllegalArgumentExceptionForNullMBeanServerArgument() throws Throwable{
        args[0] = null;
        try {
            setsMBSServer();
        }catch (IllegalArgumentException e){
            assert e.getMessage().equals("Null MBeanServer");
            return;
        }
        assert false;
    }

    @Test
    public void throwsIllegalArgumentExceptionForSettingMBeanServerTwice() throws Throwable{
        method = TestHelper.class.getMethod("setMBeanServer");
        setsMBSServer();
        try {
            setsMBSServer();
        }catch (IllegalArgumentException e){
            assert e.getMessage().equals("MBeanServer object already initialized");
            return;
        }
        assert false;
    }

    @Test
    public void invokesMethodWithoutSubject() throws Throwable{
        setsMBSServer();
        args = null;
        when(sg.getSubject()).thenReturn(null);
        method = MBeanServer.class.getMethod("getDefaultDomain");
        try {
            Object result = handler.invoke(proxy, method, args);
            assertEquals("my-domain", (String)result);
        }catch (Exception e){
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void invokesMethodWithSecurity() throws Throwable{
        setsMBSServer();
        args = null;
        when(sg.getSubject()).thenReturn(subject);

        JMXPrincipal principal = new JMXPrincipal("some-name");
        Set<JMXPrincipal> principals = new HashSet<JMXPrincipal>();
        principals.add(principal);

        when(subject.getPrincipals(JMXPrincipal.class)).thenReturn(principals);
        method = MBeanServer.class.getMethod("getDefaultDomain");
        try {
            Object result = handler.invoke(proxy, method, args);
            assertEquals("my-domain", (String)result);
        }catch (Exception e){
            e.printStackTrace();
            assert false;
        }
    }

    private class TestHelper {
        public void setMBeanServer(){}
        public void getMBeanServer(){}
    }


}
