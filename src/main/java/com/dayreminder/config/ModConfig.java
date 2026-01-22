package com.dayreminder.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {
	private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("dayreminder.json")
			.toFile();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private static ModConfig instance;

	public int warningDurationSeconds = 5;
	public boolean enabled = true;
	public java.util.List<CommandGroup> commandGroups = new java.util.ArrayList<>();

	public static class CommandGroup {
		public String name;
		public String command;
		public java.util.List<Integer> days;

		public CommandGroup(String name, String command, java.util.List<Integer> days) {
			this.name = name;
			this.command = command;
			this.days = days;
		}
	}

	public ModConfig() {
		// Default: Forge Reminders (Days 7, 14, 21, 28)
		java.util.List<Integer> forgeDays = new java.util.ArrayList<>();
		forgeDays.add(7);
		forgeDays.add(14);
		forgeDays.add(21);
		forgeDays.add(28);

		commandGroups.add(new CommandGroup("Forge Reminders", "/warp forge", forgeDays));
	}

	public String getCommandForDay(int day) {
		for (CommandGroup group : commandGroups) {
			if (group.days.contains(day)) {
				return group.command;
			}
		}
		return null;
	}

	public static ModConfig get() {
		if (instance == null) {
			load();
		}
		return instance;
	}

	public static void load() {
		if (CONFIG_FILE.exists()) {
			try (FileReader reader = new FileReader(CONFIG_FILE)) {
				instance = GSON.fromJson(reader, ModConfig.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (instance == null) {
			instance = new ModConfig();
			save(); // Save default
		}
	}

	public static void save() {
		if (instance == null)
			return;
		try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
			GSON.toJson(instance, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
