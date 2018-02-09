package org.cloudfoundry.jmxnozzle.jmx;

import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import org.cloudfoundry.jmxnozzle.Metric;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

public class BeanCollector {
    private Cache<ObjectName, DynamicJmxBean> dynamicJmxBeans;
    private String metricPrefix;

    public  BeanCollector( long expiryTime, String metricPrefix) {
        this.metricPrefix = metricPrefix;
        RemovalListener<ObjectName, DynamicJmxBean> unregsisterFromMBeanServer = removal -> {
            MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
            try {
                platformMBeanServer.unregisterMBean(removal.getKey());
            } catch (InstanceNotFoundException | MBeanRegistrationException e) {
                e.printStackTrace();
            }
        };

        this.dynamicJmxBeans = CacheBuilder.newBuilder()
                .expireAfterAccess(expiryTime, TimeUnit.MILLISECONDS)
                .removalListener(unregsisterFromMBeanServer)
                .ticker(Ticker.systemTicker())
                .build();
    }

    public void stop() {
        dynamicJmxBeans.invalidateAll();
    }

    public void setMetric(Metric metric) throws MalformedObjectNameException, InstanceAlreadyExistsException, NotCompliantMBeanException, MBeanException, AttributeNotFoundException, ReflectionException, InvalidAttributeValueException {
        getBeanForMetric(metric).setMetric(metric);
    }

    private DynamicJmxBean getBeanForMetric(Metric metric) throws MalformedObjectNameException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        DynamicJmxBean dynamicJmxBean = new DynamicJmxBean(
                metric.getDeployment(),
                metric.getJob(),
                metric.getIndex(),
                metric.getIP(),
                this.metricPrefix
        );
        ObjectName objectName = new ObjectName(dynamicJmxBean.getName());

        MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();

        if (dynamicJmxBeans.getIfPresent(objectName) != null) {
            dynamicJmxBean = dynamicJmxBeans.getIfPresent(objectName);
        } else {
            dynamicJmxBeans.put(objectName, dynamicJmxBean);
            platformMBeanServer.registerMBean(dynamicJmxBean, objectName);
        }
        return dynamicJmxBean;
    }
}