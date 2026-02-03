package com.dayreminder;

import com.dayreminder.config.ModConfig;
import com.dayreminder.gui.WarningHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DayReminderMod implements ClientModInitializer {
	public static final String MOD_ID = "dayreminder";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Pattern to catch "Early Summer 7th", mandating the suffix to avoid matching
	// "7m30s"
	private static final Pattern DAY_PATTERN = Pattern
			.compile("(?<season>[a-zA-Z\\s]+)\\s+(?<day>\\d+)(?:st|nd|rd|th)");

	// STATIC state to persist across server switches
	private static int lastWarnedDay = -1;
	private static String lastWarnedDateString = "";
	private static long lastWarningTime = 0;

	@Override
	public void onInitializeClient() {
		ModConfig.get(); // Init config
		HudRenderCallback.EVENT.register(new WarningHud());

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null || client.world == null)
				return;

			// Only run every 20 ticks (1 second) to save performance
			if (client.world.getTime() % 20 != 0)
				return;

			checkForEvents(client);
		});

		// Register Client Command /dayreminder status and /dayreminder test
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(literal("dayreminder")
					.then(literal("status")
							.executes(context -> {
								MinecraftClient client = MinecraftClient.getInstance();
								client.execute(() -> {
									String status = getStatusMessage(client);
									client.player.sendMessage(Text.literal(status), false);
								});
								return 1;
							}))
					.then(literal("test")
							.executes(context -> {
								MinecraftClient client = MinecraftClient.getInstance();
								client.execute(() -> {
									WarningHud.showWarning("Test Warning!");
									client.player.sendMessage(Text.literal("§a[DayReminder] §eTriggered test warning!"),
											false);
								});
								return 1;
							})));
		});

		LOGGER.info("DayReminder initialized!");
	}

	private void checkForEvents(MinecraftClient client) {
		if (!ModConfig.get().enabled)
			return;

		Scoreboard scoreboard = client.world.getScoreboard();
		ScoreboardObjective sidebar = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR); // This might be
																										// null?

		if (sidebar == null) {
			LOGGER.info("No sidebar found."); // Optional debug
			return;
		}

		Collection<ScoreboardEntry> scores = scoreboard.getScoreboardEntries(sidebar);
		if (scores.isEmpty()) {
			LOGGER.info("Sidebar found but no scores.");
		}

		for (ScoreboardEntry entry : scores) {
			String owner = entry.owner();
			String text = owner;

			// Reconstruct text from Team if present
			var team = scoreboard.getScoreHolderTeam(owner);
			if (team != null) {
				String prefix = team.getPrefix().getString();
				String suffix = team.getSuffix().getString();
				text = prefix + owner + suffix;
			}

			checkLine(client, text);
		}
	}

	private void checkLine(MinecraftClient client, String line) {
		// Aggressively strip ALL color codes (including non-standard ones like §t, §p
		// which Hypixel uses)
		String cleanLine = line.replaceAll("§.", "").trim();

		Matcher matcher = DAY_PATTERN.matcher(cleanLine);
		if (matcher.find()) {
			try {
				// Use named group "day" (or group 2)
				int day = Integer.parseInt(matcher.group("day"));

				String command = ModConfig.get().getCommandForDay(day);
				if (command != null) {
					// Logic:
					// 1. If line is DIFFERENT from last warned line, we might warn.
					// 2. OR if we have never warned this session.
					if (!cleanLine.equals(lastWarnedDateString)) {
						// Double check time delta (5 seconds) to prevent spam
						if (System.currentTimeMillis() - lastWarningTime > 5000) {

							LOGGER.info("DayReminder Triggered! Day: {}, Line: '{}'", day, cleanLine);

							triggerWarning(client, day, command);

							lastWarnedDay = day;
							lastWarnedDateString = cleanLine;
							lastWarningTime = System.currentTimeMillis();
						}
					}
				}
			} catch (NumberFormatException ignored) {
			}
		}
	}

	private void triggerWarning(MinecraftClient client, int day, String command) {
		// 1. HUD Warning
		WarningHud.showWarning("It is Day " + day + "!");

		// 2. Chat Message with Button
		// Command might be "/warp forge" -> Display "[WARP FORGE]"
		String upperCommand = command.replace("/", "").toUpperCase();

		Text warpCommand = Text.literal("[" + upperCommand + "]")
				.setStyle(Style.EMPTY
						.withColor(Formatting.GOLD)
						.withBold(true)
						.withClickEvent(new ClickEvent.RunCommand(command))
						.withHoverEvent(new HoverEvent.ShowText(Text.literal("Click to run " + command))));

		Text message = Text.literal("§c[DayReminder] §eIt is Day " + day + "! ")
				.append(warpCommand);

		client.player.sendMessage(message, false);
	}

	private String getStatusMessage(MinecraftClient client) {
		StringBuilder sb = new StringBuilder("§e[DayReminder Status]§r\n");
		sb.append("§7Last Notified: §f").append(lastWarnedDateString.isEmpty() ? "None" : lastWarnedDateString)
				.append("\n");

		// Manual scan
		if (client.world == null)
			return sb.append("§cWorld is null").toString();
		Scoreboard scoreboard = client.world.getScoreboard();
		ScoreboardObjective sidebar = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);

		if (sidebar == null) {
			sb.append("§cNo sidebar found.");
			return sb.toString();
		}

		Collection<ScoreboardEntry> scores = scoreboard.getScoreboardEntries(sidebar);
		String currentDayMatch = "None";

		for (ScoreboardEntry entry : scores) {
			String owner = entry.owner();
			String text = owner;
			var team = scoreboard.getScoreHolderTeam(owner);
			if (team != null) {
				text = team.getPrefix().getString() + owner + team.getSuffix().getString();
			}

			String cleanLine = text.replaceAll("§.", "").trim();
			Matcher matcher = DAY_PATTERN.matcher(cleanLine);

			if (matcher.find()) {
				currentDayMatch = cleanLine;
				break;
			}
		}

		sb.append("§7Current Date: §f").append(currentDayMatch);
		return sb.toString();
	}
}
