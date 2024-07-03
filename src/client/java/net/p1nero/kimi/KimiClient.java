package net.p1nero.kimi;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KimiClient implements ClientModInitializer {
	private static KeyBinding openKimi;

	@Override
	public void onInitializeClient() {

		openKimi = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.minekimi.open",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_K,
				"category.minekimi.open"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openKimi.wasPressed()) {
				client.setScreen(new ChatScreen("/kimi "));
			}
		});

	}


}