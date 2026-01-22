package com.dayreminder.gui;

import com.dayreminder.config.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class WarningHud implements HudRenderCallback {
	private static long warningEndTime = 0;
	private static String warningText = "";

	public static void showWarning(String text) {
		warningText = text;
		warningEndTime = System.currentTimeMillis() + (ModConfig.get().warningDurationSeconds * 1000L);
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
		int textWidth = textRenderer.getWidth(text);

		int x = (width - textWidth) / 2;
		int y = height / 3; // Top third of the screen

		// Scale up
		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(x + textWidth / 2.0, y + textRenderer.fontHeight / 2.0, 0);
		matrices.scale(2.0f, 2.0f, 2.0f);
		matrices.translate(-(x + textWidth / 2.0), -(y + textRenderer.fontHeight / 2.0), 0);

		context.drawText(textRenderer, text, x, y, 0xFFFFFF, true);

		matrices.pop();
	}
}
