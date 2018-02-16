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
        --vars-store $VBOX_DEPLOYMENT_DIR/deployment-vars.yml \
        -v system_domain=bosh-lite.com


popd

bosh create-release --force
bosh -n -e vbox upload-release
bosh -n -e vbox -d jmx-nozzle deploy manifests/nozzle.yml \
    --vars-file $VBOX_DEPLOYMENT_DIR/deployment-vars.yml \
    --vars-store $VBOX_DEPLOYMENT_DIR/nozzle-vars.yml \
      -v cf_deployment_name=cf \
      -v zone=z1 \
      -v vm_type=default \
      -v network_name=default
rm $VBOX_DEPLOYMENT_DIR/jconsole.truststore
bosh int $VBOX_DEPLOYMENT_DIR/nozzle-vars.yml --path /jmx_ssl/certificate > $VBOX_DEPLOYMENT_DIR/jmx.crt
keytool -import -alias jconsole -file $VBOX_DEPLOYMENT_DIR/jmx.crt -keystore $VBOX_DEPLOYMENT_DIR/jconsole.truststore -storepass password -noprompt
vm_ip=$(bosh -e vbox vms  | grep jmx-nozzle | awk '{ print $4 }')

echo "Run command:"
echo "jconsole -J-Djavax.net.ssl.trustStore=$VBOX_DEPLOYMENT_DIR/jconsole.truststore -J-Djavax.net.ssl.trustStorePassword=password"
echo "Connect to:"
echo "$vm_ip:44444"