#!/bin/zsh
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
TARGET_DIR="$ROOT_DIR/target"
CLASS_DIR="$TARGET_DIR/classes"
SOURCE_LIST="$TARGET_DIR/sources.list"

mkdir -p "$CLASS_DIR"

find "$ROOT_DIR/src/main/java" -name '*.java' | sort > "$SOURCE_LIST"

LIB_CP=""
for jar in "$ROOT_DIR"/libs/*.jar; do
  if [[ -f "$jar" ]]; then
    if [[ -z "$LIB_CP" ]]; then
      LIB_CP="$jar"
    else
      LIB_CP="$LIB_CP:$jar"
    fi
  fi
done

for jar in "$ROOT_DIR"/../VIEWEB/WEB-INF/lib/*.jar; do
  if [[ -z "$LIB_CP" ]]; then
    LIB_CP="$jar"
  else
    LIB_CP="$LIB_CP:$jar"
  fi
done

javac \
  -encoding UTF-8 \
  -source 8 \
  -target 8 \
  -parameters \
  -cp "$LIB_CP" \
  -d "$CLASS_DIR" \
  @"$SOURCE_LIST"

echo "Compiled classes written to $CLASS_DIR"
