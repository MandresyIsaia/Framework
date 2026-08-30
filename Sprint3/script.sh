#!/bin/bash

DESTINATION_DIR=/Users/rehareharanaivo/S4/NAINA/JAVA_Framework/Test/WEB-INF/lib
LIB_SERVLET=/opt/homebrew/Cellar/tomcat/10.1.20/libexec/lib

# Vérifier si le dossier lib existe dans le dossier de destination
if [ ! -d "$DESTINATION_DIR/lib" ]; then
    mkdir -p "$DESTINATION_DIR/lib"
fi

# Compiler les fichiers Java
javac -cp "$LIB_SERVLET/*" -d "../classes" ./src/*.java

# Créer le fichier JAR
jar -cf "../libFramework/framework.jar" -C "../classes" .

# Copier le fichier JAR vers le dossier lib du dossier de destination
cp "../libFramework/framework.jar" "$DESTINATION_DIR"

echo "Compilation et copie terminées."
