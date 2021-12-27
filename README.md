## Building the Source

Rubix Platform is the Spring API project which uses RubixCoreJava and DIDRef to run the network. To build the source, you need to download the RubixCoreJava and DIDRef repositories. Then, you need to build the RubixCoreJava and DIDRef java libraries. Install the dependencies by running the following mvn commmand with different versions of the dependencies. Update the versions in the pom.xml file to match the versions of the dependencies. Then, you need to build the RubixPlatform.jar file using mvn package command.


RUBIX JAR build setup  
```
mvn install:install-file \
   -Dfile=/<path to jar>/rubixcorejava.jar \
   -DgroupId=com.rubix.core \
   -DartifactId=rubix \
   -Dversion=10.1-creditSecurity \
   -Dpackaging=jar \
   -DgeneratePom=true
  
 ```


DID Ref build setup

```

mvn install:install-file \
   -Dfile=/<path to jar>/didjava.jar \
   -DgroupId=com.did.core \
   -DartifactId=did \
   -Dversion=10.1-mainnet \
   -Dpackaging=jar \
   -DgeneratePom=true
   
```
