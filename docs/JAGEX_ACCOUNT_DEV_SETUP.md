# Jagex Account Developer Setup

How to use a Jagex Account (Jagex Launcher login) with a locally-launched RuneLite client for plugin development.

Source: [RuneLite wiki — Using Jagex Accounts](https://github.com/runelite/runelite/wiki/Using-Jagex-Accounts)

---

## How It Works

The RuneLite launcher can write your Jagex authentication credentials to a local file
(`.runelite/credentials.properties`). Any RuneLite client launched afterward — including
via `./gradlew runClient` — reads this file automatically and authenticates without
needing the Jagex Launcher present.

You only need to repeat the write step when the credentials expire or are invalidated.

---

## One-Time Setup

### Step 1 — Verify launcher version

Ensure your RuneLite launcher is **2.6.3 or newer**.

### Step 2 — Open launcher configure mode

```bash
/Applications/RuneLite.app/Contents/MacOS/RuneLite --configure
```

### Step 3 — Add the credential write flag

In the **Client arguments** field, enter:

```
--insecure-write-credentials
```

Click **Save**.

### Step 4 — Launch RuneLite via the Jagex Launcher once

Open RuneLite normally through the Jagex Launcher. It will write your credentials to:

```
~/.runelite/credentials.properties
```

### Step 5 — Remove the flag

Go back to configure mode and **remove** `--insecure-write-credentials` from Client arguments.
This prevents credentials from being rewritten on every launch.

---

## Security Warning

`credentials.properties` grants full account access **without a password**.

- Never share or commit this file
- It is already covered by `.gitignore` (verify if unsure)
- To invalidate credentials remotely: **runescape.com → Account Settings → End sessions**
- To stop local access: delete `~/.runelite/credentials.properties`

---

## After Setup

Once credentials are written, the normal dev workflow works without any changes:

1. Run **Tasks: Run Task → Run RuneLite (Developer Mode)** in VS Code
   (or `./gradlew runClient` in terminal)
2. RuneLite reads `credentials.properties` and logs in automatically
3. Attach the debugger if needed: **F5 → Attach to RuneLite (port 5005)**

See [MANUAL_TESTING.md](MANUAL_TESTING.md) for the full test workflow.
