#!/bin/zsh
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
TARGET_DIR="$ROOT_DIR/target"
CLASS_DIR="$TARGET_DIR/classes"
RESOURCE_DIR="$ROOT_DIR/src/main/resources"
STAGE_DIR="$TARGET_DIR/package-stage"
DIST_DIR="$ROOT_DIR/dist"
JAR_NAME="vie-app-controller-0.0.1-SNAPSHOT.jar"
MANIFEST_FILE="$RESOURCE_DIR/META-INF/MANIFEST.MF"

"$ROOT_DIR/compile.sh"

rm -rf "$STAGE_DIR"
mkdir -p "$STAGE_DIR" "$DIST_DIR"

cp -R "$CLASS_DIR"/. "$STAGE_DIR"/
if [[ -d "$RESOURCE_DIR" ]]; then
  cp -R "$RESOURCE_DIR"/. "$STAGE_DIR"/
fi

rm -f "$DIST_DIR/$JAR_NAME"
(
  cd "$STAGE_DIR"
  jar cfm "$DIST_DIR/$JAR_NAME" "$MANIFEST_FILE" .
)

echo "Packaged jar written to $DIST_DIR/$JAR_NAME"
