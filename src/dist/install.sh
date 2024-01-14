#!/usr/bin/env bash

# This script installs EnvManager on Linux systems.
# Execute in a terminal with administrator permissions.

# Local paths
LOCAL_BIN_PATH="/usr/local/bin"
LOCAL_LIB_PATH="/usr/local/lib"

# Destination paths
DEST="$LOCAL_LIB_PATH/com.neo.envmanager"
DEST_BIN="$DEST/bin"
DEST_LIB="$DEST/lib"

# Program files
sudo mkdir -p "$DEST_BIN"
sudo mkdir -p "$DEST_LIB"

sudo cp -r "./bin" "$DEST"
sudo cp -r "./lib" "$DEST"

# Terminal executable
sudo cp "./envm" "$LOCAL_BIN_PATH"

echo "✓ Installed"