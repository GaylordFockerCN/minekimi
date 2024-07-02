package net.p1nero.kimi;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.*;


public class Kimi implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("minekimi");

	public static final String MOD_ID = "minekimi";
	@Override
	public void onInitialize() {
//		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("kimi")
//						.then(argument("content", StringArgumentType.greedyString()))
//							.executes(context -> broadcast(context.getSource(), StringArgumentType.getString(context,"content")))));
	}

	public static int broadcast(ServerCommandSource source, String message) {
		Objects.requireNonNull(source.getPlayer()).sendMessage(Text.literal("Only in client"));
		return Command.SINGLE_SUCCESS; // 成功
	}

}