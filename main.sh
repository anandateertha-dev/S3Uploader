#!/bin/bash

git clone "$GIT_URL" /home/app/output

apt-get update
apt-get install -y curl
curl -sL https://deb.nodesource.com/setup_20.x | bash -
apt-get upgrade -y
apt-get install -y nodejs

npm install vite

mvn clean
mvn package

# Compile the Java file using the generated JAR file from Maven
javac -cp ".:/home/app/target/builderimage-1.0.0.jar" /home/app/src/main/java/com/builderimage/App.java

# Run your Java program
java -cp ".:/home/app/target/builderimage-1.0.0.jar" com.builderimage.App
