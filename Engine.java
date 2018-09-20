import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Engine {
	private RiskFrame riskFrame;
	private HashMap<String, Territory> territories;
	
	public Engine(String propertiesFile) {
		Properties properties = new Properties();
		try (FileInputStream propertiesFileStream = new FileInputStream(propertiesFile)) {
			properties.load(propertiesFileStream);
		}
		catch (IOException e) {
			Risk.print(String.format("Error: %s missing.", propertiesFile));
			System.exit(1);
		}
		try {
			String title = properties.getProperty("title");
			int width = Integer.parseInt(properties.getProperty("width"));
			int height = Integer.parseInt(properties.getProperty("height"));
			String bgFilename = properties.getProperty("bgFilename");
			riskFrame = new RiskFrame(title, width, height);
			BufferedImage bg;
			try { bg = ImageIO.read(new File(bgFilename)); }
			catch (IOException e) {
				Risk.print(String.format("Error: %s missing.", bgFilename));
				System.exit(1);
				return;
			}
			riskFrame.setBackground(bg);
			riskFrame.setTextWindow();
			territories = new HashMap<String, Territory>();
			String territoriesFilename = properties.getProperty("territoriesFilename");
			try(
					FileInputStream territoriesFileStream = new FileInputStream(territoriesFilename);
					BufferedReader reader = new BufferedReader(new InputStreamReader(territoriesFileStream))
				) {
				String territoryString = reader.readLine();
				while (territoryString != null) {
					String[] territoryProperties = territoryString.split(" ");
					addTerritory(
							territoryProperties[0].replace("_", " "),
							Integer.parseInt(territoryProperties[1]),
							Integer.parseInt(territoryProperties[2]));
					territoryString = reader.readLine();
				}
			}
			catch (IOException e) {
				Risk.print(String.format("Error: %s missing.", territoriesFilename));
			}
		}
		catch (Exception e) {
			Risk.print(e);
			System.exit(1);
		}
		riskFrame.render();
	}
	
	public HashMap<String, Territory> getTerritories() { return territories; }
	
	public void addTerritory(String title, int x, int y) {
		riskFrame.addTerritoryButton(title, x, y);
		territories.put(title, new Territory(title));
	}
}
