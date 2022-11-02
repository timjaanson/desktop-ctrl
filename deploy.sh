#!/bin/bash

DEPLOY_DIR=${HOME}/bin/desktop-ctrl

mkdir -p "$DEPLOY_DIR"

echo "Building jar..."
mvn clean install

echo "Copying files to $DEPLOY_DIR"
cp ./target/desktop-ctrl-*.jar "${DEPLOY_DIR}/desktop-ctrl.jar"
cp ./launch_script/start.sh "${DEPLOY_DIR}/start.sh"

PID=$(pgrep -f desktop-ctrl.jar)
if [ $? -eq 0 ]; then
  echo "Killing running instance of desktop-ctrl"
  echo ""
  ps $PID
  echo ""
  read -p "Continue? "
  kill $PID
fi

echo "Starting desktop-ctrl..."
$DEPLOY_DIR/start.sh