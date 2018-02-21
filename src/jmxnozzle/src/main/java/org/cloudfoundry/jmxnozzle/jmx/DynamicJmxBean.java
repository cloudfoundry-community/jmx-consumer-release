package org.cloudfoundry.jmxnozzle.jmx;

import org.cloudfoundry.jmxnozzle.ingress.Metric;

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
    private final String metricPrefix;
    private Map<String, Double> attributes = new HashMap<>();
    private Map<String, Long> timestamps = new HashMap<>();

    public DynamicJmxBean(String deployment, String job, String index, String ip, String metricPrefix) {
        this.deployment = deployment;
        this.job = job;
        this.index = index;
        this.ip = ip;
        this.metricPrefix = metricPrefix;
    }

    public void setMetric(Metric metric) throws AttributeNotFoundException, MBeanException, ReflectionException, InvalidAttributeValueException {
        if (!timestamps.containsKey(metric.getName()) || timestamps.get(metric.getName()) < metric.getTimestamp()) {
            setAttribute(new Attribute(this.metricPrefix + metric.getName(), metric.getValue()));
            timestamps.put(metric.getName(), metric.getTimestamp());
        }
    }

    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        return attributes.get(attribute);
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        attributes.put(attribute.getName(), (Double)attribute.getValue());
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

        MBeanAttributeInfo[] attributes = this.attributes.keySet().stream()
                .map(key -> new MBeanAttributeInfo(key, "java.lang.Double", key, true, false, false))
                .toArray(size -> new MBeanAttributeInfo[size]);

        MBeanOperationInfo[] operations = this.attributes.keySet().stream()
                .map(key -> new MBeanOperationInfo(key, key, null, "java.lang.Double",  MBeanOperationInfo.INFO))
                .toArray(size -> new MBeanOperationInfo[size]);

        DescriptorSupport descriptor = new DescriptorSupport();
        this.attributes.entrySet().stream().forEach(map -> descriptor.setField(map.getKey(), map.getValue()));
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
