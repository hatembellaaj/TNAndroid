#!/usr/bin/env bash
set -euo pipefail

# Generate iOS AppIcon files with TN launcher style (green square + "TN").
# Run on macOS (requires swift + AppKit).

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../.." && pwd)"
TARGET_DIR="$ROOT_DIR/swift/TNNewsApp/TNNewsApp/Assets.xcassets/AppIcon.appiconset"

if ! command -v swift >/dev/null 2>&1; then
  echo "❌ 'swift' command is required."
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

  swift - "$TARGET_DIR/$file" "$size" <<'SWIFT'
import AppKit
import Foundation

let args = CommandLine.arguments
guard args.count >= 3 else { exit(1) }
let outputPath = args[1]
let size = Int(args[2]) ?? 1024

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

// Canvas white (iOS icon alpha-safe background).
NSColor.white.setFill()
NSBezierPath(rect: NSRect(origin: .zero, size: canvasSize)).fill()

// Green rounded square like Android launcher icon.
let margin = CGFloat(size) * 0.09
let iconRect = NSRect(x: margin, y: margin, width: CGFloat(size) - 2 * margin, height: CGFloat(size) - 2 * margin)
let radius = CGFloat(size) * 0.20
let iconPath = NSBezierPath(roundedRect: iconRect, xRadius: radius, yRadius: radius)
NSColor(calibratedRed: 64/255, green: 214/255, blue: 98/255, alpha: 1).setFill()
iconPath.fill()

// Subtle darker border.
NSColor(calibratedRed: 45/255, green: 179/255, blue: 77/255, alpha: 1).setStroke()
iconPath.lineWidth = max(2, CGFloat(size) * 0.02)
iconPath.stroke()

// White letter blocks ("T" and "N"), centered.
let blockGap = CGFloat(size) * 0.05
let blockWidth = CGFloat(size) * 0.25
let blockHeight = CGFloat(size) * 0.36
let blockY = CGFloat(size) * 0.34
let totalWidth = blockWidth * 2 + blockGap
let startX = (CGFloat(size) - totalWidth) / 2

func drawLetterBlock(x: CGFloat, letter: String) {
    let rect = NSRect(x: x, y: blockY, width: blockWidth, height: blockHeight)
    let blockPath = NSBezierPath(roundedRect: rect, xRadius: blockWidth * 0.18, yRadius: blockWidth * 0.18)
    NSColor.white.setFill()
    blockPath.fill()

    let attrs: [NSAttributedString.Key: Any] = [
        .font: NSFont.systemFont(ofSize: CGFloat(size) * 0.23, weight: .heavy),
        .foregroundColor: NSColor(calibratedRed: 72/255, green: 182/255, blue: 88/255, alpha: 1)
    ]
    let text = NSAttributedString(string: letter, attributes: attrs)
    let textSize = text.size()
    let textPoint = NSPoint(
        x: rect.midX - textSize.width / 2,
        y: rect.midY - textSize.height / 2 - CGFloat(size) * 0.01
    )
    text.draw(at: textPoint)
}

drawLetterBlock(x: startX, letter: "T")
drawLetterBlock(x: startX + blockWidth + blockGap, letter: "N")

NSGraphicsContext.restoreGraphicsState()

guard let pngData = rep.representation(using: .png, properties: [:]) else { exit(3) }
try pngData.write(to: URL(fileURLWithPath: outputPath))
SWIFT

  echo "✅ generated $file (${size}x${size})"
done

echo "\nDone. Icons are generated with TN launcher style (green rounded square + TN letters)."
echo "Set App Icons Source to 'AppIcon' in Xcode target settings if needed."
