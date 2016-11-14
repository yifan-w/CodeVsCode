# Project setup instructions

## Installing Spring Tool Suite
1. Make sure you have the latest version of Eclipse and JDK installed (we are using Java 8)
2. Launch Eclipse
3. Click Help->Eclipse Marketplace
4. Search for "spring tool suite" and install "Spring Tool Suite (STS) for Eclipse 3.7.2.RELEASE"

## Installing m2e
1. Click Help->Install New Software
2. Click add and paste `http://download.eclipse.org/technology/m2e/releases` in the Location box
3. Install "Maven Integration for Eclipse"

## Importing
1. Click File->Import->Maven->Existing Maven Projects
2. Click Browse and navigate to the directory
3. Once you've imported, right click the project->Run As->Spring Boot App
4. check localhost:8080 to see if it is working
