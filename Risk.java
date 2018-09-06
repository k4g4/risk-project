import java.io.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class Risk {

	private static class RiskFrame extends JFrame {
		
		public RiskFrame(String title, int width, int height) {
			super(title);
			setDefaultCloseOperation(RiskFrame.EXIT_ON_CLOSE);
			setPreferredSize(new Dimension(width, height));
		}
		
		public void showText() {
			this.add(new JLabel("Test"));
	    }
		
		public void setBackground(String filename, int width, int height) {
			BufferedImage bg;
			try {
				bg = ImageIO.read(new File(filename));
			} catch (IOException e) { return; }
			setContentPane(new JPanel() {
				public void paint(Graphics g) {
					super.paintComponent(g);
					g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
				}
			});
		}
		
		public void render() {
			pack();
			setVisible(true);
		}
	}
	
	public static void main(String[] args) {
		String title = "Risk";
		String bg = "images/bg.jpg";
		int width = 1920;
		int height = 1080;
		RiskFrame frame = new RiskFrame(title, width, height);
		frame.setBackground(bg, width, height);
		frame.render();
		
  }
}
