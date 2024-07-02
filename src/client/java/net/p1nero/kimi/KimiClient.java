package net.p1nero.kimi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;


public class KimiClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("minekimi");
	public static final String MOD_ID = "minekimi";
	private static final ArrayList<JsonObject> HISTORIES = new ArrayList<>();

	@Override
	public void onInitializeClient() {

		Config.onInitialize();
		System.out.println("hello");
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(literal("kimi")
				.then(argument("message", StringArgumentType.greedyString())
					.executes(context -> {
						CompletableFuture<Text> future = getResponse(StringArgumentType.getString(context,"message"));
						future.thenAcceptAsync(response -> context.getSource().getPlayer().sendMessage(response));
						return Command.SINGLE_SUCCESS;

					}))));

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

			System.out.println(jsonBody);

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
				KimiClient.LOGGER.info("Received response from Kimi API" + response);

				if (choices == null || choices.isEmpty()) {
					KimiClient.LOGGER.info("ERROR: "+response.statusCode());
					return Text.literal("ERROR: "+response.statusCode()).formatted(Formatting.RED);
				}
				String responseText = choices.get(0).getAsJsonObject().getAsJsonObject("message").get("content").getAsString();
				KimiClient.LOGGER.info("Generated response: " + responseText);
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