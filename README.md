
# About

This is the core functionality of framework for creating rich objects with complex structure and building execution threads in a functional style. 
See core classes in the package computing.core:


```
ExecutionFlow
Stage
Task

ExecutionEngine

AsyncResult
Event
```

Execution Engine supports the following features:

* Asynchronous execution and concurrency with CompletableFuture classes from Java 8

* Feather for dependency injection

* Auto-configuration of execution via annotations

* MyBatis ORM for persistence level

* Auto-commit and transactional mechanism for database operations  


## Launching classes with process launcher

1) Annotate your main class with @Entry annotation
2) The executable class must extend computing.core.Stage. Business logic must be defined by the way of overriding exec method


## Statuses are stored in the database

Database connection is mandatory for core to work. It is used to store statuses of currently running business processes and more.


## Certificate configuration on client side (Secret Vault usage)

1 Download certificate from website and save it to file RestCert1.cer (for example)

2 Make import of certificate using the command:

```
keytool -import -noprompt -trustcacerts -alias rest_db_cert_1 -file   RestCert1.cer  -keystore "c:\Program Files\Java\jre1.8.0_201\lib\security\cacerts" -storepass changeit
```

### boot.properties file MUST present on classpath during run

Contents of this file are used during boot time.

The example file has the following structure:

```
# Configuration File
# ===================================================
#
# Refer to the "Client Configuration" section in the main
# documentation for a complete description of this file.  A short
# synopsis follows.
#
# This file defines:
# 
# 1) what database to connect to
# 2) secret vault info
# 3) packages to scan for myBatis mappers
# 4) custom configuration for Feather

## PostgreSQL settings
datasource.url=jdbc:postgresql://127.0.0.1:5432/database
datasource.username=postgres
datasource.password=admin

# Secret Vault
vaultservice.url=https://127.0.0.1:8443/svcs/rs/vault

# defines paths to be scanned during boot (@Entry classes)
packages=computing

# defines classes to be used for Feather providers
configuration=computing.model.DefaultConfiguration

# defines paths to be scanned for classes annotated with @Datastore
# for MyBatis persistence level
mapperscan=computing.sample.mappers,computing.demo.dao,computing.client.database.dao

## DO NOT EDIT 
## reserved for server configuration
## hibernate: show all queries

server.port=8443

spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.type=trace
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.properties.hibernate.current_session_context_class=org.hibernate.context.internal.ThreadLocalSessionContext

# enabling H2 Console
spring.h2.console.enabled=true

# turn Statistics on
spring.jpa.properties.hibernate.generate_statistics=true
logging.level.org.hibernate.stat=info
logging.level.root=WARN
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate=ERROR

## security
server.ssl.key-alias=selfsigned_localhost_sslserver
server.ssl.key-password=changeit
server.ssl.key-store=classpath:ssl-server.jks
server.ssl.key-store-provider=SUN
server.ssl.key-store-type=JKS
```


