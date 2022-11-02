#!/bin/bash

DIR=/home/tj/bin/desktop-ctrl
cd $DIR || exit 1
java -Dspring.profiles.active=prod -jar desktop-ctrl.jar > log 2>&1 &
echo $! > pid
