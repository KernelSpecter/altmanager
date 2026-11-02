# Alt Manager

Offline account switcher for Minecraft 1.8.9 (Forge, works in Lunar Client). It keeps a list of usernames and switches the account you play as to one of them without restarting the game, like the alt managers built into some PvP clients.

Offline accounts only. An offline name works in singleplayer, on LAN and on offline-mode (cracked) servers. Online-mode servers like Hypixel refuse it with "Failed to verify username": that is Minecraft's login check, not something a client mod can get around. **Main account** switches you back to the account the launcher started the game with, so online servers work again without a restart.

## Install

1. Download `altmanager-1.0.jar` from the releases page.
2. Put it in your Forge 1.8.9 mods folder. For Lunar Client that is `%USERPROFILE%\.lunarclient\profiles\1.8\mods\forge-1.8.9\`.
3. Start the game and open the Alt Manager (see below).

## Opening it

| Where | How |
| --- | --- |
| Main menu and Multiplayer screen | The **Alt Manager** button in the top-left corner. "Logged in as" next to it shows the current name |
| In game | Type `/alts` in chat |
| Anywhere outside a menu | The **Open Alt Manager** key. It has no key by default; set one under Controls, in the Alt Manager category |

Lunar replaces the vanilla main menu and Multiplayer screen with its own, so the button doesn't show there. In Lunar, use `/alts` or the key.

## The screen

The top shows who you are logged in as, and whether that is your main account or an offline name. Below it is the list of saved alts. The one you are using is green and marked active.

| Button | What it does |
| --- | --- |
| Add | Save the name typed in the box to the list |
| Random | Fill the box with a random name like `SwiftFox482` |
| Login | Log in as the typed name (without saving it), or as the selected alt when the box is empty. Enter does the same |
| Delete | Remove the selected alt from the list |
| Main account | Switch back to the account the launcher started the game with |
| Done | Close the screen. Esc does the same |

Click an alt to select it and double-click to log in as it. Names have to follow Minecraft's rules: 3 to 16 letters, numbers or `_`. Invalid names and names already in the list (ignoring case) get a red message.

If you switch while you're in a world or on a server, that world keeps your old name. The new one is used from the next world or server you join.

## How switching works

Alt Manager replaces the game's session with an offline one: the new name, and the same UUID an offline-mode server works out for that name (`OfflinePlayer:<name>`), so the server and your client agree on who you are. It finds the session by its type instead of by field name, so it works with Forge's and Lunar's mappings alike.

## Storage

The list is saved to `.minecraft\config\altmanager.json` as `{"alts": ["name", ...]}`. It holds usernames only: offline accounts have no password or token, and your main account's login is never written anywhere. If the file can't be read, it is kept as `altmanager.json.bad` and the list starts empty.

## Build

Needs JDK 8. Run `gradlew build` in this folder (it runs the tests too). The jar ends up in `build\libs\`.

## License

MIT. See [LICENSE](LICENSE).
