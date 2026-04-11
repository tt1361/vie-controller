#!/bin/zsh
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
DIST_JAR="$ROOT_DIR/dist/vie-app-controller-0.0.1-SNAPSHOT.jar"
TARGET_DIR="$ROOT_DIR/../VIEWEB/WEB-INF/lib"
TARGET_JAR="$TARGET_DIR/vie-app-controller-0.0.1-SNAPSHOT.jar"
BACKUP_JAR="$TARGET_DIR/vie-app-controller-0.0.1-SNAPSHOT.jar.bak.$(date +%Y%m%d%H%M%S)"

if [[ ! -f "$DIST_JAR" ]]; then
  "$ROOT_DIR/package.sh"
fi

cp "$TARGET_JAR" "$BACKUP_JAR"
cp "$DIST_JAR" "$TARGET_JAR"

echo "Installed rebuilt jar to $TARGET_JAR"
echo "Backup written to $BACKUP_JAR"
