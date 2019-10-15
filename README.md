## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Setup](#setup)

## General info
Implementation of Ricart Agrawala algorithm using Java RMI and JavaFX.
	
## Technologies
Project is created with:
* Java version: 11
* JavaFX version: 11
* Maven version: 3.6.2
	
## Setup
1. Install Java Runtime Environment.
2. Install [Maven](https://maven.apache.org/install.html)
3. Clone this repository.
4. To run this project, compile it and run it using mvn:

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
