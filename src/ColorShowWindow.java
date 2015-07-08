import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class ColorShowWindow {
	JFrame colorFrame;
	JPanel colorPanel;
	
	public ColorShowWindow(int positionX,int positionY,Vector<Color> colorVector){
		colorFrame=new JFrame("Colors - Cetobasilius bot");
		colorFrame.setLayout(new FlowLayout());
		
		colorPanel = new JPanel();
		colorPanel.setPreferredSize(new Dimension(42,colorVector.size()*24));
    	
		for(int vectorIndex = 0;vectorIndex<colorVector.size();vectorIndex++){
			JLabel miniColorLabel = new JLabel(""+(vectorIndex+1));
			JPanel miniColorPanel = new JPanel();
			miniColorPanel.setBackground(colorVector.elementAt(vectorIndex));
			miniColorPanel.setPreferredSize(new Dimension(16,16));
			colorPanel.add(miniColorLabel);
			colorPanel.add(miniColorPanel);
		}
		
		colorFrame.add(colorPanel);
		colorFrame.pack();
		colorFrame.setSize(32,colorVector.size()*24+48);
		colorFrame.setLocation(positionX,positionY);
		colorFrame.setVisible(true);
		colorFrame.setAlwaysOnTop(true);
		
	}

	public void hide() {
		colorFrame.setVisible(false);
	}
}
