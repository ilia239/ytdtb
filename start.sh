#!/bin/bash
### BEGIN INIT INFO
# Provides:          haltusbpower
# Required-Start:    $all
# Required-Stop:
# Default-Start:     2 3 4 5
# Default-Stop:
# Short-Description: Halts USB power...
### END INIT INFO

source  $HOME/ytdtb/local/env.sh
export JAVA_HOME=$HOME/.sdkman/candidates/java/current

cd $HOME/ytdtb
git pull
./mvnw clean install -DskipTests

$JAVA_HOME/bin/java -jar ~/ytdtb/target/ytdtb-0.0.1-SNAPSHOT.jar

