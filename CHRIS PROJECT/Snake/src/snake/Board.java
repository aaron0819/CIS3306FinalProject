package snake;

/**@author Chris Hume
 *  Thanks to zetcode.com for the multiple 2D game tutorials. Learning to create and understand this is thanks to them.
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import util.MyTimer;

public class Board extends JPanel implements ActionListener {

	private final int B_WIDTH = 500;
	private final int B_HEIGHT = 500;
	private final int DOT_SIZE = 10;
	private final int ALL_DOTS = 900;
	private final int RAND_POS = 29;
	private int DELAY = 80;
	private ArrayList missiles;

	private final int x[] = new int[ALL_DOTS];
	private final int y[] = new int[ALL_DOTS];

	private int dots;
	private int dotsCount;
	private int apple_x;
	private int apple_y;

	private boolean leftDirection = false;
	private boolean rightDirection = true;
	private boolean upDirection = false;
	private boolean downDirection = false;
	private boolean inGame = true;
	private boolean inMenu = true;

	private Timer timer;
	private Image ball;
	private Image apple;
	private Image head;
	private Image missile;

	public Board() {

		addKeyListener(new TAdapter());
		setBackground(Color.black);
		setFocusable(true);

		setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));

		loadImages();
		initGame();
		MyTimer.startTime();
	}

	private void loadImages() {

		ImageIcon iid = new ImageIcon("snake/dot.png");
		ball = iid.getImage();

		ImageIcon iia = new ImageIcon("snake/apple.png");
		apple = iia.getImage();

		ImageIcon iih = new ImageIcon("snake/head.png");
		head = iih.getImage();

		ImageIcon iim = new ImageIcon("snake/missile.png");
		missile = iim.getImage();
	}

	/**
	 * initGame() creates a snake with 3 initial parts. The game starts by
	 * putting the 3 length snake at an initial position with x[z] = 50 - z *
	 * 10; y[z] = 50;
	 * 
	 * Then, one apple is randomly placed on the board with locateApple(). A
	 * timer is started with a corresponding delay, which dictates the speed of
	 * the game.
	 */
	private void initGame() {

		dots = 3;

		for (int z = 0; z < dots; z++) {
			x[z] = 50 - z * 10;
			y[z] = 50;
		}

		locateApple();

		timer = new Timer(DELAY, this);
		timer.start();
	}

	/**
	 * Calls doDrawing(), which paints everything we're managing onto the board.
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		doDrawing(g);
	}

	/**
	 * 
	 * @param g
	 *            Graphics object inMenu: Program will start inMenu, and the
	 *            menu will be drawn. Stays in menu until told otherwise by a
	 *            keystroke (1, 2, 3, r for varying reasons).
	 * 
	 * 
	 *            inGame: Draws the single head object of the snake, as well as
	 *            all parts of its tail, while the counter is less than "dots";
	 *            so 2 tail/ball images are initially drawn.
	 * 
	 *            else: If not inMenu, and game is ended, the gameOver screen
	 *            will be drawn, which tells you how many apples you ate, and
	 *            how great of a job you did! "Nice."
	 */
	private void doDrawing(Graphics g) {

		if (inMenu) {
			drawMenu(g);

		} else if (inGame) {

			g.drawImage(apple, apple_x, apple_y, this);

			for (int z = 0; z < dots; z++) {
				if (z == 0) {
					g.drawImage(head, x[z], y[z], this);
				} else {
					g.drawImage(ball, x[z], y[z], this);
				}
			}

			Toolkit.getDefaultToolkit().sync();
			g.dispose();

		} else {

			gameOver(g);
		}
	}

	/**
	 * 
	 * @param g
	 *            Graphics object Draws a starting screen, giving you three
	 *            options, each varying the game's speed.
	 * 
	 */
	public void drawMenu(Graphics g) {
		String msg = "Easy (Press 1)";
		String msg2 = "Medium (Press 2)";
		String msg3 = "Hard (Press 3)";

		Font small = new Font("Helvetica", Font.BOLD, 14);
		FontMetrics metr = getFontMetrics(small);
		g.setColor(Color.white);
		g.setFont(small);

		g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2,
				B_HEIGHT / 2 - 60);
		g.drawString(msg2, (B_WIDTH - metr.stringWidth(msg2)) / 2,
				B_HEIGHT / 2 - 10);
		g.drawString(msg3, (B_WIDTH - metr.stringWidth(msg3)) / 2,
				B_HEIGHT / 2 + 40);
	}

	/**
	 * 
	 * @param g
	 *            Graphics object Draws a game over screen. Displayed when you
	 *            collide with yourself, or any side of the screen.
	 */
	private void gameOver(Graphics g) {
		String msg = "Game Over!";
		String msg2 = "You ate";
		String msg3 = "apples.";
		String msg4 = "Nice.";
		String appC = String.valueOf(dotsCount);
		String msg5 = msg2 + " " + appC + " " + msg3;

		Font small = new Font("Helvetica", Font.BOLD, 14);
		FontMetrics metr = getFontMetrics(small);

		g.setColor(Color.white);
		g.setFont(small);
		g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2,
				B_HEIGHT / 2 - 50);
		// g.drawString(msg2, (B_WIDTH - metr.stringWidth(msg2)) / 2 - 35,
		// B_HEIGHT / 2);
		g.drawString(msg5, (B_WIDTH - metr.stringWidth(msg5)) / 2, B_HEIGHT / 2);
		// g.drawString(msg3, (B_WIDTH - metr.stringWidth(msg3)) / 2 + 34,
		// B_HEIGHT / 2);
		g.drawString(msg4, (B_WIDTH - metr.stringWidth(msg4)) / 2 + 32,
				B_HEIGHT / 2 + 50);
	}

	/**
	 * If the snake comes upon the same spot as an apple, the snake will grow by
	 * one "ball", one will be added to the count of how many apples you've
	 * gotten, and the apple will be redrawn somewhere else.
	 */
	private void checkApple() {

		if ((x[0] == apple_x) && (y[0] == apple_y)) {

			dots++;
			dotsCount++;
			locateApple();
		}
	}

	/**
	 * x[z] = x[(z - 1)]; y[z] = y[(z - 1)]; Still unsure of exact knowledge
	 * behind this codes' function of it moving all parts of the snake together.
	 * 
	 * The directions: Makes the head of the snake move one spot based on the
	 * direction input. Tail of snake accompanies based on code just above.^^^
	 */
	private void move() {

		for (int z = dots; z > 0; z--) {
			x[z] = x[(z - 1)];
			y[z] = y[(z - 1)];
		}

		if (leftDirection) {
			x[0] -= DOT_SIZE;
		}

		if (rightDirection) {
			x[0] += DOT_SIZE;
		}

		if (upDirection) {
			y[0] -= DOT_SIZE;
		}

		if (downDirection) {
			y[0] += DOT_SIZE;
		}
	}

	/**
	 * If the snake collides with any side of the board, or any part of itself,
	 * the game ends.
	 */
	private void checkCollision() {

		for (int z = dots; z > 0; z--) {

			if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
				inGame = false;
			}
		}

		if (y[0] > B_HEIGHT) {
			inGame = false;
		}

		if (y[0] < 0) {
			inGame = false;
		}

		if (x[0] > B_WIDTH) {
			inGame = false;
		}

		if (x[0] < 0) {
			inGame = false;
		}
	}

	/**
	 * Randomly positions an apple on the board, by randomizing its x and y
	 * values.
	 */
	private void locateApple() {

		int r = (int) (Math.random() * RAND_POS);
		apple_x = ((r * DOT_SIZE));

		r = (int) (Math.random() * RAND_POS);
		apple_y = ((r * DOT_SIZE));
	}

	@Override
	/**
	 * Constantly runs, and does something each time an event occurs (1-3, r, and arrow keys).
	 */
	public void actionPerformed(ActionEvent e) {

		if (inGame) {

			checkApple();
			checkCollision();
			move();
		}

		repaint();
	}

	/**
	 * 
	 * @param i
	 *            Passed in delay int
	 * 
	 *            Changes speed of game by changing the DELAY value in the game
	 *            menu. Currently not functional.
	 */
	void myDelay(int i) {
		DELAY = i;
	}

	/**
	 * 
	 * "key" grabs the input keystroke from the user, and does the corresponding
	 * action.
	 *
	 */
	private class TAdapter extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {

			int key = e.getKeyCode();

			if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
				leftDirection = true;
				upDirection = false;
				downDirection = false;
			}

			if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
				rightDirection = true;
				upDirection = false;
				downDirection = false;
			}

			if ((key == KeyEvent.VK_UP) && (!downDirection)) {
				upDirection = true;
				rightDirection = false;
				leftDirection = false;
			}

			if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
				downDirection = true;
				rightDirection = false;
				leftDirection = false;
			}

			if (key == KeyEvent.VK_1) {
				myDelay(400);
				// System.out.println(DELAY);
				if (inMenu) {
					inMenu = false;
					initGame();
					repaint();
				}

			}

			if (key == KeyEvent.VK_2) {
				myDelay(135);
				// System.out.println(DELAY);
				if (inMenu) {
					inMenu = false;
					initGame();
					repaint();
				}
			}

			if (key == KeyEvent.VK_3) {
				myDelay(110);
				// System.out.println(DELAY);
				if (inMenu) {
					inMenu = false;
					initGame();
					repaint();
				}
			}

			if (key == KeyEvent.VK_R) {
				initGame();
				inMenu = true;
			}
		}
	}
}
