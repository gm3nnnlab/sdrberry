#!/bin/bash
# SessionStart hook for Claude Code on the web.
#
# This repo has two independent subprojects:
#   - the root: a C++ SDR app (CMake) that targets ARM64 Raspberry Pi hardware and
#     builds liquid-dsp and SoapySDR from source in CI (many minutes each) — not a
#     fit for a generic x86_64 cloud container, so this hook does not touch it.
#   - android/ChornobylHamDash: a Gradle/Kotlin Android app, which is what this hook
#     prepares: an Android SDK (platform 34, build-tools 34.0.0, platform-tools) and
#     a warm Gradle dependency cache, so `./gradlew` works immediately in the session.
set -euo pipefail

# Only run in Claude Code on the web; a local checkout already has its own SDK.
if [ "${CLAUDE_CODE_REMOTE:-}" != "true" ]; then
  exit 0
fi

REPO_ROOT="${CLAUDE_PROJECT_DIR:-$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)}"
ANDROID_PROJECT="$REPO_ROOT/android/ChornobylHamDash"

if [ ! -d "$ANDROID_PROJECT" ]; then
  exit 0
fi

SDK_ROOT="${ANDROID_SDK_ROOT:-$HOME/android-sdk}"
API_LEVEL="34"
BUILD_TOOLS="34.0.0"
# Android SDK command-line tools package, as currently linked from
# https://developer.android.com/studio#command-line-tools-only. sdkmanager itself
# updates the rest of the SDK, so this only needs to be a recent, working bootstrap.
CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-15859902_latest.zip"

mkdir -p "$SDK_ROOT"

if [ ! -x "$SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" ]; then
  echo "Downloading Android SDK command-line tools..."
  TMP_ZIP="$(mktemp)"
  TMP_DIR="$(mktemp -d)"
  trap 'rm -rf "$TMP_ZIP" "$TMP_DIR"' EXIT

  curl -sSL -o "$TMP_ZIP" "$CMDLINE_TOOLS_URL"
  unzip -q "$TMP_ZIP" -d "$TMP_DIR"

  mkdir -p "$SDK_ROOT/cmdline-tools"
  rm -rf "$SDK_ROOT/cmdline-tools/latest"
  mv "$TMP_DIR/cmdline-tools" "$SDK_ROOT/cmdline-tools/latest"
fi

SDKMANAGER="$SDK_ROOT/cmdline-tools/latest/bin/sdkmanager"

echo "Installing Android SDK packages (platform $API_LEVEL, build-tools $BUILD_TOOLS)..."
yes | "$SDKMANAGER" --sdk_root="$SDK_ROOT" --licenses > /dev/null 2>&1 || true
"$SDKMANAGER" --sdk_root="$SDK_ROOT" \
  "platform-tools" \
  "platforms;android-$API_LEVEL" \
  "build-tools;$BUILD_TOOLS" > /dev/null

# Persist for the rest of the session (new shells, and this hook re-run on resume).
{
  echo "export ANDROID_HOME=\"$SDK_ROOT\""
  echo "export ANDROID_SDK_ROOT=\"$SDK_ROOT\""
} >> "$CLAUDE_ENV_FILE"

# So Gradle finds the SDK even in a shell that hasn't sourced $CLAUDE_ENV_FILE yet.
echo "sdk.dir=$SDK_ROOT" > "$ANDROID_PROJECT/local.properties"

# Warm the Gradle wrapper distribution and dependency cache so the first real
# build/test in the session doesn't pay that cost. Non-fatal if it fails (e.g. a
# transient network hiccup) — the session can still retry the real command itself.
echo "Warming the Gradle dependency cache..."
( cd "$ANDROID_PROJECT" && ANDROID_HOME="$SDK_ROOT" ANDROID_SDK_ROOT="$SDK_ROOT" \
    ./gradlew help --console=plain > /dev/null 2>&1 ) || \
  echo "Warning: Gradle cache warm-up failed; the first gradlew invocation in the session may be slower." >&2

echo "Android SDK ready at $SDK_ROOT"
