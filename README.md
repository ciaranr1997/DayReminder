<h1 align="center">
  Day Reminder
</h1>

<div align="center">

[![Fabric](https://img.shields.io/badge/Fabric-1.21.5-blue?style=for-the-badge&logo=fabric&logoColor=white)](https://fabricmc.net/)
[![License](https://img.shields.io/badge/License-LGPL_v3-blue?style=for-the-badge)](LICENSE)

</div>

## What it does

**Day Reminder** is a lightweight Fabric utility mod for **Hypixel Skyblock**. It automatically tracks the in-game Skyblock Date and provides customizable reminders so you never miss a configured day again.

With Day Reminder you can:
*   **Track Recurring Events:** Set reminders for specific days to catch events like **Spooky Festival** (Days 29-31).
*   **Run Commands Quickly:** Click a button in chat to instantly warp or run a command.
*   **Customize Everything:** Create your own "Command Groups" to run different commands on specific days.

## Features

*   **Automatic Day Detection**: Parses the scoreboard sidebar to find the current Skyblock Date accurately (e.g., "Early Spring 7th").
*   **Command Groups**: Define strict rules for specific days using the config menu.
    *   *Example*: "Days 7, 14, 21, 28" -> Reminder to `/warp forge`.
    *   *Example*: "Days 29-31" -> Reminder to prep for Spooky Festival.
*   **Smart HUD Warning**: Displays a non-intrusive title on your screen when a reminder triggers.
*   **Chat Macro Button**: Sends a safe, clickable message in chat to execute your configured command.
*   **Anti-Spam**: Logic ensures you are only warned **once per day**, even if you swap lobbies or rejoin.

## Getting Started

1.  **Install**: Download `Day Reminder`, [Fabric API](https://modrinth.com/mod/fabric-api), [Cloth Config](https://modrinth.com/mod/cloth-config), and [Mod Menu](https://modrinth.com/mod/modmenu).
2.  **Launch**: Start the game on Fabric.
3.  **Config**: Open **Mods** -> **Day Reminder** -> **Config**.
4.  **Customize**: Use the GUI to add your own Day/Command rules!

## ToS Compliance (Hypixel)

This mod is designed to be safe and compliant:
*   **Read-Only**: It only reads information visible on your scoreboard.
*   **No Automation**: It does **not** automatically run commands. It provides a clickable chat button (similar to other skyblock mods) that requires user input.

## License

This project is free and open-source under the **GNU Lesser General Public License v3.0**.