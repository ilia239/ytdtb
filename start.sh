#!/bin/bash
### BEGIN INIT INFO
# Provides:          haltusbpower
# Required-Start:    $all
# Required-Stop:
# Default-Start:     2 3 4 5
# Default-Stop:
# Short-Description: Halts USB power...
### END INIT INFO

cd ~/ytdtb
git pull
./mvnw clean install -DskipTests

/home/ilia/.sdkman/candidates/java/current/bin/java -jar ~/ytdtb/target/ytdownloadbot-0.0.1-SNAPSHOT.jar

