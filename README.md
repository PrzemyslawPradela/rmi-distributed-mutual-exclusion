## Table of contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Setup](#setup)

## General info
Implementation of Ricart Agrawala algorithm using Java RMI
	
## Technologies
Project is created with:
* Java version: 13
* JavaFX version: 13.0.1
* Maven version: 3.6.3
	
## Setup
1. Install Java Runtime Environment
2. Install [Maven](https://maven.apache.org/install.html)
3. Clone this repository
4. Install modules dependencies
    ```
    $ cd ../rmi-distributed-mutual-exclusion
    $ mvn install
    ```
5. To run this project:

    *Server appliction*
    ```
    $ cd ../rmi-distributed-mutual-exclusion/server
    $ mvn javafx:run
    ```

    *Client appliction*
    ```
    $ cd ../rmi-distributed-mutual-exclusion/client
    $ mvn javafx:run
    ```
