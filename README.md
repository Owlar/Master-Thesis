# Mobile Assets in Semantic Digital Twins
Repository for my Master Thesis at the University of Oslo Spring 2023

# How to run?
1. Create a new directory locally on the machine, and clone the repository at the location of the directory with:
    ````flutter
    > git clone https://github.com/Owlar/Master-Thesis.git
    ````
2. 
    * Open the server-directory containing the Maven project in an IDE such as IntelliJ, and it should resolve itself.
    1. To run SMOL REPL with necessary commands:
    ````flutter
    > java -jar smol.jar -d http://www.semanticweb.org/oscarlr/ontologies/2023/2/building# -b building.owl
    ````
    2. To then read and run the SMOL program:
    ````flutter
    > reada main.smol
    ````
    * Open the client-directory in an IDE that supports running an emulator, such as Android Studio. Flutter SDK should be installed and set to PATH. In case version solving fails, try running:
    ````flutter
    > flutter upgrade
    > flutter pub get
    ````
 
