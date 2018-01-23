# JMX Nozzle Release

This is still under active development and not _production_ ready at all.

## Development

### Requirements

* bosh director
* deployment of [cf-deployment](https://github.com/cloudfoundry/cf-deployment)
* Java 1.8+

### Testing with `jconsole`

*Assuming* the `bosh` is targeted correctly.

```
bosh create-release --force
bosh upload-release --rebase
bosh -d jmx-nozzle deploy manifests/nozzle.yml \
  --vars-store vars.yml \
  --vars-file ~/workspace/cf-deployment/deployments-vars.yml
bosh -d jmx-nozzle vms #grab IP address of VM
jconsole <IP ADDRESS>:44444
```

🎂🎉 Celebrate 🎂🎉