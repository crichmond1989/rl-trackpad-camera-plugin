# Manual Testing Guide

## Prerequisites

- macOS with a built-in or external Apple trackpad
- Java 11+ installed
- VS Code with the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)
- An OSRS account — Jagex Accounts are supported, see [JAGEX_ACCOUNT_DEV_SETUP.md](JAGEX_ACCOUNT_DEV_SETUP.md) for one-time setup

---

## Build the Plugin

```bash
./gradlew build
```

A successful build produces no checkstyle errors and exits with `BUILD SUCCESSFUL`.

---

## Launch RuneLite from VS Code

The project includes a `run` Gradle task ([build.gradle](../build.gradle)) that launches RuneLite in developer mode with your plugin already on the classpath — no JAR sideloading needed.

**Step 1 — Start RuneLite**

Open the VS Code Command Palette (`⌘⇧P`) → **Tasks: Run Task** → **Run RuneLite (Developer Mode)**

This runs `./gradlew run`, which:
- Resolves the RuneLite client JAR from the repo
- Puts your plugin classes on the classpath
- Launches `net.runelite.client.RuneLite --developer-mode`
- Starts a JDWP debug listener on port 5005

**Step 2 — Attach the debugger (optional)**

Once RuneLite is open, press `F5` or go to **Run and Debug** → **Attach to RuneLite (port 5005)**.

You can now set breakpoints anywhere in the plugin source and they will hit live.

**Step 3 — Enable the plugin**

In the RuneLite client, open **Configuration** (wrench icon), find **Trackpad Camera**, and enable it.

---

## Jagex Launcher Note

The `run` Gradle task launches RuneLite directly without the Jagex Launcher. Jagex Accounts are still supported — RuneLite reads credentials from `~/.runelite/credentials.properties`, which the Jagex Launcher can write once as part of a one-time setup.

See [JAGEX_ACCOUNT_DEV_SETUP.md](JAGEX_ACCOUNT_DEV_SETUP.md) for the full setup steps.

---

## Test Cases

For each test, ensure you are **logged in to a game world** and have the game window focused. The plugin only acts when `GameState == LOGGED_IN`.

---

### TC-01: Yaw Rotation — Two-Finger Horizontal Swipe

**Purpose:** Verify horizontal swipe rotates the camera left/right.

| Step | Action | Expected Result |
|------|--------|----------------|
| 1 | Swipe two fingers **left** on trackpad | Camera rotates **left** (counter-clockwise) |
| 2 | Swipe two fingers **right** on trackpad | Camera rotates **right** (clockwise) |
| 3 | Swipe slowly | Camera rotates slowly |
| 4 | Swipe quickly | Camera rotates faster |
| 5 | Increase **Rotation Sensitivity** to 20 in config, repeat step 1 | Rotation is noticeably faster |
| 6 | Disable **Enable Rotation** in config, swipe horizontally | Camera does **not** rotate |
| 7 | Re-enable **Enable Rotation** | Rotation resumes |

---

### TC-02: Pitch Tilt — Ctrl + Two-Finger Scroll

**Purpose:** Verify ctrl+scroll tilts the camera up/down.

| Step | Action | Expected Result |
|------|--------|----------------|
| 1 | Hold **Ctrl** and scroll two fingers **up** | Camera tilts **up** (more overhead view) |
| 2 | Hold **Ctrl** and scroll two fingers **down** | Camera tilts **down** (more horizontal view) |
| 3 | Ctrl+scroll until extreme up position | Camera clamps — does not go past top-down limit |
| 4 | Ctrl+scroll until extreme down position | Camera clamps — does not go past horizon limit |
| 5 | Disable **Enable Tilt** in config, ctrl+scroll | Camera does **not** tilt |
| 6 | Re-enable **Enable Tilt** | Tilt resumes |

> **Note:** If your system accessibility zoom (System Settings → Accessibility → Zoom → Use scroll gesture with modifier keys to zoom) triggers instead of camera tilt, disable it first.

---

### TC-03: Zoom — OSRS Native + Invert

**Purpose:** Verify vertical scroll zooms natively, and that Invert Zoom works when enabled.

| Step | Action | Expected Result |
|------|--------|----------------|
| 1 | Enable "Use Scroll Wheel to Zoom" in OSRS Settings → Controls → Camera | — |
| 2 | Scroll two fingers **up** (no Ctrl) | Camera **zooms in** |
| 3 | Scroll two fingers **down** (no Ctrl) | Camera **zooms out** |
| 4 | Enable **Invert Zoom** in plugin config | — |
| 5 | Scroll two fingers **up** (no Ctrl) | Camera now **zooms out** |
| 6 | Scroll two fingers **down** (no Ctrl) | Camera now **zooms in** |
| 7 | Disable **Invert Zoom** | Normal zoom direction restored |

---

### TC-04: Scroll Wheel Pass-Through

**Purpose:** Verify a physical scroll wheel (or Magic Mouse) still works normally.

| Step | Action | Expected Result |
|------|--------|----------------|
| 1 | Connect a physical scroll wheel mouse | — |
| 2 | Scroll **up** with the wheel | Camera **zooms in** (default RuneLite behavior) |
| 3 | Scroll **down** with the wheel | Camera **zooms out** |
| 4 | Plugin should **not** intercept these events | No unintended rotation or tilt |

---

### TC-05: Gesture Isolation (No Cross-Talk)

**Purpose:** Verify gestures don't bleed into each other.

| Step | Action | Expected Result |
|------|--------|----------------|
| 1 | Swipe purely horizontally | Only yaw changes — pitch and zoom unchanged |
| 2 | Swipe purely vertically | Only pitch changes — yaw and zoom unchanged |
| 3 | Ctrl+scroll with no horizontal component | Only pitch changes — yaw and zoom unchanged |

---

### TC-06: Logged-Out State

**Purpose:** Verify the plugin does nothing outside of an active game session.

| Step | Action | Expected Result |
|------|--------|----------------|
| 1 | Return to the lobby / login screen | — |
| 2 | Swipe and ctrl+scroll on the trackpad | No camera movement, no errors in the client log |

---

### TC-07: Plugin Enable / Disable

**Purpose:** Verify clean startup and shutdown.

| Step | Action | Expected Result |
|------|--------|----------------|
| 1 | Disable the plugin from the plugin list | All gestures stop working immediately |
| 2 | Scroll wheel zoom still works | Pass-through behavior unaffected |
| 3 | Re-enable the plugin | All gestures resume |
| 4 | Check RuneLite log for exceptions | No errors logged |

---

## Checking the RuneLite Log

Open the RuneLite developer console (**Help → Open Log Directory** or `--developer-mode` console output) and confirm:

- No `NullPointerException` or `IllegalArgumentException` from `com.trackpadcamera`
- Plugin registers/unregisters mouse listeners cleanly on toggle

---

## Quick Smoke Test Checklist

Run through this before each commit:

- [ ] Build succeeds (`./gradlew build`)
- [ ] Plugin loads without errors
- [ ] Horizontal swipe rotates camera (yaw)
- [ ] Ctrl + two-finger scroll tilts camera (pitch)
- [ ] Vertical scroll zooms (OSRS native)
- [ ] Invert Zoom works when enabled
- [ ] Scroll wheel zoom still works (pass-through)
- [ ] All toggles in config correctly enable/disable each gesture
- [ ] No exceptions in client log
