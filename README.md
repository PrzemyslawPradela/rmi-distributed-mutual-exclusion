## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Setup](#setup)

## General info
Implementation of Ricart Agrawala algorithm using Java RMI and JavaFX.
	
## Technologies
Project is created with:
* Java: 11
* Maven: 3.6.2
	
## Setup
Install Java Runtime Environment and Maven.
Clone this repository.
To run this project, compile it and run it using mvn:

*Server appliction*
```
$ cd ../rmi-distributed-mutual-exclusion
$ mvn compile
$ mvn exec:java -Dexec.mainClass=rmi.mutex.server.StartServer
```

*Client appliction*
```
$ cd ../rmi-distributed-mutual-exclusion
$ mvn compile
$ mvn exec:java -Dexec.mainClass=rmi.mutex.client.Client
```
