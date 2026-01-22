<h1 align="center">
  Day Reminder
</h1>

<div align="center">

[![Made with Java](https://img.shields.io/badge/Made%20With-Java-orange?style=for-the-badge&logo=java&logocolor=white)](https://openjdk.org/)
[![Fabric](https://img.shields.io/badge/Fabric-1.21.5-blue?style=for-the-badge&logo=fabric&logoColor=white)](https://fabricmc.net/)
[![License](https://img.shields.io/badge/License-LGPL_v3-blue?style=for-the-badge)](LICENSE)

</div>

## What it does

**Day Reminder** is a lightweight Fabric 1.21.5 utility mod for **Hypixel Skyblock**. It helps you track the in-game Skyblock Date and provides customizable reminders so you never miss an event again.

With Day Reminder you can:
*   **Track Events:** Get alerted on days like the **Mining Fiesta**, **Jerry's Workshop**, or **Spooky Festival**.
*   **Run Commands Quickly:** Click a button in chat to instantly warp or run a command.
*   **Customize Everything:** Create your own "Command Groups" to run different commands on different days.

## Features

*   **Automatic Day Detection**: Parses the scoreboard sidebar to find the current Skyblock Date accurately (e.g., "Early Spring 7th").
*   **Command Groups**: Define strict rules for specific days.
    *   *Example*: "Days 7, 14, 21, 28" -> Reminder to `/warp forge`.
    *   *Example*: "Days 29-31" -> Reminder to prep for Spooky Festival.
*   **Smart HUD Warning**: Displays a non-intrusive title on your screen when a day triggers.
*   **Chat Macro Button**: Sends a safe, clickable message in chat to execute your configured command.
*   **Anti-Spam**: Logic ensures you are only warned **once per day**, even if you swap lobbies or rejoin.

## Getting Started

1.  **Install**: Download `DayReminder`, [Fabric API](https://modrinth.com/mod/fabric-api), [Cloth Config](https://modrinth.com/mod/cloth-config), and [Mod Menu](https://modrinth.com/mod/modmenu).
2.  **Launch**: Start the game on Fabric 1.21.5.
3.  **Config**: Open **Mods** -> **Day Reminder** -> **Config**.
4.  **Customize**: Use the GUI to add your own Day/Command rules!


## License

This project is free and open-source under the **GNU Lesser General Public License v3.0**. You are free to use, modify, and distribute it.
