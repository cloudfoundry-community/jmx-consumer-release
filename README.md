# JMX Consumer Release


### Requirements

* bosh director
* deployment of [cf-deployment](https://github.com/cloudfoundry/cf-deployment)
* Java 1.8+

### Testing with `jconsole`

*Assuming* the `bosh` is targeted correctly.

```
bosh create-release --force
bosh upload-release --rebase
bosh -d jmx-consumer deploy manifests/consumer.yml \
  --vars-store vars.yml \
  --vars-file ~/workspace/cf-deployment/deployments-vars.yml \
  -v cf_deployment_name=cf \
  -v zone=az1 \
  -v vm_type=default \
  -v network_name=default
bosh -d jmx-consumer vms #grab IP address of VM

# since self signed certs are used, a truststore needs to be generated for `jconsole`
bosh int vars.yml --path /jmx_ssl/certificate > /tmp/jmx.crt
keytool -import -alias jconsole -file /tmp/jmx.crt -keystore /tmp/jconsole.truststore -storepass password -noprompt
jconsole -J-Djavax.net.ssl.trustStore=/tmp/jconsole.truststore -J-Djavax.net.ssl.trustStorePassword=password

# jconsole will then ask for the credentials and IP:Port to access
# Host: <IP Address>:44444
# Username: admin
# Password: insecure-password
```

🎂🎉 Celebrate 🎂🎉
