#!/usr/bin/env bash
set -euo pipefail

# Generate iOS AppIcon files from Android logo source.
# Run on macOS (requires swift + AppKit).

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../.." && pwd)"
SOURCE_PNG="$ROOT_DIR/app/src/main/res/drawable/logo.png"
TARGET_DIR="$ROOT_DIR/swift/TNNewsApp/TNNewsApp/Assets.xcassets/AppIcon.appiconset"

if ! command -v swift >/dev/null 2>&1; then
  echo "❌ 'swift' command is required."
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

  swift - "$SOURCE_PNG" "$TARGET_DIR/$file" "$size" <<'SWIFT'
import AppKit
import Foundation

let args = CommandLine.arguments
guard args.count >= 4 else { exit(1) }
let sourcePath = args[1]
let outputPath = args[2]
let size = Int(args[3]) ?? 1024

guard let source = NSImage(contentsOfFile: sourcePath) else {
    fputs("❌ Cannot open source image: \(sourcePath)\n", stderr)
    exit(2)
}

let canvasSize = NSSize(width: size, height: size)
let rep = NSBitmapImageRep(
    bitmapDataPlanes: nil,
    pixelsWide: size,
    pixelsHigh: size,
    bitsPerSample: 8,
    samplesPerPixel: 4,
    hasAlpha: true,
    isPlanar: false,
    colorSpaceName: .deviceRGB,
    bytesPerRow: 0,
    bitsPerPixel: 0
)!

NSGraphicsContext.saveGraphicsState()
let ctx = NSGraphicsContext(bitmapImageRep: rep)!
NSGraphicsContext.current = ctx

// TN green background to avoid transparent/blank launcher icon.
NSColor(calibratedRed: 54/255, green: 199/255, blue: 80/255, alpha: 1).setFill()
NSBezierPath(rect: NSRect(origin: .zero, size: canvasSize)).fill()

// Center Android logo with safe margins.
let inset = CGFloat(size) * 0.18
let targetRect = NSRect(x: inset, y: inset, width: CGFloat(size) - 2 * inset, height: CGFloat(size) - 2 * inset)
source.draw(in: targetRect, from: .zero, operation: .sourceOver, fraction: 1.0)

NSGraphicsContext.restoreGraphicsState()

guard let pngData = rep.representation(using: .png, properties: [:]) else { exit(3) }
try pngData.write(to: URL(fileURLWithPath: outputPath))
SWIFT

  echo "✅ generated $file (${size}x${size})"
done

echo "\nDone. Icons are generated with non-transparent TN green background."
echo "Set App Icons Source to 'AppIcon' in Xcode target settings if needed."
