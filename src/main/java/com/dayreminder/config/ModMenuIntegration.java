package com.dayreminder.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

import java.util.List;

public class ModMenuIntegration implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {
			ModConfig config = ModConfig.get();

			ConfigBuilder builder = ConfigBuilder.create()
					.setParentScreen(parent)
					.setTitle(Text.of("Day Reminder Config"));

			ConfigEntryBuilder entryBuilder = builder.entryBuilder();

			// --- General Category ---
			ConfigCategory general = builder.getOrCreateCategory(Text.of("General"));

			general.addEntry(entryBuilder.startBooleanToggle(Text.of("Enable Mod"), config.enabled)
					.setDefaultValue(true)
					.setTooltip(Text.of("Toggle the entire mod on/off"))
					.setSaveConsumer(newValue -> config.enabled = newValue)
					.build());

			general.addEntry(entryBuilder
					.startIntSlider(Text.of("Warning Duration (Seconds)"), config.warningDurationSeconds, 1, 60)
					.setDefaultValue(5)
					.setTooltip(Text.of("How long the HUD warning stays on screen"))
					.setSaveConsumer(newValue -> config.warningDurationSeconds = newValue)
					.build());

			// --- Command Groups Category ---
			ConfigCategory groupsCategory = builder.getOrCreateCategory(Text.of("Command Groups"));

			// Helpful text
			groupsCategory.addEntry(entryBuilder.startTextDescription(Text.of("Add strict command rules here."))
					.setTooltip(Text.of("Each group defines a command that runs on specific days."))
					.build());

			// List to track groups to delete on save
			java.util.List<ModConfig.CommandGroup> groupsToRemove = new java.util.ArrayList<>();

			// Iterate existing groups
			for (int i = 0; i < config.commandGroups.size(); i++) {
				ModConfig.CommandGroup group = config.commandGroups.get(i);

				groupsCategory.addEntry(
						entryBuilder.startTextDescription(Text.of("§e--- Group " + (i + 1) + " ---")).build());

				// Name
				groupsCategory.addEntry(entryBuilder.startStrField(Text.of("Name"), group.name)
						.setDefaultValue("New Group")
						.setSaveConsumer(n -> group.name = n)
						.build());

				// Command
				groupsCategory.addEntry(entryBuilder.startStrField(Text.of("Command"), group.command)
						.setDefaultValue("/warp forge")
						.setTooltip(Text.of("Example: /warp forge"))
						.setSaveConsumer(c -> group.command = c)
						.build());

				// Days (CSV)
				String daysCsv = group.days.toString().replace("[", "").replace("]", "");
				groupsCategory.addEntry(entryBuilder.startStrField(Text.of("Days (e.g. 7, 14, 21)"), daysCsv)
						.setDefaultValue("7, 14")
						.setTooltip(Text.of("Comma separated list of days (1-31)"))
						.setSaveConsumer(s -> {
							java.util.List<Integer> newDays = new java.util.ArrayList<>();
							for (String part : s.split(",")) {
								try {
									String trimmed = part.trim();
									if (!trimmed.isEmpty()) {
										int d = Integer.parseInt(trimmed);
										if (d >= 1 && d <= 31)
											newDays.add(d);
									}
								} catch (NumberFormatException ignored) {
								}
							}
							group.days = newDays;
						})
						.build());

				// Delete Toggle
				groupsCategory.addEntry(entryBuilder.startBooleanToggle(Text.of("§cDelete This Group"), false)
						.setTooltip(Text.of("If checked, this group will be removed when you click Save."))
						.setSaveConsumer(b -> {
							if (b)
								groupsToRemove.add(group);
						})
						.build());
			}

			// Add New Group Toggle
			groupsCategory.addEntry(entryBuilder.startTextDescription(Text.of(" ")).build()); // Spacer
			groupsCategory.addEntry(entryBuilder.startBooleanToggle(Text.of("§a[+] Add New Group on Save"), false)
					.setTooltip(Text.of("Check this and click Save to append a new empty group."))
					.setSaveConsumer(b -> {
						if (b) {
							config.commandGroups.add(
									new ModConfig.CommandGroup("New Group", "/say hi", new java.util.ArrayList<>()));
						}
					})
					.build());

			builder.setSavingRunnable(() -> {
				config.commandGroups.removeAll(groupsToRemove);
				ModConfig.save();
			});

			return builder.build();
		};
	}
}
