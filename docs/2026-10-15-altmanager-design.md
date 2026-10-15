# Alt Manager: design

An Elixe-style alt manager for Forge 1.8.9 (the vanilla-launcher Forge instance, TLauncher's Forge 1.8.9, and Lunar).
Offline accounts only.

## What it does

- Keeps a list of offline usernames and switches the game's account to one of them without restarting.
- Switching to an offline name works in singleplayer, on LAN and on offline-mode (cracked) servers. Online-mode
  servers (Hypixel etc.) refuse it with "Failed to verify username"; that's Minecraft's auth, not something a client
  can get around.
- **Main account** switches back to whatever account the launcher started the game with (kept from startup), so
  online servers work again without a restart.

## How switching works

`Minecraft.session` is replaced with a new `Session(name, offlineId, "0", "legacy")`. The field is found by type, not
by name, so it works whatever mappings the client uses (Forge, Lunar). The offline id is
`UUID.nameUUIDFromBytes("OfflinePlayer:" + name)`, the same one offline-mode servers compute, so the server and the
client agree on who you are. The new name applies to the next server you join or world you open.

## Where to open it

- **Main menu and Multiplayer screen:** an "Alt Manager" button top-left, and "Logged in as <name>" beside it.
- **`/alts`** in chat, and an **Open Alt Manager** keybind (Controls > Alt Manager, unbound by default). These are
  for Lunar, whose own menus replace the vanilla ones the button is added to.

## The screen

- A scrolling list of saved alts; the active one is green. Click to select, double-click to log in.
- A username box, and buttons: **Add**, **Login** (logs in as the typed name without saving it, or the selected alt
  when the box is empty), **Random** (fills the box with a name like `SwiftFox482`), **Delete**, **Main account**,
  **Done**.
- Names must follow Minecraft's rules: 3-16 letters, digits and `_`. Bad names and duplicates get a red message.

## Storage

`config/altmanager.json` in the game folder: `{"alts": ["name", ...]}`. No passwords or tokens: offline accounts
have none. A file that can't be read is kept as `altmanager.json.bad` and the list starts empty.

## Units

- `Names`: validation and the random generator (pure).
- `OfflineIds`: offline UUID from a name (pure).
- `AltStore`: the list, add/remove, load/save to JSON (pure, file I/O only).
- `SessionSwitcher`: remembers the original session, swaps `Minecraft.session`.
- `GuiAltManager` + `AltList`: the screen.
- `AltManager`: the mod; menu button, `/alts`, keybind.

## Testing

JUnit for `Names`, `OfflineIds` (against Notch's known offline UUID) and `AltStore` (round trip, duplicates, corrupt
file). Manual in game: switch to an alt, open a singleplayer world and check the name in tab, switch back with Main
account.
