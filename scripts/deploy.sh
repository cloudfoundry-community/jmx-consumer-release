#! /usr/bin/env bash

# This script just enacts the bosh-lite docs found here:
# https://bosh.io/docs/bosh-lite
# NB: requires sudo, may call for password entry

set -eux

export VBOX_DEPLOYMENT_DIR=~/deployments/vbox
mkdir -p "${VBOX_DEPLOYMENT_DIR}"

pushd ~/workspace/cf-deployment
    bosh -n -e vbox update-cloud-config iaas-support/bosh-lite/cloud-config.yml
    bosh -n -e vbox -d cf deploy cf-deployment.yml \
        -o operations/bosh-lite.yml \
        -o operations/use-compiled-releases.yml \
        -o operations/experimental/use-bosh-dns.yml \
        -o operations/experimental/use-bosh-dns-for-containers.yml \
        --vars-store $VBOX_DEPLOYMENT_DIR/deployment-vars.yml \
        -v system_domain=bosh-lite.com


popd

pushd ~/workspace/bosh-system-metrics-forwarder-release
    bosh create-release --force
    bosh -n -e vbox upload-release
    bosh -n -e vbox -d bosh-system-metrics-forwarder deploy <(cat manifests/bosh-system-metrics-forwarder.yml | sed 's/loggregator_metron/loggregator_tls_metron/g') \
        --vars-file $VBOX_DEPLOYMENT_DIR/creds.yml \
        --vars-file $VBOX_DEPLOYMENT_DIR/deployment-vars.yml \
        -v internal_ip=192.168.50.6
popd

bosh create-release --force
bosh -n -e vbox upload-release
bosh -n -e vbox -d jmx-consumer deploy manifests/consumer.yml \
    --vars-file $VBOX_DEPLOYMENT_DIR/deployment-vars.yml \
    --vars-store $VBOX_DEPLOYMENT_DIR/consumer-vars.yml \
      -v cf_deployment_name=cf \
      -v zone=z1 \
      -v vm_type=default \
      -v network_name=default
rm $VBOX_DEPLOYMENT_DIR/jconsole.truststore
bosh int $VBOX_DEPLOYMENT_DIR/consumer-vars.yml --path /jmx_ssl/certificate > $VBOX_DEPLOYMENT_DIR/jmx.crt
keytool -import -alias jconsole -file $VBOX_DEPLOYMENT_DIR/jmx.crt -keystore $VBOX_DEPLOYMENT_DIR/jconsole.truststore -storepass password -noprompt
vm_ip=$(bosh -e vbox vms  | grep jmx-consumer | awk '{ print $4 }')

echo "Run command:"
echo "jconsole -J-Djavax.net.ssl.trustStore=$VBOX_DEPLOYMENT_DIR/jconsole.truststore -J-Djavax.net.ssl.trustStorePassword=password"
echo "Connect to:"
echo "$vm_ip:44444"