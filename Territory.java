import java.util.*;

public class Territory {
	private ArrayList<Territory> adjacentTerritories;
	private String name;
	
	public Territory(String name) {
		adjacentTerritories = new ArrayList<Territory>();
		this.name = name;
	}
	
	public ArrayList<Territory> getAdjacentTerritories() {
		return adjacentTerritories;
	}
}
