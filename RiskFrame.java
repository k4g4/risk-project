import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class RiskFrame extends JFrame {
	private Container contentPane;
	private JPanel backgroundPanel;
	private static final int TERRITORY_BUTTON_WIDTH = 60;
	private static final int TERRITORY_BUTTON_HEIGHT = 30;
	
	public RiskFrame(String title, int width, int height) {
		super(title);
		setDefaultCloseOperation(RiskFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(width, height));
		contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
	}
	
	public void setBackground(String filename) {
		BufferedImage bg;
		try { bg = ImageIO.read(new File(filename)); }
		catch (IOException e) {
			Risk.print(String.format("Error: %s missing.", filename));
			System.exit(1);
			return;
		}
		
		backgroundPanel = new JPanel() {
			public void paint(Graphics g) {
				super.paintComponent(g);
				g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
			}
		};
		backgroundPanel.setLayout(null);
		contentPane.add(backgroundPanel, BorderLayout.CENTER);
	}
	
	public void addTerritoryButton(String label, int x, int y) {
		JButton territoryButton = new JButton(label);
		territoryButton.setBounds(
				backgroundPanel.getWidth() * (x/100),
				backgroundPanel.getHeight() * (y/100),
				TERRITORY_BUTTON_WIDTH,
				TERRITORY_BUTTON_HEIGHT);
		backgroundPanel.add(territoryButton);
	}
	
	public void render() {
		pack();
		setVisible(true);
	}
}
