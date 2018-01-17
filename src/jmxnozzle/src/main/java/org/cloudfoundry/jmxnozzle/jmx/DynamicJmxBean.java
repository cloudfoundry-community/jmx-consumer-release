package org.cloudfoundry.jmxnozzle.jmx;

import com.j256.simplejmx.common.JmxFolderName;
import com.j256.simplejmx.common.JmxResource;
import com.j256.simplejmx.common.JmxSelfNaming;

import javax.management.*;
import javax.management.modelmbean.DescriptorSupport;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DynamicJmxBean  implements DynamicMBean {

    private final String deployment;
    private final String job;
    private final String index;
    private final String ip;
    private Map<String, Double> values = new HashMap<>();

    public DynamicJmxBean(String deployment, String job, String index, String ip) {
        this.deployment = deployment;
        this.job = job;
        this.index = index;
        this.ip = ip;
    }

    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        return values.get(attribute);
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        values.put(attribute.getName(), (Double)attribute.getValue());
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
            return new AttributeList(Arrays.stream(attributes).map(name -> {
                try {
                    return new Attribute(name, getAttribute(name));
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }).collect(Collectors.toList()));
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        for (Object attribute : attributes) {
            try {
                setAttribute((Attribute) attribute);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return attributes;
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        return null;
    }

    public MBeanInfo getMBeanInfo() {
        String name = getName();

        MBeanAttributeInfo[] attributes = values.keySet().stream()
                .map(key -> new MBeanAttributeInfo(key, "java.lang.Double", key, true, false, false))
                .toArray(size -> new MBeanAttributeInfo[size]);

        MBeanOperationInfo[] operations = values.keySet().stream()
                .map(key -> new MBeanOperationInfo(key, key, null, "java.lang.Double",  MBeanOperationInfo.INFO))
                .toArray(size -> new MBeanOperationInfo[size]);

        DescriptorSupport descriptor = new DescriptorSupport();
        values.entrySet().stream().forEach(map -> descriptor.setField(map.getKey(), map.getValue()));
        descriptor.setField("deployment", this.deployment);
        descriptor.setField("job", this.job);
        descriptor.setField("index", this.index);
        descriptor.setField("ip", this.ip);

        return new MBeanInfo(
                name, //className
                name, //description
                attributes,
                null,
                operations,
                null,
                descriptor
        );
    }

    public String getName() {
        return String.format("org.cloudfoundry:deployment=%s,job=%s,index=%s,ip=%s", this.deployment, this.job, this.index, this.ip);
    }
}
