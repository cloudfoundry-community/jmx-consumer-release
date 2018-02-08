package unit;

import org.cloudfoundry.jmxnozzle.Metric;
import org.cloudfoundry.jmxnozzle.jmx.DynamicJmxBean;
import org.junit.jupiter.api.Test;

import javax.management.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static javax.management.ImmutableDescriptor.EMPTY_DESCRIPTOR;
import static org.assertj.core.api.Assertions.assertThat;

public class DynamicBeanTest {
    @Test
    public void returnsAValidMBeanInfo() throws AttributeNotFoundException, MBeanException, ReflectionException, InvalidAttributeValueException {
        DynamicJmxBean bean = new DynamicJmxBean(
                "deployment",
                "job",
                "0",
                "0.0.0.0",
                ""
        );

        assertThat(bean.invoke("", null, null )).isNull();

        bean.setAttribute(new Attribute("system.cpu", 5d));

        MBeanInfo info= bean.getMBeanInfo();

        assertThat(info).isNotNull();
        assertThat(info.getClassName()).isEqualTo("org.cloudfoundry:deployment=deployment,job=job,index=0,ip=0.0.0.0");
        assertThat(info.getDescription()).isEqualTo("org.cloudfoundry:deployment=deployment,job=job,index=0,ip=0.0.0.0");
        assertThat(info.getAttributes()).isNotEmpty();
        assertThat(info.getAttributes()).contains(
                new MBeanAttributeInfo("system.cpu", "java.lang.Double", "system.cpu", true, false, false)
        );

        assertThat(info.getOperations()).isNotEmpty();
        assertThat(info.getOperations()).contains(
                new MBeanOperationInfo("system.cpu", "system.cpu", null, "java.lang.Double",  MBeanOperationInfo.INFO)
        );

        assertThat(info.getDescriptor()).isNotEqualTo(EMPTY_DESCRIPTOR);
        assertThat(info.getDescriptor().getFieldValue("system.cpu")).isEqualTo(5d);
        assertThat(info.getDescriptor().getFieldValue("deployment")).isEqualTo("deployment");
        assertThat(info.getDescriptor().getFieldValue("job")).isEqualTo("job");
        assertThat(info.getDescriptor().getFieldValue("index")).isEqualTo("0");
        assertThat(info.getDescriptor().getFieldValue("ip")).isEqualTo("0.0.0.0");

    }

    @Test
    public void getAndSetAttribute() throws AttributeNotFoundException, MBeanException, ReflectionException, InvalidAttributeValueException {
        DynamicJmxBean bean = new DynamicJmxBean(
                "deployment",
                "job",
                "0",
                "0.0.0.0",
                ""
        );

        bean.setAttribute(new Attribute("system.cpu", 5d));
        Object attribute = bean.getAttribute("system.cpu");
        assertThat((Double)attribute).isEqualTo(5d);
    }

    @Test
    public void getAndSetAttributes() throws AttributeNotFoundException, MBeanException, ReflectionException, InvalidAttributeValueException {
        DynamicJmxBean bean = new DynamicJmxBean(
                "deployment",
                "job",
                "0",
                "0.0.0.0",
                ""
        );

        List<Attribute> setAttributes = new ArrayList<>();
        setAttributes.add(new Attribute("system1.cpu", 1d));
        setAttributes.add(new Attribute("system2.cpu", 2d));
        bean.setAttributes(new AttributeList(setAttributes));

        AttributeList getAttributes = bean.getAttributes(new String[]{"system1.cpu", "system2.cpu"});
        assertThat(getAttributes.asList()).contains(setAttributes.get(0), setAttributes.get(1));
    }

    @Test
    public void setMetric() throws AttributeNotFoundException, MBeanException, ReflectionException, InvalidAttributeValueException {
        DynamicJmxBean bean = new DynamicJmxBean(
                "deployment",
                "job",
                "0",
                "0.0.0.0",
                ""
        );

        bean.setMetric(new Metric("test", 100d, 0, new HashMap<>()));
        Object attribute = bean.getAttribute("test");
        assertThat((Double)attribute).isEqualTo(100d);
    }

    @Test
    public void setMetricWithPrefix() throws AttributeNotFoundException, MBeanException, ReflectionException, InvalidAttributeValueException {
        DynamicJmxBean bean = new DynamicJmxBean(
                "deployment",
                "job",
                "0",
                "0.0.0.0",
                "prefix."
        );

        bean.setMetric(new Metric("test", 100d, 0, new HashMap<>()));
        Object attribute = bean.getAttribute("prefix.test");
        assertThat((Double)attribute).isEqualTo(100d);
    }
}
