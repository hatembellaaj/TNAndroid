#!/usr/bin/env bash
set -euo pipefail

# Generate iOS AppIcon files from Android logo source.
# Run on macOS (requires sips).

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
SOURCE_PNG="$ROOT_DIR/app/src/main/res/drawable/logo.png"
TARGET_DIR="$ROOT_DIR/swift/TNNewsApp/TNNewsApp/Assets.xcassets/AppIcon.appiconset"

if ! command -v sips >/dev/null 2>&1; then
  echo "❌ 'sips' is required (run this on macOS)."
  exit 1
fi

if [[ ! -f "$SOURCE_PNG" ]]; then
  echo "❌ Android source icon not found: $SOURCE_PNG"
  exit 1
fi

mkdir -p "$TARGET_DIR"

# iPhone/iPad common icon sizes (filenames + pixel dimensions)
icons=(
  "Icon-20@2x.png:40"
  "Icon-20@3x.png:60"
  "Icon-29@2x.png:58"
  "Icon-29@3x.png:87"
  "Icon-40@2x.png:80"
  "Icon-40@3x.png:120"
  "Icon-60@2x.png:120"
  "Icon-60@3x.png:180"
  "Icon-20-ipad@1x.png:20"
  "Icon-20-ipad@2x.png:40"
  "Icon-29-ipad@1x.png:29"
  "Icon-29-ipad@2x.png:58"
  "Icon-40-ipad@1x.png:40"
  "Icon-40-ipad@2x.png:80"
  "Icon-76-ipad@1x.png:76"
  "Icon-76-ipad@2x.png:152"
  "Icon-83.5-ipad@2x.png:167"
  "Icon-1024.png:1024"
)

for entry in "${icons[@]}"; do
  file="${entry%%:*}"
  size="${entry##*:}"
  sips -z "$size" "$size" "$SOURCE_PNG" --out "$TARGET_DIR/$file" >/dev/null
  echo "✅ generated $file ($size x $size)"
done

echo "\nDone. Set App Icons Source to 'AppIcon' in Xcode target settings if needed."
