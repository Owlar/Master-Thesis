# Mobile Assets in Semantic Digital Twins
Repository for my Master Thesis at the University of Oslo Spring 2023

# How to run?
### Prerequisites
1. Have minimum Java Runtime version 11 (class file version 55) on the local machine. Java 16 is not recommended.
    * Download Java JDK 11 from Oracle (sign-in required). 
    * Environment variables need to be set as well. More specifically, set JAVA_HOME to the source of the downloaded JDK, and Path to %JAVA_HOME%\bin (and move it to the top when using Windows).

2. Create a new directory locally on the machine, and clone the repository at the location of the directory with:
    ````flutter
    > git clone https://github.com/Owlar/Master-Thesis.git
    ````
   
3. To authenticate, use Firebase to create a service account with the role "Owner". Generate a key and download it as a JSON file. Its contents should be stored in a file named "saf.json" in server/.

### Server
3. The server has a Maven wrapper that builds the code:
* For Unix systems:
    ````flutter
    > ./mvnw clean install
    ````
* For Windows systems:
    ````flutter
    > mvnw.cmd clean install
    ````
    

### Twin
4. To run SMOL REPL with necessary commands:
    ````flutter
    > java -jar smol.jar -d http://www.semanticweb.org/oscarlr/ontologies/2023/2/building# -b building.owl
    ````
5. To then read and execute the SMOL program at the same time:
    ````flutter
    > reada main.smol
    ````

### Client
* To run the app on an Android device:
    * Simply download the file release.apk (path: client/apk/release.apk) on your smartphone.
* To run the app on an iOS device:
    1. Flutter SDK should be installed and set to PATH.
    2. Open the client-directory in an IDE that supports running an emulator, such as Android Studio. 
    3. Run:
    ````flutter
    > flutter upgrade
    > flutter pub get
    ````
    If there is any undefined code, e.g. due to Firebase, simply run what follows:
    ````flutter
    > flutter pub get
    ````
 
