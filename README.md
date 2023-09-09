# awesome-monolith
Yes, this is a monolith project by design

## Why am I doing this?
I've been working in the software industry for some time now and arguably one of the biggest trends I've
come across is Microservices. This buzzword is found everywhere and for good reason. Independent deployability,
independent scaling, domain segregation, these are all things we like to have.

Unfortunately these all come with a lot of additional complexity, more than most people can take. It is not uncommon
to start a new project and the default architecture people shout is Microservices. But why? All the problems
Microservices help solve are only present at scale.

This project is a thought experiment to start with a monolith, but not any monolith. I'm going to build
a modular monolith that can be decoupled into separate services with minimal overhead.

The idea is to start building everything into the same package, a single unit of deployment, but do so in a way
that we can deploy different instances running separate services or just one instance running all of them.

## Isn't this the same as full blown App servers like Wildfly or Payara?
In a sense, yes, but without all the effort that goes into the management of them, which is a nightmare on 
complex deployments.

The main goal here is not to build the system per se (that's just for the fun), it is to explore an architectural
style that enjoys the benefits and the simplicity of being a monolith, while keeping options open to easily
migrate to Microservice based environment.

## Goals

- One single repository for all the code
- The repository is still split into modules (gradle in this case)
- The core modules will contain all the infrastructure needed to bootstrap and handle service lifecycle 
and communication
- Each service needs to be completely independent and provide 2 implementation. One for local usage and 
another for remote proxies
- Each service will have a factory which provides a manifest with dependencies it needs to run

## Stages

- Start with a single repository
- Start moving modules to different repositories
- Deploy a single unit with all services
- Deploy multiple units with services split between them
- Decommission one of the services locally and keep only the proxy (breaking the monolith)

## Domain

At this point, any domain is good enough as long as I can separate it into at least 5 modules, excluding
core. So it is just another online shopping app.

A rough idea of what It would be able to do

* Register
* Browse categories
* Comment products
* Shopping cart
* Search

And which modules will be needed

* User management
* Category management
* Product management
* Checkout
* Payment
* Search
* Core

### User interface

For user interaction I'll probably use something like Flutter or Compose for web, since it provides a lot of
functionality and exports everything to JS/WASM.
