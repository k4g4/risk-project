import java.util.*;

public class Territory {
	private ArrayList<Territory> adjacentTerritories;
	private String name;
	
	public Territory() {
		this.adjacentTerritories = new ArrayList<Territory>();
		this.name = "";
	}

	public Territory(ArrayList<Territory> adjacentTerritories, String name) {
		this.adjacentTerritories = adjacentTerritories;
		this.name = name;
	}
	
	public ArrayList<Territory> getAdjacentTerritories() {
		return this.adjacentTerritories;
	}
}
