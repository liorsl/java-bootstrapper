## Java-Bootstrapper Project

In simple terms, this application loads specified jar and libraries at runtime using the URLClassLoader API.

This project intends to solve the change in behaviour Java 9 have introduced to the identity of the system class loader.
While this is definitely not the easiest way it is the best one to use for various environments, especially to ensure backward compatibility for applications that were intended for earlier Java version.

For now, this piece of code acts as a shim, however it may be expanded in the future to allow fine-grained control over the process.

This project targets Java 9 and does not require any library at runtime.

### Requirements:
1. Java 9 or newer
2. That's pretty much it

### How To Build
1. Clone this project
2. Run `gradle clean jar`
3. View the results in the directory `build/libs`

### How To Use
1. Place this project's jar in a directory (preferably a new one)
2. Place your application's jar file and name it in accordance with the configuration
3. If needed, create the `libraries` and drop your library jar files in there. 

#### Configuration 
Currently, there is only one configuration entry:

`mainJarFileName`: Determines the bootstrap jar file name. Default: application.jar

### TODO
* Use GitHub workflows
* Publish future releases to GitHub packages