#!/bin/bash

APP_NAME="MonFrameWorkTest"
SRC_DIR="src/main/java"
WEB_DIR="src/main/webapp"
BUILD_DIR="build"
LIB_DIR="lib"
TOMCAT_WEBAPPS="/opt/tomcat/webapps"

# Nettoyage complet
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/WEB-INF/classes"
mkdir -p "$BUILD_DIR/WEB-INF/lib"

# Compilation (tous les jars de lib/ dans le classpath)
javac -parameters -cp "$LIB_DIR/*" -d "$BUILD_DIR/WEB-INF/classes" $(find "$SRC_DIR" -name "*.java")
if [ $? -ne 0 ]; then
    echo "Erreur de compilation !"
    exit 1
fi

# Fichiers web (web.xml, JSP...)
cp -r "$WEB_DIR"/* "$BUILD_DIR"/

# Copier les jars SAUF servlet-api (fourni par Tomcat)
for jar in "$LIB_DIR"/*.jar; do
    case "$(basename "$jar")" in
        servlet-api*|jakarta.servlet-api*) continue ;;
    esac
    cp "$jar" "$BUILD_DIR/WEB-INF/lib/"
done

# Génération du war (dans un dossier à part pour ne pas s'inclure lui-même)
(cd "$BUILD_DIR" && jar -cf "../$APP_NAME.war" *)

# Déploiement
sudo cp -f "$APP_NAME.war" "$TOMCAT_WEBAPPS/"

echo "Déploiement terminé."