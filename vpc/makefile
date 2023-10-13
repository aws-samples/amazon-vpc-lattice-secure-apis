include etc/environment.sh

cdk: cdk.synth cdk.deploy
cdk.bootstrap:
	cdk bootstrap --profile ${PROFILE}
cdk.synth:
	cdk synth --profile ${PROFILE} --context cidr=${P_CIDR}
cdk.deploy:
	cdk deploy --profile ${PROFILE} --context cidr=${P_CIDR}
