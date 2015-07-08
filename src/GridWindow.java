import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JFrame;
import javax.swing.JLabel;


public class GridWindow {

	JFrame gridFrame;
    JLabel statusLabel;
    
    
    int globalSizeY;
    boolean miniToggled = false;
    int realPuzzleSizeGlobal;
    
    public GridWindow(int realPuzzleSize,int positionX,int positionY){
    	realPuzzleSizeGlobal=realPuzzleSize;
    	gridFrame = new JFrame("Grid - Cetobasilius bot");
        //gridFrameTransparent = new TransparentFrame();
    	//gridFrameTransparent.setLayout(new BorderLayout( ));
    	statusLabel = new JLabel("Status");
    	//gridFrameTransparent.add("South",statusLabel);
    	//gridFrame.getContentPane( ).add("Center",gridFrameTransparent);
    	gridFrame.pack( );
    	globalSizeY=realPuzzleSize+38;
    	gridFrame.setSize(realPuzzleSize+16,globalSizeY);
    	//gridFrame.setLocation((int)mainFrame.getLocation().getX()+(int)mainFrame.getSize().getWidth(),(int)mainFrame.getLocation().getY());
    	gridFrame.setLocation(positionX-29,positionY-20);
    	gridFrame.setVisible(true);
    	gridFrame.setAlwaysOnTop(true);
    	gridFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	
    	try {
    		Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
    		Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
    		mSetWindowOpacity.invoke(null, gridFrame, Float.valueOf(0.4f));
    	} catch (NoSuchMethodException ex) {
    		ex.printStackTrace();
    	} catch (SecurityException ex) {
    		ex.printStackTrace();
    	} catch (ClassNotFoundException ex) {
    		ex.printStackTrace();
    	} catch (IllegalAccessException ex) {
    		ex.printStackTrace();
    	} catch (IllegalArgumentException ex) {
    		ex.printStackTrace();
    	} catch (InvocationTargetException ex) {
    		ex.printStackTrace();
    	}
    	toggleSize();
    }

    public void update(){
    	gridFrame.repaint();
    	statusLabel.setText((int)gridFrame.getLocation().getX()+","+(int)gridFrame.getLocation().getY());
    }


    public int getPositionX() {
    	return (int)gridFrame.getLocation().getX();
    }

    public int getPositionY() {
    	return (int)gridFrame.getLocation().getY();
    }

	public void hide() {
		gridFrame.setVisible(false);
	}
	
	public void show() {
		gridFrame.setVisible(true);
	}

	public void toggleSize() {
		if(miniToggled==false){
			gridFrame.setSize(realPuzzleSizeGlobal+16,38);
			miniToggled=true;
		}
		else{
			gridFrame.setSize(realPuzzleSizeGlobal+16,globalSizeY);
			miniToggled=false;
		}
		
	}
}
