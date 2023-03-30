/**
 * Writer - 안학룡(BieNew22)
 * Role of this file
 * 				- Just like DB manager
 *              - Update user DB
 *              - Read and manage game DB
 * Date of latest update - 2023.03.29
 */

package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


class Deck {
	public String id = null;
	public String name = null;
	public ArrayList<String> cardList = new ArrayList<>();
	
	public Deck(JsonNode data) {
		id = data.get("Id").asText("Error");
		name = data.get("Name").asText("Error");
		
		if (id.equals("Error") || name.equals("Error")) {
			System.out.println("deck data error");
			System.exit(1);
		}
		
		ArrayNode cards = (ArrayNode) data.get("Cards");
		for (int i = 0; i < cards.size(); i++) {
			cardList.add(new String(cards.get(i).get("CardDefId").asText("Erro")));
		}
		
		// test print
//		System.out.println(id + " " + name);
//		for (int i = 0 ; i < cardList.size(); i++) {
//			System.out.println(cardList.get(i));
//		}
	}
	
	public boolean checkId(String find) {
		if (id.equals(find)) {
			return true;
		}
		return false;
	}
}

public class Database {
	private static Database instance = new Database();
	
	private ObjectNode userDB = null;
	
	private ArrayList<Deck> deckList = new ArrayList<>();
	
	private String selectedDeckId = null; //PlayState.json -> SelectedDeckId
	
	private int[] matches = new int[3]; // 0 : win, 1 : lose, 2 : tie
	
	private Database() {}
	
	//basic folder : C:\Users\name\AppData\LocalLow\Second Dinner\SNAP\Standalone\States\nvprod
	public static Database getInstance() {
		return instance;
	}

	public void init() {
		ObjectMapper mapper = new ObjectMapper();
		
		// initialization user userDB information
		userDB = (ObjectNode) openJson(mapper, "./src/db.json");
		
		//--- initialization data from marvel snap AppData ---//
		String dir =  System.getProperty("user.dir").split("Desktop")[0] + "AppData/LocalLow/Second Dinner/"
				+ "SNAP/Standalone/States/nvprod";
		//test directory
		dir =  System.getProperty("user.dir").split("Desktop")[0] + "Downloads";
		
		// 1. read deck information
		JsonNode deckDB = openJson(mapper, dir + "/CollectionState.json");
		
		// get Deck list(ArrayNode)
		ArrayNode deckNode = (ArrayNode) deckDB.at("/ServerState/Decks");
		for (int i = 0; i < deckNode.size(); i++) {
			deckList.add(new Deck(deckNode.get(i)));
		}
		
		// 2. read now user selected deck information
		JsonNode selectDeck = openJson(mapper, dir + "/PlayState.json");
		
		selectedDeckId = selectDeck.get("SelectedDeckId").asText("Error");
		
		// 3. read total number of match (win, lose, tie)
		JsonNode match = openJson(mapper, dir + "/ProfileState.json");
		
		matches[0] = match.at("/ServerState/Account/WinsInPlaytestEnvironment").asInt();
		matches[1] = match.at("/ServerState/Account/LossesInPlaytestEnvironment").asInt();
		matches[2] = match.at("/ServerState/Account/TiesInPlaytestEnvironment").asInt();
		
		// sync db.json and server deck info
		syncDatabase(mapper);
	}
	
	private JsonNode openJson(ObjectMapper mapper, String dir) {
		File file = new File(dir);
		
		JsonNode res = null;
		
		try {
			res = mapper.readTree(file);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return res;
	}
	
	private JsonNode stringToJson(ObjectMapper mapper, String str) {
		JsonNode res = mapper.createArrayNode();
		
		try {
			res = mapper.readTree(str);
		} catch (JsonMappingException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return res;
	}
	
	private void syncDatabase(ObjectMapper mapper) {
		JsonNode basicNode = stringToJson(mapper, "{\"infor\" : {"
				+ "			\"total\": 0,"
				+ "			\"win\": 0,"
				+ "			\"lose\": 0,"
				+ "			\"tie\": 0,"
				+ "			\"cube\": 0"
				+ "		}, \"log\": [{"
				+ "				\"who\": \"\","
				+ "				\"state\": \"\","
				+ "				\"cube\": 0"
				+ "			}]}");
		
		// add new deck
		for (Deck d: deckList) {
			if (userDB.at("/" + d.id).asText("None").equals("None")) {
				userDB.set(d.id, basicNode);
				//userDB.set(d.id, 101); - for PrimitiveType
			}
		}
		
		// remove deleted deck
		ArrayList<String> key = new ArrayList<>();
		userDB.fieldNames().forEachRemaining(key::add);
		
		
		for (String id: key) {
			if(findDeck(id) == null) {
				userDB.remove(id);
			}
		}
	}
	
	public Deck findDeck(String deckName) {
		for (Deck d: deckList) {
			if (d.checkId(deckName)) {
				return d;
			}
		}
		return null;
	}
}
