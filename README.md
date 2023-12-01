# Distributed tracing with http headers #

This project presents a demo implementation of microservices distributed tracing using http headers for span/context propagation.

### What is this repository for? ###

* setup a k8s cluster to see how distributed tracing with Jeager works

### How do I get set up? ###

* Summary of set up
* Configuration
* Dependencies
* Database configuration
* How to run tests
* Deployment instructions

### How do I get set up? ###

* Download/clone the repo
* executed "eshop-k8s" deployment files against your k8s cluster
* the docker images are all public so you should be able to execute the deploymenys without build each single image.
* forward the eshop-checkout pod and Jeager pod
* perform a call to the /checkout endpoint
* open the jeager ui (16686 is the default port)
* watch the distributed tracing data

### Contribution guidelines ###

* Writing tests
* Code review
* Clean code principles
* Clean architecture principles
* SOLID principles

### NOTES ###
## Aspects ##
The tracing instrumentation is implemented through AOP

## Jeager ##
For simplicity the Jeager version is the "all-in-one", it means that runs in a single pod and it is not intended for production use.

## Tracing ##
The project is implemented with the "Open Tracing" API and will be migrated to Open Telemetry as soon as possible.

### Who do I talk to? ###

* Repo owner or admin: fedegm3@gmail.com