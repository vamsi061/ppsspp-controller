# PPSSPP Controller

Turn your Android phone into a wireless game controller for your Mac — inspired by PPSSPP's iconic semi-transparent overlay style.

## Overview

This project has two components:

- **Android App** (`GamingController/`) — Touchscreen controller UI that sends input over WiFi
- **Mac Receiver App** (`MacControllerApp/`) — macOS status bar app that receives inputs and simulates keyboard events via CGEvent

The Android phone connects to the Mac over the same WiFi network via TCP (port 9001), with automatic service discovery over UDP (port 9000).

## Features

- PPSSPP-style visual design: semi-transparent overlay, PSP button colors (▲ green, ● red, ✕ blue, ■ pink), dark gradient background
- Full layout customization: visual editor, key mapping, layout presets (Standard, PPSSPP, Fighting)
- Customizable layout with drag-to-position editor
- Dual analog joysticks with visual indicators
- D-pad and shoulder buttons (L1, R1, L2, R2)
- WiFi auto-discovery — no IP configuration needed
- Immersive fullscreen mode (hides status bar, swipe to reveal)
- 60fps input streaming for responsive gameplay

## Requirements

### Android
- Android 8.0+ (API 26+)
- WiFi connection (same network as Mac)

### Mac
- macOS 12.0+ (Monterey)
- **Accessibility permission** required (for CGEvent keyboard simulation)
  - Go to System Settings → Privacy & Security → Accessibility → add `MacControllerApp.app`

## Setup

### Mac Receiver
```bash
cd MacControllerApp
swift build -c release
cp .build/arm64-apple-macosx/release/MacControllerApp /Applications/MacControllerApp.app/Contents/MacOS/MacControllerApp
open /Applications/MacControllerApp.app
```

Grant Accessibility permission when prompted. The app runs as a status bar icon.

### Android App
Open `GamingController/` in Android Studio, build and deploy to your device. The app will auto-discover the Mac on the same network.

## Default Key Mappings

| Controller | Keyboard |
|---|---|
| ▲ (Triangle) | S |
| ● (Circle) | X |
| ✕ (Cross) | Z |
| ■ (Square) | A |
| D-Pad | Arrow Keys |
| Left Analog | I / K / J / L |
| L1 | Q |
| R1 | E |
| L2 | Q (hold) |
| R2 | E (hold) |
| SELECT | Enter |
| START | Space |
| ESC | Escape |

## Tech Stack

- **Android**: Kotlin, Jetpack Compose, Hilt, Gson
- **Mac**: Swift, SwiftUI, CGEvent, Network framework
- **Communication**: TCP (port 9001) + UDP discovery (port 9000)
