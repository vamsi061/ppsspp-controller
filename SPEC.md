# GamingController - Remote Desktop Controller App

## Project Overview
- **Project Name**: GamingController
- **Type**: Cross-platform remote control application
- **Core Functionality**: Android app that connects to Mac over local WiFi and provides a gaming controller interface to simulate keyboard/mouse inputs on Mac
- **Target Users**: Gamers who want to use Android device as a gaming controller for Mac games/apps

## Technology Stack & Choices

### Android Side
- **Language**: Kotlin 1.9.x
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Architecture**: MVVM with Clean Architecture
- **UI Framework**: Jetpack Compose with Material 3
- **Networking**: UDP for discovery, TCP for data transfer
- **DI**: Hilt
- **Async**: Kotlin Coroutines + Flow

### Mac Side
- **Language**: Swift 5.9
- **Target**: macOS 12+
- **Networking**: Network framework (TCP listener)
- **Input Simulation**: CGEvent for keyboard/mouse simulation (requires Accessibility permissions)

## Feature List

### Android App
1. **Device Discovery** - Scan local network for Mac servers via UDP broadcast
2. **Connection Management** - Connect/disconnect to discovered Mac devices
3. **Gaming Controller UI**:
   - Virtual analog stick (left) for WASD movement
   - Virtual analog stick (right) for mouse/camera control
   - Face buttons (A/B/X/Y) mapped to keyboard keys
   - Shoulder buttons (L1/R1) for shift/ctrl modifiers
   - Trigger buttons (L2/R2) for mouse clicks
   - D-pad for additional key bindings
4. **Connection Status** - Show connected/disconnected state
5. **Settings** - Configure key mappings, connection port

### Mac Server App
1. **TCP Server** - Listen for incoming connections from Android
2. **Input Parser** - Decode controller input packets
3. **Input Simulation** - Convert inputs to keyboard/mouse events
4. **Status Display** - Show connected clients

## UI/UX Design Direction

### Visual Style
- Material Design 3 with dark theme (gaming aesthetic)
- Accent color: Electric blue (#2196F3) on dark background
- High contrast controls for visibility

### Color Scheme
- Background: Dark gray (#121212)
- Surface: Darker gray (#1E1E1E)
- Primary: Electric blue (#2196F3)
- Secondary: Purple accent (#9C27B0)
- Controller buttons: Distinct colors for face buttons (A=green, B=red, X=blue, Y=yellow)

### Layout Approach
- Single screen with controller UI
- Top bar with connection status and settings
- Bottom area with two analog sticks
- Center area with face buttons and D-pad

### Controller Layout
```
[Connection Status]          [Settings]
-----------------------------------
    [L-Stick]    [Face Buttons]   [R-Stick]
                  [D-Pad]
-----------------------------------
[Shoulder] [Triggers] [Shoulder]
```

## Communication Protocol

### Packet Structure (JSON over TCP)
```json
{
  "type": "input",
  "data": {
    "leftStick": {"x": 0.5, "y": -0.3},
    "rightStick": {"x": 0.0, "y": 0.0},
    "buttons": {"a": true, "b": false, "x": false, "y": false},
    "shoulders": {"l1": false, "r1": false},
    "triggers": {"l2": false, "r2": false},
    "dpad": {"up": false, "down": false, "left": false, "right": false}
  }
}
```

### Discovery Protocol (UDP)
- Android broadcasts to 255.255.255.255:9000
- Mac responds with: `{"type": "announce", "name": "MacBook-Pro", "ip": "192.168.1.x", "port": 9001}`