package com.dayreminder.gui;

import com.dayreminder.DayReminderMod;
import com.dayreminder.config.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class WarningHud implements HudRenderCallback {
	private static long warningEndTime = 0;
	private static String warningText = "";

	public static void showWarning(String text) {
		warningText = text;
		warningEndTime = System.currentTimeMillis() + (ModConfig.get().warningDurationSeconds * 1000L);
		DayReminderMod.LOGGER.info("Showing warning '{}'", text);
	}

	@Override
	public void onHudRender(DrawContext context, net.minecraft.client.render.RenderTickCounter tickCounter) {
		if (!ModConfig.get().enabled)
			return;
		if (System.currentTimeMillis() > warningEndTime)
			return;

		MinecraftClient client = MinecraftClient.getInstance();
		TextRenderer textRenderer = client.textRenderer;

		int width = client.getWindow().getScaledWidth();
		int height = client.getWindow().getScaledHeight();

		Text text = Text.literal(warningText).formatted(Formatting.RED, Formatting.BOLD);

		// Get scale from config - clamp between 1 and 5
		float scale = Math.max(1.0f, Math.min(5.0f, ModConfig.get().textScale));

		// Calculate text dimensions
		int textWidth = textRenderer.getWidth(text);

		// Center position based on scaled dimensions
		int centerX = width / 2;
		int y = height / 3;

		// Try drawing with matrix scaling
		try {
			// Get the matrix stack
			var matrices = context.getMatrices();

			// Apply scaling transformation
			matrices.scale(scale, scale);

			// Adjust position for scale
			int adjustedX = (int) (centerX / scale) - (textWidth / 2);
			int adjustedY = (int) (y / scale);

			// Draw text at scaled position
			context.drawText(textRenderer, text, adjustedX, adjustedY, 0xFFFFFFFF, true);

			// Reset scale
			matrices.scale(1.0f / scale, 1.0f / scale);

		} catch (Exception e) {
			// Fallback to normal rendering if scaling fails
			DayReminderMod.LOGGER.error("Failed to scale text: {}", e.getMessage());
			context.drawText(textRenderer, text, centerX - (textWidth / 2), y, 0xFFFFFFFF, true);
		}
	}
}
