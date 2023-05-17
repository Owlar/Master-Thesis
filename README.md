# Mobile Assets in Semantic Digital Twins
Repository for my Master Thesis at the University of Oslo Spring 2023

# How to run?
### Preliminaries
1. Have minimum Java Runtime version 11 (class file version 55) on the local machine. To setup, download Java JDK 11 from Oracle (sign-in required). Environment variables need to be set as well. More specifically, set JAVA_HOME to the source of the downloaded JDK, and Path to %JAVA_HOME%\bin (and move it to the top when using Windows).

2. Create a new directory locally on the machine, and clone the repository at the location of the directory with:
    ````flutter
    > git clone https://github.com/Owlar/Master-Thesis.git
    ````

### Server
3. The server has a Maven wrapper. To run the server from with all the required dependencies:
For Unix systems:
    ````flutter
    > ./mvnw clean install
    ````
For Windows systems:
    ````flutter
    > mvnw.cmd clean install
    ````

### Digital Twin
4.
    To run SMOL REPL with necessary commands:
    ````flutter
    > java -jar smol.jar -d http://www.semanticweb.org/oscarlr/ontologies/2023/2/building# -b building.owl
    ````
    2. To then read and run the SMOL program:
    ````flutter
    > reada main.smol
    ````
    
### Client
5.
    With an Android device: download the file release.apk (path: client/apk/release.apk) on your smartphone, or do what follows if you are using an iOS device:
    
    Open the client-directory in an IDE that supports running an emulator, such as Android Studio. Flutter SDK should be installed and set to PATH. In case version solving fails, try running:
    ````flutter
    > flutter upgrade
    > flutter pub get
    ````
    If there are any undefined code, e.g. due to Firebase, simply run the following:
    ````flutter
    > flutter pub get
    ````
 
