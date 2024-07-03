package net.p1nero.kimi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.*;


public class Kimi implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("minekimi");
	public static final String MOD_ID = "minekimi";
	private static final ArrayList<JsonObject> HISTORIES = new ArrayList<>();
	@Override
	public void onInitialize() {
		Config.onInitialize();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("kimi")
				.then(argument("message", StringArgumentType.greedyString())
						.executes(context -> {
							ServerPlayerEntity player = Objects.requireNonNull(context.getSource().getPlayer());
							String message = StringArgumentType.getString(context,"message");
							if(Config.BROADCAST){
								broadcast(player.getServerWorld(), Text.literal("<").append(player.getName()).append("> "+message));//不用append会带literal，append比较方便
							}
							CompletableFuture<Text> future = getResponse(message);
							future.thenAcceptAsync(response -> {
								if(Config.BROADCAST){
//									player.sendMessage(Text.literal("<"+player.getDisplayName()+"> "+message),false);//在收到回复前显示比较好
									broadcast(player.getServerWorld(), response);
								} else {
									player.sendMessageToClient(response, false);
								}
							});
							return Command.SINGLE_SUCCESS;
						}))));
	}

	/**
	 * 不知有没有什么别的发给全体的办法
	 */
	public static void broadcast(ServerWorld world, boolean overlay, Text ...messages){
		for(ServerPlayerEntity player : world.getPlayers()){
			for(Text message : messages){
				player.sendMessage(message, overlay);
			}
		}
	}

	public static void broadcast(ServerWorld world, Text ...messages){
		broadcast(world, false, messages);
	}

	public static CompletableFuture<Text> getResponse(String message) {

		return CompletableFuture.supplyAsync(()->{
			String jsonBody = "{" +
					" \"model\": \""+Config.MODEL+"\"," +
					"\"messages\": "+ getHistory(message) + "," +
					"\"temperature\": "+Config.TEMPERATURE +
					"}";

//			JsonObject init = new JsonObject();
//			init.addProperty("model", Config.MODEL);
//			init.addProperty("messages", getHistory(message));
//			init.addProperty("temperature", Config.TEMPERATURE);
//
//			System.out.println(init);

			try {
				HttpClient client = HttpClient.newHttpClient();
				HttpRequest request = HttpRequest.newBuilder()
						.uri(URI.create(Config.API_LINK))
						.header("Content-Type", "application/json")
						.header("Authorization", "Bearer " + Config.API_KEY)
						.POST(HttpRequest.BodyPublishers.ofString(jsonBody))
						.build();
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
				JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();
				JsonArray choices = responseJson.getAsJsonArray("choices");
				LOGGER.info("Received response from Kimi API" + response);

				if (choices == null || choices.isEmpty()) {
					LOGGER.info("ERROR: "+response.statusCode());
					return Text.literal("ERROR: "+response.statusCode()).formatted(Formatting.RED);
				}
				String responseText = choices.get(0).getAsJsonObject().getAsJsonObject("message").get("content").getAsString();
				LOGGER.info("Generated response: " + responseText);
				return Text.literal("["+Config.NAME+"]: ").formatted(Formatting.YELLOW).append(Text.literal(responseText).formatted(Formatting.AQUA));
			} catch (Exception e) {
				return Text.literal("ERROR: "+e.getCause()).formatted(Formatting.RED);
			}
		});
	}

	/**
	 * 构造多轮对话并返回，最大记录限度为SIZE
	 */
	public static String getHistory(String message){

		JsonObject newMessage = new JsonObject();
		newMessage.addProperty("role", "user");
		newMessage.addProperty("content", message);

		if(HISTORIES.size() > Config.SIZE){
			HISTORIES.clear();
		}
		if (HISTORIES.isEmpty()){
			JsonObject init = new JsonObject();
			init.addProperty("role", "system");
			init.addProperty("content", Config.SYSTEM);
			HISTORIES.add(init);
		}
		HISTORIES.add(newMessage);
		LOGGER.info(HISTORIES.toString());

		return new Gson().toJson(HISTORIES);

	}

}