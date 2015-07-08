
import java.awt.AWTException;
import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, AWTException /*remember to actually handle these exceptions if you plan on doing something productive*/ {
        PuzzleBot bot = new PuzzleBot();
        while(true) //infinite loop
        {
            //bot.updateMousePositionInformation(); //find the mouse

        	
            bot.updatePuzzleFrame();
            
            //Thread.sleep(10); //wait for one second 
        }       
    }
}