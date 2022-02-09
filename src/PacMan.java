/*
 * Pac Man Game: GUI that recreates the classic game Pacman
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.util.*;


public class PacMan extends JFrame implements KeyListener {

	private enum STATUS {PACMAN,EMPTY,PELLET,BLOCKED};
	private enum DIR {LEFT,RIGHT,UP,DOWN};

	private final int NUM_ROWS = 20;
	private final int NUM_COLS = 18;

	private  BufferedImage[] PAC_IMAGE;			//left, right, up, down
	private  BufferedImage PELLET_IMAGE=null;;

	private PicPanel[][] allPanels;

	private int pacRow =1;						//location of pacman
	private int pacCol =1;

	public PacMan(){

		PAC_IMAGE = new BufferedImage[4];

		try {

			PAC_IMAGE[0] = ImageIO.read(new File("pac_left.jpg"));
			PAC_IMAGE[1] = ImageIO.read(new File("pac_right.jpg"));
			PAC_IMAGE[2] = ImageIO.read(new File("pac_up.jpg"));
			PAC_IMAGE[3] = ImageIO.read(new File("pac_down.jpg"));
			PELLET_IMAGE = ImageIO.read(new File("pellet.png"));

		} catch (IOException ioe) {
			System.out.println("Could not read pacman pics");
			System.exit(0);
		}

		setSize(600,800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Pacman");
		setLayout(new GridLayout(NUM_ROWS,NUM_COLS));
		allPanels = new PicPanel[NUM_ROWS][NUM_COLS];

		this.addKeyListener(this);

		//read maze text file and build the grid
		Scanner fileIn = null;
		try {
			fileIn = new Scanner(new File("maze.txt"));
		}catch(FileNotFoundException e){
			System.out.println("Maze file not found");
			System.exit(-1);
		}

		int row = 0;
		while(fileIn.hasNextLine()) {
			String nextLine = fileIn.nextLine();
			String[] nextLineChars = nextLine.split("");

			for(int col=0; col<nextLineChars.length; col++) {
				allPanels[row][col] = new PicPanel(nextLineChars[col]);
				add(allPanels[row][col]);
			}

			row++;
		}

		allPanels[pacRow][pacCol].addPacman(DIR.RIGHT);

		setVisible(true);
	}

	class PicPanel extends JPanel{

		private BufferedImage image;		
		private STATUS panelStat;

		//takes in a single val from the file (either "x" or "o")
		public PicPanel(String val){
			if(val.equals("o")) {
				panelStat = STATUS.PELLET;
				image = PELLET_IMAGE;
			}
			else
				panelStat = STATUS.BLOCKED;
		}

		public void clearPanel() {
			image = null;
			panelStat = STATUS.EMPTY;
			repaint();
		}

		public void addPacman(DIR direction) {
			image = PAC_IMAGE[direction.ordinal()];
			panelStat = STATUS.PACMAN;
			repaint();
		}

		//this will draw the image
		public void paintComponent(Graphics g){
			super.paintComponent(g);

			if(panelStat == STATUS.EMPTY)
				setBackground(Color.black);
			else if(panelStat == STATUS.BLOCKED)
				setBackground(Color.blue);
			else
				g.drawImage(image,0,0,this);			
		}
	}

	//reacts to when arrow keys are pressed to move pacman
	public void keyPressed(KeyEvent arg0) {

		int keyVal = arg0.getKeyCode();

		//right key
		if(keyVal == KeyEvent.VK_RIGHT) {
			PicPanel toRight = allPanels[pacRow][pacCol+1];

			//if panel to right is not blocked, move pacman there
			if(toRight.panelStat != STATUS.BLOCKED) {
				updateBoard(toRight, DIR.RIGHT);
				pacCol++;
			}
		}

		//left key
		else if(keyVal == KeyEvent.VK_LEFT) {
			PicPanel toLeft = allPanels[pacRow][pacCol-1];

			//if panel to left is not blocked, move pacman there
			if(toLeft.panelStat != STATUS.BLOCKED) {
				updateBoard(toLeft, DIR.LEFT);
				pacCol--;
			}
		}

		//up key
		else if(keyVal == KeyEvent.VK_UP) {
			PicPanel toUp = allPanels[pacRow-1][pacCol];

			//if panel to up is not blocked, move pacman there
			if(toUp.panelStat != STATUS.BLOCKED) {
				updateBoard(toUp, DIR.UP);
				pacRow--;
			}
		}

		//down key
		else if(keyVal == KeyEvent.VK_DOWN) {
			PicPanel toDown = allPanels[pacRow+1][pacCol];

			//if panel to down is not blocked, move pacman there
			if(toDown.panelStat != STATUS.BLOCKED) {
				updateBoard(toDown, DIR.DOWN);
				pacRow++;
			}
		}

	}

	//updates board by clearing and adding pacman to correct panels if move is valid
	private void updateBoard(PicPanel moveTo, DIR direction) {
		PicPanel current = allPanels[pacRow][pacCol];

		current.clearPanel();
		moveTo.addPacman(direction);

		current.panelStat = STATUS.EMPTY;
		moveTo.panelStat = STATUS.PACMAN;
	}

	public void keyReleased(KeyEvent arg0) {


	}

	public void keyTyped(KeyEvent arg0) {


	}	

	public static void main(String[] args){
		new PacMan();
	}
}
