import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PuzzleBot {
	
	private int botTimer = 73;
	int screenSizeX = Toolkit.getDefaultToolkit().getScreenSize().width;
    int screenSizeY = Toolkit.getDefaultToolkit().getScreenSize().height;

    private static final int REAL_PUZZLE_BOX_SIZE = 40;
    private static final int REAL_PUZZLE_GRID_SIZE = 320;
    
    
	private static final int PUZZLE_REPRESENTATION_BOX_SIZE = 16;
	private static final int PUZZLE_Y = 8;
	private static final int PUZZLE_X = 8;
	private Color pixelColor;
    private Robot robot = null;
    private PointerInfo pointer;
    private Point coord;
    private int mouseX;
    private int mouseY;
    
    Vector<Color> puzzleColors = new Vector<Color>();
    
    private int scanLocationX;
    private int scanLocationY;
       
    JFrame mainFrame;
    JPanel mainPanel;
    JButton resetBotButton;
    JButton startBotButton;
    JCheckBox loopBotCheckBox;
    JTextField timerTextField;
    
    BufferedImage desktopImage;
    JLabel mainStatusLabel;
    JLabel cursorInfoLabel;
    
    GridWindow gridWindow;
    ColorShowWindow colorShowWindow;
    
    private boolean solvePuzzle = false;
    
    public class BBoolean{
    	private boolean value = false;
    	public BBoolean(boolean state) {
    		value = state;
    	}
		public boolean isValue() {
			return value;
		}
		public void setValue(boolean value) {
			this.value = value;
		}
		public boolean isTrue() {
			if(value) {
				return true;
			}
			return false;
		}
		
		public boolean isFalse() {
			if(!value) {
				return true;
			}
			return false;
		}
    }
    
    public class ClockTimer extends Thread{
    	public boolean timerEnded = true;
    	long t0,t1;
    	
    	public ClockTimer(int seconds){
            t0=System.currentTimeMillis();
            t1=t0+(1000*seconds);
    	}
    	
    	public void run(){
    		while(true){
    			if(timerEnded==false){
    				t0=System.currentTimeMillis();

    				if(t0>t1){
    					timerEnded=true;
    					resetBot();	
    				}
    			}
    			
    			try {
					sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    	}
    	
    	public int getElapsedTime(){
    		return (int)((t1-t0)/1000);
    	}
    	
    
    	public void stopTimer(){
    		timerEnded=true;
    	}
    	
    	public void setTimer(int seconds){
    		t0=System.currentTimeMillis();
            t1=t0+(1000*seconds);
            timerEnded=false;
    	}
    }
    
    ClockTimer timer;
    
    //private int puzzleArray[][] = new int[PUZZLE_X][PUZZLE_Y];
    private Color puzzleArrayColor[][] = new Color[PUZZLE_X][PUZZLE_Y];
    private JPanel puzzleArrayPanels[][] = new JPanel[PUZZLE_X][PUZZLE_Y];

    public PuzzleBot() throws AWTException //AWTException is for the odd circumstance that no mouse is attached.
    {
        robot = new Robot();
        pointer = MouseInfo.getPointerInfo(); //get raw pointer info
        coord = pointer.getLocation(); //translate pointer info into a point
        //mouseX = coord.x; //separate the x and y values of the point
        //mouseY = coord.y;
        //pixelColor = robot.getPixelColor(mouseX, mouseY); //get the color
        
    	mainPanel = new JPanel();
    	
    	mainFrame = new JFrame("Puzzle Bot - Cetobasilius");
    	mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	mainFrame.setLayout(new FlowLayout());
    	
    	
    	
    	for(int x=0;x<PUZZLE_X;x++){
    		for(int y=0;y<PUZZLE_Y;y++){
    			puzzleArrayColor[x][y] = new Color(255,0,0);
    			puzzleArrayPanels[x][y] = new JPanel();
    			puzzleArrayPanels[x][y].setBackground(puzzleArrayColor[x][y]);
    			puzzleArrayPanels[x][y].setPreferredSize(new Dimension(PUZZLE_REPRESENTATION_BOX_SIZE,PUZZLE_REPRESENTATION_BOX_SIZE));
    			
    			mainPanel.add(puzzleArrayPanels[x][y]);
        	}
    	}
    	mainPanel.setPreferredSize(new Dimension((PUZZLE_REPRESENTATION_BOX_SIZE*PUZZLE_X)+(PUZZLE_REPRESENTATION_BOX_SIZE*3),(PUZZLE_REPRESENTATION_BOX_SIZE*PUZZLE_Y+(PUZZLE_REPRESENTATION_BOX_SIZE*3))));
    	mainFrame.getContentPane().add(mainPanel);
    	
    	startBotButton = new JButton("Start");
    	startBotButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	startBotting();
            }
        });
    	
    	
    	resetBotButton = new JButton("Reset");
    	resetBotButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	resetBot();
            }
        });
    	resetBotButton.setEnabled(false);
    	
    	cursorInfoLabel= new JLabel("Cursor");

    	
    	mainStatusLabel = new JLabel("Status");
    	
    	
    	JButton miniBotGridButton = new JButton("-");
    	miniBotGridButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	gridWindow.toggleSize();
            }
        });
    	
    	loopBotCheckBox = new JCheckBox("Navigate Menus and Loop");
    	loopBotCheckBox.setSelected(false);
    	
    	
    	timerTextField = new JTextField();
    	timerTextField.setColumns(2);
    	timerTextField.setText(""+botTimer);
    	
    	mainFrame.getContentPane().add(startBotButton);
    	mainFrame.getContentPane().add(resetBotButton);
    	mainFrame.getContentPane().add(miniBotGridButton);
    	
    	mainFrame.getContentPane().add(new JLabel("Time:"));
    	mainFrame.getContentPane().add(timerTextField);
    	
    	mainFrame.getContentPane().add(mainStatusLabel);
    	mainFrame.getContentPane().add(cursorInfoLabel);
    	
    	mainFrame.getContentPane().add(loopBotCheckBox);
    	
    	
    	mainFrame.pack();
    	mainFrame.setSize(new Dimension((PUZZLE_REPRESENTATION_BOX_SIZE*PUZZLE_X)+(PUZZLE_REPRESENTATION_BOX_SIZE*6),128+(PUZZLE_REPRESENTATION_BOX_SIZE*PUZZLE_Y+(PUZZLE_REPRESENTATION_BOX_SIZE*6))));
    	mainFrame.setLocation(0,20);
    	mainFrame.setVisible(true);
    	mainFrame.setAlwaysOnTop(true);
    	
    	//gridWindow = new GridWindow(REAL_PUZZLE_GRID_SIZE,(int)mainFrame.getLocation().getX()+(int)mainFrame.getSize().getWidth(),(int)mainFrame.getLocation().getY());
    	gridWindow = new GridWindow(REAL_PUZZLE_GRID_SIZE,400,357);

    	
    	timer = new ClockTimer(0);
    	timer.start();
    
    }
    
    private void startBotting() {
		gridWindow.hide();
    	basileanWait(200);
    	enumerateColors();
    	resetBotButton.setEnabled(true);
    	solvePuzzle=true;
    	timer.setTimer(Integer.parseInt(timerTextField.getText()));
	}
    
    
    

    public void updateMousePositionInformation() //update mouse position. Identical to constructor
    {
        pointer = MouseInfo.getPointerInfo();
        coord = pointer.getLocation();
        mouseX = coord.x;
        mouseY = coord.y;
        pixelColor = robot.getPixelColor(mouseX, mouseY);
    }
    
    BufferedImage grid;
    
    public Color getColorAtPosition(int posX,int posY){	
    	return robot.getPixelColor(posX, posY);      
    }
    
    public BufferedImage getDesktopImage(){
        Rectangle screenArea = new Rectangle(0,0,screenSizeX,screenSizeY);
    	return(robot.createScreenCapture(screenArea));
    }
    
    private void resetBot() {
		gridWindow.show();
    	puzzleColors.removeAllElements();
    	if(colorShowWindow!=null){colorShowWindow.hide();}
    	solvePuzzle=false;
    	timer.stopTimer();
	}
    
    public void updatePuzzleFrame(){
    	basileanWait(5);
    	desktopImage = getDesktopImage();	
    	gridWindow.update();
    	
    	mainStatusLabel.setText(scanLocationX+","+scanLocationY+" Detected Colors:"+puzzleColors.size()+"\n Time:"+timer.getElapsedTime());
    	
    	scanLocationX=gridWindow.getPositionX()+9+(REAL_PUZZLE_BOX_SIZE/2);
    	scanLocationY=gridWindow.getPositionY()+0+(REAL_PUZZLE_BOX_SIZE/2);
    	
    	int currentX=scanLocationX;
    	int currentY=scanLocationY;
    	
    	for(int x=0;x<PUZZLE_X;x++){
    		currentY=scanLocationY;
    		for(int y=0;y<PUZZLE_Y;y++){
    			currentY+=REAL_PUZZLE_BOX_SIZE;
    			
    			if(currentX>=screenSizeX){currentX=screenSizeX-1;}
    			if(currentY>=screenSizeY){currentY=screenSizeY-1;}
    			if(currentX<0){currentX=0;}
    			if(currentY<0){currentY=0;}
    			
    			int colorData = desktopImage.getRGB(currentX, currentY);
    	        
    	        puzzleArrayColor[y][x] = new Color(colorData);
    				
    			puzzleArrayPanels[y][x].setBackground(puzzleArrayColor[y][x]);
    			puzzleArrayPanels[y][x].repaint();
    			
        	}
    		currentX+=REAL_PUZZLE_BOX_SIZE;
    	}
    	mainPanel.repaint();
    	
    	
    	
    	if(isSolvingPuzzle()){
    		
    		//solveWithAllPosible();
    		//solveSelective();
    		//solveWithAllPosibleNonStop();
    		solveWithAllPosible2();
    		
    	}
    	else
    	{
    		updateMousePositionInformation();
    		cursorInfoLabel.setText("X:"+this.getX()+"Y:"+getY()+"Color:("+getRed()+","+getGreen()+","+getBlue()+")");

    		if(loopBotCheckBox.isSelected()){

    			basileanWait(3000);
    			setMousePosition(371,627);
    			basileanWait(1000);
    			Color colorAtPosition = robot.getPixelColor(371,627);
    			if(colorAtPosition.equals(new Color(234,51,166))){
    				System.out.println("play again");
    				this.emulateMouseClickLeft();
    			}
    			
    			setMousePosition(497,658);
    			basileanWait(1000);
    			colorAtPosition = robot.getPixelColor(497,658);
    			if(colorAtPosition.equals(new Color(99,99,99))){
    				System.out.println("continue");
    				this.emulateMouseClickLeft();
    			}
    			
    			
    			setMousePosition(379,698);
    			basileanWait(1000);
    			colorAtPosition = robot.getPixelColor(379,698);
    			if(colorAtPosition.equals(new Color(108,108,108))){
    				System.out.println("cancel");
    				this.emulateMouseClickLeft();
    			}

    			
    			setMousePosition(363,703);
    			basileanWait(1000);
    			colorAtPosition = robot.getPixelColor(363,703);
    			if(colorAtPosition.equals(new Color(91,91,91))){
    				System.out.println("no thanks");
    				this.emulateMouseClickLeft();
    			}
    			
    			
    			setMousePosition(365,667);
    			basileanWait(1000);
    			colorAtPosition = robot.getPixelColor(365,667);
    			if(colorAtPosition.equals(new Color(223,64,164))){
    				System.out.println("play now");
    				this.emulateMouseClickLeft();
    				this.startBotting();
    			}
    			
    			setMousePosition(497,658);
    			basileanWait(1000);
    			colorAtPosition = robot.getPixelColor(497,658);
    			if(colorAtPosition.equals(new Color(99,99,99))){
    				System.out.println("continue");
    				this.emulateMouseClickLeft();
    			}

    		}
    		

    	}
    }
    
    private void basileanWait(int i) {
		// TODO Auto-generated method stub
		
	}

	private void solveSelective(){
    	boolean madeMove = false;

    	if(madeMove==false){
    		for(int y=0;y<PUZZLE_Y && madeMove==false;y++){
    			for(int x=0;x<PUZZLE_X && madeMove==false;x++){
    				madeMove = doAlgorithmSet1(PUZZLE_X-1-x,PUZZLE_Y-1-y,madeMove);
    			}
    		}
    	}

    	if(madeMove==false){
    		for(int y=0;y<PUZZLE_Y && madeMove==false;y++){
    			for(int x=0;x<PUZZLE_X && madeMove==false;x++){
    				madeMove = doAlgorithmSet2(PUZZLE_X-1-x,PUZZLE_Y-1-y,madeMove);
    			}
    		}
    	}

    	if(madeMove==false){
    		for(int y=0;y<PUZZLE_Y && madeMove==false;y++){
    			for(int x=0;x<PUZZLE_X && madeMove==false;x++){
    				madeMove = doAlgorithmSet3(PUZZLE_X-1-x,PUZZLE_Y-1-y,madeMove);
    			}
    		}
    	}
    }

	private void solveWithAllPosible() {
		boolean madeMove = false;
		for(int y=0;y<PUZZLE_Y && madeMove==false;y++){
			for(int x=0;x<PUZZLE_X && madeMove==false;x++){
				madeMove = solveWithAllAlgorithms(PUZZLE_X-1-x,PUZZLE_Y-1-y);
			}
		}
	}
	
	private void solveWithAllPosible2(){
		boolean madeMove = false;
		for(int y=0;y<PUZZLE_Y && madeMove==false;y++){
			for(int x=0;x<PUZZLE_X && madeMove==false;x++){
				madeMove = solveWithAllAlgorithms2(PUZZLE_X-1-x,PUZZLE_Y-1-y);
			}
		}
	}
	
	private boolean solveWithAllAlgorithms2(int x,int y){
		boolean madeMove = false;

		madeMove = doAlgorithmSet1(x, y, madeMove);
		if(madeMove==false){madeMove = doAlgorithmSet2(x, y, madeMove);}
		if(madeMove==false){madeMove = doAlgorithmSet3(x, y, madeMove);}
		
		return madeMove;
    }
	
	
	private void solveWithAllPosibleNonStop() {
		boolean madeMove = false;
		for(int y=0;y<PUZZLE_Y;y++){
			for(int x=0;x<PUZZLE_X;x++){
				madeMove = solveWithAllAlgorithms(PUZZLE_X-1-x,PUZZLE_Y-1-y);
			}
		}
		basileanWait(200);
	}
    
    private boolean solveWithAllAlgorithms(int x,int y){
		boolean madeMove = false;
		
		
		/*    [O][x][O]
		 * [O]   [O]   [O]
		 * [x][O][0][O][x]
		 * [O]   [O]   [O]
		 *    [O][x][O]
		 */
		madeMove = doAlgorithmSet1(x, y, madeMove);
		
		/*       [O]
		 *    [O][x][O]
		 * [O][x][0][x][O]
		 *    [O][x][O]
		 *       [O]
		 */
		madeMove = doAlgorithmSet2(x, y, madeMove);
		

		/*          [O]
		 *          [x]
		 *          [O]
		 * [O][x][O][0][O][x][O]
		 *          [O]
		 *          [x]
		 *          [O]   
		 */
		madeMove = doAlgorithmSet3(x, y, madeMove);
		
		return madeMove;
    }

	private boolean doAlgorithmSet3(int x, int y, boolean madeMove) {
		if(madeMove==false){madeMove = solveAlgorithm9(x,y);}
		if(madeMove==false){madeMove = solveAlgorithm10(x,y);}
		if(madeMove==false){madeMove = solveAlgorithm11(x,y);}
		if(madeMove==false){madeMove = solveAlgorithm12(x,y);}
		return madeMove;
	}

	private boolean doAlgorithmSet2(int x, int y, boolean madeMove) {
		if(madeMove==false){madeMove = solveAlgorithm5(x,y);}
		if(madeMove==false){madeMove = solveAlgorithm6(x,y);}
		if(madeMove==false){madeMove = solveAlgorithm7(x,y);}
		if(madeMove==false){madeMove = solveAlgorithm8(x,y);}
		return madeMove;
	}

	private boolean doAlgorithmSet1(int x, int y, boolean madeMove) {
		if(madeMove==false){madeMove = solveAlgorithm1(x,y);}
		if(madeMove==false){madeMove = solveAlgorithm2(x,y);}
		if(madeMove==false){madeMove = solveAlgorithm3(x,y);}
		if(madeMove==false){madeMove = solveAlgorithm4(x,y);}
		return madeMove;
	}
    
    private boolean solveAlgorithm8(int x,int y){

    	Color slotColor = puzzleArrayColor[y][x];

    	int nextSlotX = x;
    	int nextSlotY = y+1;
    	
    	if(nextSlotY<PUZZLE_Y){
    		Color nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    		if(!(ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    			nextSlotX = x;
    	    	nextSlotY = y+2;
    	    	if(nextSlotY<PUZZLE_Y){
    	    		nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    	    		if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    	    			
    	    			nextSlotX = x+1;
    	    	    	nextSlotY = y+1;
    	    	    	if(nextSlotX<PUZZLE_X){
    	    	    		nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    	    	    		if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    	    	    			//Move this block left
    	    	    			dragMouse(scanLocationX+(REAL_PUZZLE_BOX_SIZE*(x)),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(y+1+1)),
    									scanLocationX+(REAL_PUZZLE_BOX_SIZE*nextSlotX),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(nextSlotY+1)));
    							return true;
    	    	    		}
    	    	    	}
    	    	    	
    	    	    	nextSlotX = x-1;
    	    	    	nextSlotY = y+1;
    	    	    	if(nextSlotX>=0){
    	    	    		nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    	    	    		if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    	    	    			//Move this block right
    	    	    			dragMouse(scanLocationX+(REAL_PUZZLE_BOX_SIZE*(x)),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(y+1+1)),
    									scanLocationX+(REAL_PUZZLE_BOX_SIZE*nextSlotX),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(nextSlotY+1)));
    							return true;
    	    	    		}
    	    	    	}
    	    	    	
    	    		}
    	    	}
    		}
    	}
    	return false;
    }
    
    private boolean solveAlgorithm7(int x,int y){

    	Color slotColor = puzzleArrayColor[y][x];

    	int nextSlotX = x;
    	int nextSlotY = y-1;
    	
    	if(nextSlotY>=0){
    		Color nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    		if(!(ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    			nextSlotX = x;
    	    	nextSlotY = y-2;
    	    	if(nextSlotY>=0){
    	    		nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    	    		if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    	    			
    	    			nextSlotX = x+1;
    	    	    	nextSlotY = y-1;
    	    	    	if(nextSlotX<PUZZLE_X){
    	    	    		nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    	    	    		if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    	    	    			//Move this block left
    	    	    			dragMouse(scanLocationX+(REAL_PUZZLE_BOX_SIZE*(x)),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(y+1-1)),
    									scanLocationX+(REAL_PUZZLE_BOX_SIZE*nextSlotX),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(nextSlotY+1)));
    							return true;
    	    	    		}
    	    	    	}
    	    	    	
    	    	    	nextSlotX = x-1;
    	    	    	nextSlotY = y-1;
    	    	    	if(nextSlotX>=0){
    	    	    		nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    	    	    		if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    	    	    			//Move this block right
    	    	    			dragMouse(scanLocationX+(REAL_PUZZLE_BOX_SIZE*(x)),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(y+1-1)),
    									scanLocationX+(REAL_PUZZLE_BOX_SIZE*nextSlotX),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(nextSlotY+1)));
    							return true;
    	    	    		}
    	    	    	}
    	    	    	
    	    		}
    	    	}
    		}
    	}
    	return false;
    }
    
    
    private boolean solveAlgorithm6(int x,int y){

    	Color slotColor = puzzleArrayColor[y][x];

    	int nextSlotX = x-1;
    	int nextSlotY = y;
    	
    	if(nextSlotX>=0){
    		Color nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    		if(!(ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    			nextSlotX = x-2;
    	    	nextSlotY = y;
    	    	if(nextSlotX>=0){
    	    		nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    	    		if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    	    			nextSlotX = x-1;
    	    	    	nextSlotY = y+1;
    	    	    	if(nextSlotY<PUZZLE_Y){
    	    	    		nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    	    	    		if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    	    	    			//Move this block down
    	    	    			dragMouse(scanLocationX+(REAL_PUZZLE_BOX_SIZE*(x-1)),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(y+1)),
    									scanLocationX+(REAL_PUZZLE_BOX_SIZE*nextSlotX),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(nextSlotY+1)));
    							return true;
    	    	    		}
    	    	    	}
    	    	    	
    	    	    	nextSlotX = x-1;
    	    	    	nextSlotY = y-1;
    	    	    	if(nextSlotY>=0){
    	    	    		nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    	    	    		if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    	    	    			//Move this block up
    	    	    			dragMouse(scanLocationX+(REAL_PUZZLE_BOX_SIZE*(x-1)),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(y+1)),
    									scanLocationX+(REAL_PUZZLE_BOX_SIZE*nextSlotX),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(nextSlotY+1)));
    							return true;
    	    	    		}
    	    	    	}
    	    	    	
    	    		}
    	    	}
    		}
    	}
    	return false;
    }
    
    private boolean solveAlgorithm5(int x,int y){

    	Color slotColor = puzzleArrayColor[y][x];

    	int nextSlotX = x+1;
    	int nextSlotY = y;
    	
    	if(nextSlotX<PUZZLE_X){
    		Color nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    		if(!(ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    			nextSlotX = x+2;
    	    	nextSlotY = y;
    	    	if(nextSlotX<PUZZLE_X){
    	    		nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    	    		if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    	    			nextSlotX = x+1;
    	    	    	nextSlotY = y+1;
    	    	    	if(nextSlotY<PUZZLE_Y){
    	    	    		nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    	    	    		if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    	    	    			//Move this block down
    	    	    			dragMouse(scanLocationX+(REAL_PUZZLE_BOX_SIZE*(x+1)),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(y+1)),
    									scanLocationX+(REAL_PUZZLE_BOX_SIZE*nextSlotX),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(nextSlotY+1)));
    							return true;
    	    	    		}
    	    	    	}
    	    	    	
    	    	    	nextSlotX = x+1;
    	    	    	nextSlotY = y-1;
    	    	    	if(nextSlotY>=0){
    	    	    		nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    	    	    		if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    	    	    			//Move this block up
    	    	    			dragMouse(scanLocationX+(REAL_PUZZLE_BOX_SIZE*(x+1)),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(y+1)),
    									scanLocationX+(REAL_PUZZLE_BOX_SIZE*nextSlotX),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(nextSlotY+1)));
    							return true;
    	    	    		}
    	    	    	}
    	    	    	
    	    		}
    	    	}
    		}
    	}
    	return false;
    }
    

    private boolean solveAlgorithm4(int x,int y){

    	Color slotColor = puzzleArrayColor[y][x];

    	int nextSlotX = x;
    	int nextSlotY = y-1;

    	if(nextSlotY>=0){

    		Color nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    		if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){

    			nextSlotX = x;
    			nextSlotY = y-2;

    			if(nextSlotY>=0){

    				nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    				if(!(ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    					nextSlotX = x-1;
    					nextSlotY = y-2;
    					if(nextSlotX>=0){

    						nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    						if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    							//TODO move this block left
    							dragMouse(scanLocationX+(REAL_PUZZLE_BOX_SIZE*(x)),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(y+1-2)),
    									scanLocationX+(REAL_PUZZLE_BOX_SIZE*nextSlotX),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(nextSlotY+1)));
    							return true;
    						}
    					}

    					nextSlotX = x+1;
    					nextSlotY = y-2;
    					if(nextSlotX<PUZZLE_X){
    						nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    						if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){

    							//TODO move this block right
    	    					dragMouse(scanLocationX+(REAL_PUZZLE_BOX_SIZE*(x)),
    	    							scanLocationY+(REAL_PUZZLE_BOX_SIZE*(y+1-2)),
    	    							scanLocationX+(REAL_PUZZLE_BOX_SIZE*nextSlotX),
    	    	    					scanLocationY+(REAL_PUZZLE_BOX_SIZE*(nextSlotY+1)));
    	    					return true;
    	    				}
    	    			}
    				}
    	    	}
    		}
    	}
    	return false;
    }
   
    private boolean solveAlgorithm3(int x,int y){
    	
    	Color slotColor = puzzleArrayColor[y][x];
    	
    	int nextSlotX = x;
    	int nextSlotY = y+1;
    	
    	if(nextSlotY<PUZZLE_Y){
    		
    		Color nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    		if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    			
    			nextSlotX = x;
    			nextSlotY = y+2;
    			
    			if(nextSlotY<PUZZLE_Y){
    				
    				nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    				if(!(ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    					nextSlotX = x-1;
    	    			nextSlotY = y+2;
    	    			if(nextSlotX>=0){
    	    				
    	    				nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    	    				if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    	    					//TODO move this block left
    	    					dragMouse(scanLocationX+(REAL_PUZZLE_BOX_SIZE*(x)),
    	    							scanLocationY+(REAL_PUZZLE_BOX_SIZE*(y+1+2)),
    	    							scanLocationX+(REAL_PUZZLE_BOX_SIZE*nextSlotX),
    	    	    					scanLocationY+(REAL_PUZZLE_BOX_SIZE*(nextSlotY+1)));
    	    					return true;
    	    				}
    	    			}
    	    			
    	    			nextSlotX = x+1;
    	    			nextSlotY = y+2;
    	    			if(nextSlotX<PUZZLE_X){
    	    				nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    	    				if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    	    					
    	    					//TODO move this block right
    	    					dragMouse(scanLocationX+(REAL_PUZZLE_BOX_SIZE*(x)),
    	    							scanLocationY+(REAL_PUZZLE_BOX_SIZE*(y+1+2)),
    	    							scanLocationX+(REAL_PUZZLE_BOX_SIZE*nextSlotX),
    	    	    					scanLocationY+(REAL_PUZZLE_BOX_SIZE*(nextSlotY+1)));
    	    					return true;
    	    				}
    	    			}
    				}
    	    	}
    		}
    	}
    	return false;
    }
    
    
    private boolean solveAlgorithm2(int x,int y){
    	//basileanWait(200);
    	//setMousePosition(scanLocationX+(REAL_PUZZLE_BOX_SIZE*x),scanLocationY+(REAL_PUZZLE_BOX_SIZE*(y+1)));
    	Color slotColor = puzzleArrayColor[y][x];
    	
    	int nextSlotX = x+1;
    	int nextSlotY = y;
    	
    	if(nextSlotX<PUZZLE_X){
    		Color nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    		if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    			
    			nextSlotX = x+2;
    			nextSlotY = y;
    			
    			if(nextSlotX<PUZZLE_X){
    				nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    				if(!(ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    					nextSlotX = x+2;
    	    			nextSlotY = y-1;
    	    			if(nextSlotY>=0){
    	    				nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    	    				if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    	    					//TODO move this block up
    	    					dragMouse(scanLocationX+(REAL_PUZZLE_BOX_SIZE*(x+2)),
    	    							scanLocationY+(REAL_PUZZLE_BOX_SIZE*(y+1)),
    	    							scanLocationX+(REAL_PUZZLE_BOX_SIZE*nextSlotX),
    	    	    					scanLocationY+(REAL_PUZZLE_BOX_SIZE*(nextSlotY+1)));
    	    					return true;
    	    				}
    	    			}
    	    			
    	    			nextSlotX = x+2;
    	    			nextSlotY = y+1;
    	    			if(nextSlotY<PUZZLE_X){
    	    				nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    	    				if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    	    					dragMouse(scanLocationX+(REAL_PUZZLE_BOX_SIZE*(x+2)),
    	    							scanLocationY+(REAL_PUZZLE_BOX_SIZE*(y+1)),
    	    							scanLocationX+(REAL_PUZZLE_BOX_SIZE*nextSlotX),
    	    	    					scanLocationY+(REAL_PUZZLE_BOX_SIZE*(nextSlotY+1)));
    	    					return true;
    	    				}
    	    			}
    				}
    	    	}
    		}
    	}
    	return false;
    }
    
    private boolean solveAlgorithm1(int x,int y){
    	//basileanWait(200);
    	//setMousePosition(scanLocationX+(REAL_PUZZLE_BOX_SIZE*x),scanLocationY+(REAL_PUZZLE_BOX_SIZE*(y+1)));
    	Color slotColor = puzzleArrayColor[y][x];
    	
    	int nextSlotX = x-1;
    	int nextSlotY = y;
    	
    	if(nextSlotX>=0){
    		Color nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    		if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    			
    			nextSlotX = x-2;
    			nextSlotY = y;
    			
    			if(nextSlotX>=0){
    				nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    				if(!(ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    					nextSlotX = x-2;
    	    			nextSlotY = y-1;
    	    			if(nextSlotY>=0){
    	    				nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    	    				if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    	    					//TODO move this block up
    	    					dragMouse(scanLocationX+(REAL_PUZZLE_BOX_SIZE*(x-2)),
    	    							scanLocationY+(REAL_PUZZLE_BOX_SIZE*(y+1)),
    	    							scanLocationX+(REAL_PUZZLE_BOX_SIZE*nextSlotX),
    	    	    					scanLocationY+(REAL_PUZZLE_BOX_SIZE*(nextSlotY+1)));
    	    					return true;
    	    				}
    	    			}
    	    			
    	    			nextSlotX = x-2;
    	    			nextSlotY = y+1;
    	    			if(nextSlotY<PUZZLE_X){
    	    				nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    	    				if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    	    					dragMouse(scanLocationX+(REAL_PUZZLE_BOX_SIZE*(x-2)),
    	    							scanLocationY+(REAL_PUZZLE_BOX_SIZE*(y+1)),
    	    							scanLocationX+(REAL_PUZZLE_BOX_SIZE*nextSlotX),
    	    	    					scanLocationY+(REAL_PUZZLE_BOX_SIZE*(nextSlotY+1)));
    	    					return true;
    	    				}
    	    			}
    				}
    	    	}
    		}
    	}
    	return false;
    }
    
    private boolean solveAlgorithm12(int x,int y){
    	Color slotColor = puzzleArrayColor[y][x];
    	
    	int nextSlotX = x;
    	int nextSlotY = y-1;
    	
    	if(nextSlotY>=0){
    		Color nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    		if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    			
    			nextSlotX = x;
    			nextSlotY = y-2;
    			
    			if(nextSlotY>=0){
    				nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    				if(!(ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    					nextSlotX = x;
    	    			nextSlotY = y-3;
    	    			if(nextSlotY>=0){
    	    				nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    	    				if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    	    					//TODO move this block to the right
    	    					dragMouse(scanLocationX+(REAL_PUZZLE_BOX_SIZE*(x)),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(y+1-2)),
    									scanLocationX+(REAL_PUZZLE_BOX_SIZE*nextSlotX),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(nextSlotY+1)));
    	    					return true;
    	    				}
    	    			}
    				}
    	    	}
    		}
    	}
    	return false;
    }
    
    private boolean solveAlgorithm11(int x,int y){
    	Color slotColor = puzzleArrayColor[y][x];
    	
    	int nextSlotX = x;
    	int nextSlotY = y+1;
    	
    	if(nextSlotY<PUZZLE_Y){
    		Color nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    		if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    			
    			nextSlotX = x;
    			nextSlotY = y+2;
    			
    			if(nextSlotY<PUZZLE_Y){
    				nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    				if(!(ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    					nextSlotX = x;
    	    			nextSlotY = y+3;
    	    			if(nextSlotY<PUZZLE_Y){
    	    				nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    	    				if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    	    					//TODO move this block to the right
    	    					dragMouse(scanLocationX+(REAL_PUZZLE_BOX_SIZE*(x)),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(y+1+2)),
    									scanLocationX+(REAL_PUZZLE_BOX_SIZE*nextSlotX),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(nextSlotY+1)));
    	    					return true;
    	    				}
    	    			}
    				}
    	    	}
    		}
    	}
    	return false;
    }
    
    private boolean solveAlgorithm10(int x,int y){
    	Color slotColor = puzzleArrayColor[y][x];
    	
    	int nextSlotX = x+1;
    	int nextSlotY = y;
    	
    	if(nextSlotX<PUZZLE_X){
    		Color nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    		if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    			
    			nextSlotX = x+2;
    			nextSlotY = y;
    			
    			if(nextSlotX<PUZZLE_X){
    				nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    				if(!(ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    					nextSlotX = x+3;
    	    			nextSlotY = y;
    	    			if(nextSlotX<PUZZLE_X){
    	    				nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    	    				if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    	    					//TODO move this block to the right
    	    					dragMouse(scanLocationX+(REAL_PUZZLE_BOX_SIZE*(x+2)),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(y+1)),
    									scanLocationX+(REAL_PUZZLE_BOX_SIZE*nextSlotX),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(nextSlotY+1)));
    	    					return true;
    	    				}
    	    			}
    				}
    	    	}
    		}
    	}
    	return false;
    }
    
    private boolean solveAlgorithm9(int x,int y){
    	Color slotColor = puzzleArrayColor[y][x];
    	
    	int nextSlotX = x-1;
    	int nextSlotY = y;
    	
    	if(nextSlotX>=0){
    		Color nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    		if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    			
    			nextSlotX = x-2;
    			nextSlotY = y;
    			
    			if(nextSlotX>=0){
    				nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    				if(!(ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    					nextSlotX = x-3;
    	    			nextSlotY = y;
    	    			if(nextSlotX>=0){
    	    				nextColor = puzzleArrayColor[nextSlotY][nextSlotX];
    	    				if((ColorExtended.colorsAreSimilarHSBHardCoded(slotColor,nextColor))){
    	    					dragMouse(scanLocationX+(REAL_PUZZLE_BOX_SIZE*(x-2)),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(y+1)),
    									scanLocationX+(REAL_PUZZLE_BOX_SIZE*nextSlotX),
    									scanLocationY+(REAL_PUZZLE_BOX_SIZE*(nextSlotY+1)));
    	    					return true;
    	    					//TODO move this block to the left
    	    				}
    	    			}
    				}
    	    	}
    		}
    	}
    	return false;
    }
    
    
    
    public void dragMouse(int x1,int y1, int x2, int y2){
    	setMousePosition(x1,y1);
    	pressMouseClickLeft();
    	basileanWait(10);
    	setMousePosition(x2,y2);
    	releaseMouseClickLeft();
    }
    
    
    public void enumerateColors(){
    	for(int x=0;x<PUZZLE_X;x++){
    		for(int y=0;y<PUZZLE_Y;y++){
    			Color thisColor = puzzleArrayColor[x][y];
    			if(!puzzleColors.contains(thisColor)){puzzleColors.add(thisColor);}
    		}
    	}
		colorShowWindow = new ColorShowWindow(
				(int) mainFrame.getLocation().getX(), 
				(int) mainFrame.getLocation().getY() + (int) mainFrame.getSize().getHeight(),
				puzzleColors);
	}
    
    public void setMousePosition(int x,int y){
    	robot.mouseMove(x,y);
    }
    
    public void emulateMouseClickLeft(){
    	robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }
    
    public void emulateMouseClickRight(){
    	robot.mousePress(InputEvent.BUTTON3_MASK);
        robot.mouseRelease(InputEvent.BUTTON3_MASK);
    }
    
    public void emulateMouseClickMiddle(){
    	robot.mousePress(InputEvent.BUTTON2_MASK);
        robot.mouseRelease(InputEvent.BUTTON2_MASK);
    }

    public void releaseMouseClickLeft(){
    	robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    public void releaseMouseClickMiddle(){
    	robot.mouseRelease(InputEvent.BUTTON2_MASK);
    }

    public void releaseMouseClickRight(){
    	robot.mouseRelease(InputEvent.BUTTON3_MASK);
    }
    
    public void pressMouseClickLeft(){
    	robot.mousePress(InputEvent.BUTTON1_MASK);
    }

    public void pressMouseClickMiddle(){
    	robot.mousePress(InputEvent.BUTTON2_MASK);
    }

    public void pressMouseClickRight(){
    	robot.mousePress(InputEvent.BUTTON3_MASK);
    }

    //all of the following get methods return an int from 0-255 (besides getColor)
    public int getRed()
    {
        return pixelColor.getRed();
    }

    public int getGreen()
    {
        return pixelColor.getGreen();
    }

    public int getBlue()
    {
        return pixelColor.getBlue();
    }

    public Color getColor()
    {
        return pixelColor;
    }

    public int getX() //X value of pointer
    {
        return mouseX;
    }

    public int getY() //Y value of pointer
    {
        return mouseY;
    }

	public boolean isSolvingPuzzle() {
		return solvePuzzle;
	}

	public void setSolvePuzzle(boolean solvePuzzle) {
		this.solvePuzzle = solvePuzzle;
	}   

}