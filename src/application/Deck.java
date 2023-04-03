/**
 * Writer - 안학룡(BieNew22)
 * Role of this file
 * 				- save deck information from server with out matches information
 * Date of latest update - 2023.04.03
 */



package application;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class Deck {
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
