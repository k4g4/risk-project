import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class RiskFrame extends JFrame {
	private Container contentPane;
	private JPanel backgroundPanel;
	
	public RiskFrame(String title, int width, int height) {
		super(title);
		setDefaultCloseOperation(RiskFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(width, height));
		setResizable(false);
		contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
	}
	
	public void setBackground(BufferedImage bg) {
		backgroundPanel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
			}
		};
		backgroundPanel.setLayout(null);
		contentPane.add(backgroundPanel, BorderLayout.CENTER);
		pack();
	}
	
	public void addTerritoryButton(String label, int x, int y) {
		JButton territoryButton = new JButton(label);
		territoryButton.setBounds(
				(int)(backgroundPanel.getWidth() * ((float)x/100)),
				(int)(backgroundPanel.getHeight() * ((float)y/100)),
				label.length() * 8 + 50,
				30);
		backgroundPanel.add(territoryButton);
	}
	
	public void render() {
		pack();
		setVisible(true);
	}
}
