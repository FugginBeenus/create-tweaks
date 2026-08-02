#!/bin/sh
set -e
cd "$(dirname "$0")"

VERSION=1.0
OUT="dist/create-tweaks-$VERSION.zip"

mkdir -p dist
rm -f dist/create-tweaks*.zip

find datapack -name '.DS_Store' -delete

cd datapack
zip -qr "../$OUT" pack.mcmeta data
cd ..

echo "$OUT"
unzip -l "$OUT" | tail -1
