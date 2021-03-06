package snake;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;
//HI

import utilityClasses.*;

public class SnakePanel extends JPanel implements ActionListener, KeyListener {

	private boolean startGame = true;
	private boolean playing = false;
	private boolean paused = false;
	private boolean endGame = false;
	private boolean nameEnter = false;
	private boolean highScores = false;
	private boolean autoPlay = false;

	public enum Direction {

		up, down, left, right;

	}

	private ScoreInfo scores = new ScoreInfo("snake");

	private String pName = "";
	private Character letter;

	private ArrayList<Point> snakeBody = new ArrayList<Point>();
	private ArrayList<Color> snakeColor = new ArrayList<Color>();
	private Color[] Colors = { Color.CYAN, Color.RED, Color.GREEN,
			Color.YELLOW, Color.ORANGE, Color.WHITE };
	private int bodySize = 10;
	private Point head = new Point(250, 250);
	private int numOfFruits = 4;

	// private int fruitX = 300;
	// private int fruitY = 200;
	private ArrayList<Integer> fruitX = new ArrayList<Integer>();
	private ArrayList<Integer> fruitY = new ArrayList<Integer>();

	// private Color fruitColor = Color.WHITE;
	private ArrayList<Color> fruitColor = new ArrayList<Color>();

	private int deltaX = 0;
	private int deltaY = -bodySize;

	private final Point LEFT = new Point(-bodySize, 0);
	private final Point RIGHT = new Point(bodySize, 0);
	private final Point UP = new Point(0, -bodySize);
	private final Point DOWN = new Point(0, bodySize);
	private Point direction = new Point(deltaX, deltaY);

	private Point nextHead = new Point(head.x + deltaX, head.y + deltaY);

	private int prevLoseKey = KeyEvent.VK_DOWN;

	private int upKey = KeyEvent.VK_UP;
	private int downKey = KeyEvent.VK_DOWN;
	private int leftKey = KeyEvent.VK_LEFT;
	private int rightKey = KeyEvent.VK_RIGHT;

	private int[] keyMap = { KeyEvent.VK_UP, KeyEvent.VK_RIGHT,
			KeyEvent.VK_DOWN, KeyEvent.VK_LEFT };

	private ArrayList<Direction> nextDirection = new ArrayList<Direction>();

	private int keyIndex = 0;

	private Timer timer;
	private int origSpeed = bodySize;
	private double speed = origSpeed;

	private int score = 0;

	public SnakePanel() {

		//
		// for (Point x : snakeBody) {
		//
		// // System.out.print(x.x + "  " + x.y);
		// // System.out.println();
		//
		// }
		setBackground(Color.BLACK);
		setFocusable(true);
		addKeyListener(this);
		randFruitSetup();
		timer = new Timer((int) (1000 / speed), this);
		resetBody();
		timer.start();

	}

	public void actionPerformed(ActionEvent e) {

		moves();
	}

	public void moves() {

		if (playing) {

			if (nextDirection.size() > 0)
				executeDirection();

			head.x += deltaX;
			head.y += deltaY;
			nextHead = new Point(head.x + deltaX, head.y + deltaY);

			for (int i = snakeBody.size() - 1; i > 0; i--) {

				if (head.x == snakeBody.get(i).x
						&& head.y == snakeBody.get(i).y) {
					 playing = false;
					 nameEnter = true;
				}
				snakeBody.set(i, snakeBody.get(i - 1));

			}

			snakeBody.set(0, new Point(head.x, head.y));

			int nextHeadX = head.x + deltaX;
			int nextHeadY = head.y + deltaY;

			for (int i = 0; i < fruitX.size(); i++) {
				int fx = fruitX.get(i);
				int fy = fruitY.get(i);
				if (Math.abs(head.x - fx) < 5 && Math.abs(head.y - fy) < 5) {
					addBodySquare(i);
				}
			}

			if (autoPlay) {
				autonomous();
			}

			 if (head.x < 1 || head.x > 485 || head.y < 8 || head.y > 465) {
			
			 playing = false;
			 nameEnter = true;
			 }

		}

		repaint();

	}

	public void autonomous() {

		// If hit wall while moving sideways, change deltaX to 0 and deltaY
		// positive or
		// negative depending on nearest fruit

		
		
		if ((head.x < 8 + bodySize || head.x > 492 - 2 * bodySize)
				&& deltaX != 0) {

			// deltaX = 0;
			// deltaY = (head.y - fruitY.get(0) > 0) ? -bodySize : bodySize;
			upOrDown(head.y - fruitY.get(0));
		} else

		// If hit wall while moving sideways, change deltaY to 0 and deltaX
		// positive or
		// negative depending on nearest fruit

		if ((head.y < 16 || head.y > 492 - (4 * bodySize)) && deltaY != 0) {

			// deltaY = 0;
			// deltaX = (head.x - fruitX.get(0) > 0) ? -bodySize : bodySize;
			leftOrRight(head.x - fruitX.get(0));
		}

		// checkSelf();

		int prevDeltaX = deltaX;
		int prevDeltaY = deltaY;

		Random r = new Random();
		int r1 = r.nextInt(fruitX.size());

		for (int i = 0; i < fruitX.size(); i++) {
			if (i != r1) {
				int fruitXx = fruitX.get(i);
				int fruitYy = fruitY.get(i);

				if (Math.abs(head.x - fruitXx) < 5 && deltaX != 0) {

					upOrDown(head.y - fruitYy);
					// if (head.y - fruitYy > 0) {
					// up();
					// } else {
					// down();
					// }
				} else

				if (Math.abs(head.y - fruitYy) < 5 && deltaY != 0) {

					leftOrRight(head.x - fruitXx);
					// if (head.x - fruitXx > 0) {
					// left();
					// } else {
					// right();
					// }

				}

			}
		}

		// if ((prevDeltaX == -deltaX && deltaX != 0) || (prevDeltaY == -deltaY
		// && deltaY != 0)) {
		//
		// System.out.println("prevX: " + prevDeltaX);
		// System.out.println("deltaX: " + deltaX);
		//
		// System.out.println("prevY: " + prevDeltaY);
		// System.out.println("deltaY: " + deltaY);
		//
		// deltaX = -deltaX;
		// deltaY = -deltaY;
		//
		// }

		// Yay!!!
		
		checkSelf();
	}

	public void checkSelf() {

		// Point nextHead = new Point(head.x + deltaX, head.y + deltaY);
		
		if (snakeBody.contains(nextHead)) {
			// Point p = snakeBody.get(snakeBody.indexOf(nextHead));
			nextDirection.clear();
			for (int i = 1; i < snakeBody.size(); i++) {
				Point p = snakeBody.get(i);
				if (deltaX != 0) {

					if (nextHead.x == p.x) {

						// deltaX = 0;
						// deltaY = (nextHead.y - p.y > 0) ? -bodySize :
						// bodySize;
						upOrDown(-(nextHead.y - p.y));

					}

				} else

				if (deltaY != 0) {

					if (nextHead.y == p.y) {

						// deltaY = 0;
						// deltaX = (nextHead.x - p.x > 0) ? -bodySize :
						// bodySize;
						leftOrRight(-(nextHead.x - p.x));

					}

				}
			}
		}

	}

	public void up() {

		// int nextDeltaX = 0;
		// int nextDeltaY = -bodySize;
		//
		// Point nextHead = new Point(head.x + nextDeltaX, head.y + nextDeltaY);
		// && !snakeBody.contains(nextHead)

		if (deltaY != bodySize) {
			deltaX = 0;
			deltaY = -bodySize;
		}

		if (autoPlay)
			allowedMoves(UP);

	}

	public void down() {

		// int nextDeltaX = 0;
		// int nextDeltaY = bodySize;
		//
		// Point nextHead = new Point(head.x + nextDeltaX, head.y + nextDeltaY);
		// && !snakeBody.contains(nextHead)

		if (deltaY != -bodySize) {
			deltaX = 0;
			deltaY = bodySize;
		}

		if (autoPlay)
			allowedMoves(DOWN);
	}

	public void left() {

		// int nextDeltaX = -bodySize;
		// int nextDeltaY = 0;
		//
		// Point nextHead = new Point(head.x + nextDeltaX, head.y + nextDeltaY);
		// && !snakeBody.contains(nextHead)

		if (deltaX != bodySize) {
			deltaX = -bodySize;
			deltaY = 0;
		}

		if (autoPlay)
			allowedMoves(LEFT);

	}

	public void right() {

		// int nextDeltaX = bodySize;
		// int nextDeltaY = 0;
		//
		// Point nextHead = new Point(head.x + nextDeltaX, head.y + nextDeltaY);
		// && !snakeBody.contains(nextHead)

		if (deltaX != -bodySize) {
			deltaX = bodySize;
			deltaY = 0;
		}

		if (autoPlay)
			allowedMoves(RIGHT);

	}

	public void upOrDown(int i) {

		if (i > 0) {

			addDirection(Direction.up);

		} else {

			addDirection(Direction.down);

		}
	}

	public void leftOrRight(int i) {

		if (i < 0) {

			addDirection(Direction.right);

		} else {

			addDirection(Direction.left);

		}
	}

	public void nextHeadSet() {

		nextHead = new Point(head.x + deltaX, head.y + deltaY);

	}

	public Point setDirection() {

		direction = new Point(deltaX, deltaY);
		return direction;
	}

	public void setDelta(Point p) {

		deltaX = p.x;
		deltaY = p.y;

	}

	public Point getDirection() {

		return new Point(deltaX, deltaY);
	}

	public void allowedMoves(Point d) {

		ArrayList<Point> invalidDirections = new ArrayList<Point>();

		// if (direction.equals(LEFT)) invalidDirections.add(RIGHT);
		// if (direction.equals(RIGHT)) invalidDirections.add(LEFT);
		// if (direction.equals(UP)) invalidDirections.add(DOWN);
		// if (direction.equals(DOWN)) invalidDirections.add(UP);

		Point head = this.head;

		// Check Up

		if (snakeBody.contains(new Point(head.x + UP.x, head.y + UP.y))) {

			invalidDirections.add(UP);

		}

		// Check Down

		if (snakeBody.contains(new Point(head.x + DOWN.x, head.y + DOWN.y))) {

			invalidDirections.add(DOWN);

		}

		// Check Left

		if (snakeBody.contains(new Point(head.x + LEFT.x, head.y + LEFT.y))) {

			invalidDirections.add(LEFT);

		}

		// Check Right

		if (snakeBody.contains(new Point(head.x + RIGHT.x, head.y + RIGHT.y))) {

			invalidDirections.add(RIGHT);

		}

		if (!invalidDirections.contains(d)) {

			setDelta(d);

		}

	}

	public void addBodySquare(int fruitIndex) {

		int lastBodyX = snakeBody.get(snakeBody.size() - 1).x;
		int lastBodyY = snakeBody.get(snakeBody.size() - 1).y;
		int secondLastBodyX = snakeBody.get(snakeBody.size() - 2).x;
		int secondLastBodyY = snakeBody.get(snakeBody.size() - 2).y;

		int changeX = lastBodyX - secondLastBodyX;
		int changeY = lastBodyY - secondLastBodyY;

		snakeBody.add(new Point(lastBodyX + changeX, lastBodyY + changeY));
		snakeColor.add(fruitColor.get(fruitIndex));

		addGoodFruit(fruitIndex);

		// fruitX.set(fruitIndex, randNum());
		// fruitY.set(fruitIndex, randNum());

		fruitColor.set(fruitIndex, randColor());

		speed += .5;
		// System.out.println(speed);
		// System.out.println((int) (1000.0 / speed));
		timer.setDelay((int) (1000.0 / speed));
		score++;

	}

	public void resetBody() {

		snakeBody.clear();
		snakeColor.clear();
		snakeBody.add(new Point(250, 250));
		snakeBody.add(new Point(250, 260));
		snakeBody.add(new Point(250, 270));
		snakeBody.add(new Point(250, 280));

		for (int i = 0; i < snakeBody.size(); i++) {
			// Whoop
			// snakeColor.add(randColor());
			snakeColor.add(Color.WHITE);

		}

		head.x = 250;
		head.y = 250;
		deltaY = -bodySize;
		deltaX = 0;
		prevLoseKey = KeyEvent.VK_DOWN;
		speed = origSpeed;
		timer.setDelay((int) (1000.0 / speed));
		// timer = new Timer((int) (1000 / speed), this);
		// timer.start();

	}

	public void setKeys() {

		upKey = keyMap[0];
		rightKey = keyMap[1];
		downKey = keyMap[2];
		leftKey = keyMap[3];

	}

	public int randNum() {

		return ((int) (Math.random() * 45)) * 10 + 10;
	}

	public Color randColor() {

		return Colors[(int) (Math.random() * Colors.length)];
	}

	public void randFruitSetup() {

		fruitX.clear();
		fruitY.clear();
		for (int i = 0; i < numOfFruits; i++) {

			fruitX.add(randNum());
			fruitY.add(randNum());
			fruitColor.add(randColor());
		}

	}

	public int randFruitNum() {

		return (int) (Math.random() * fruitColor.size());
	}

	public void executeDirection() {

		Direction d = nextDirection.get(0);
		nextDirection.remove(0);

		switch (d) {

		case up:

			up();

			break;
		case down:
			down();

			break;
		case left:

			left();

			break;
		case right:

			right();

		}

	}

	public void addDirection(Direction d) {

		if (nextDirection.size() < 2)
			nextDirection.add(d);

	}

	// lastDirection = d;
	public void addGoodFruit(int fruitIndex) {

		int x = randNum();
		int y = randNum();

		if (deltaX != 0 && head.y == y) {

			if (deltaX > 0) {

				while (x < head.x) {

					x = randNum();

				}

			} else {

				while (x > head.x) {

					x = randNum();

				}

			}

		} else if (deltaY != 0 && head.x == x) {

			if (deltaY > 0) {

				while (y < head.y) {

					y = randNum();

				}

			} else {

				while (y > head.x) {

					y = randNum();

				}

			}

		}

		fruitX.set(fruitIndex, x);
		fruitY.set(fruitIndex, y);

	}

	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		g.setColor(Color.WHITE);
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(15));
		g2.drawRect(0, 0, 499, 477);
		g2.setStroke(new BasicStroke(2));

		if (startGame) {

			g.setFont(new Font("Joystix", Font.BOLD, 80));
			CenteredText.draw("SNAKE!!", 500, 500, g, true, 180);
			drawColorOptions(g, 415);
			g.setFont(new Font("Joystix", Font.BOLD, 20));

			CenteredText.draw("Press Enter to", 500, 500, g, true, 300);
			CenteredText.draw("Start", 500, 500, g, true, 330);

			g.setFont(new Font("Joystix", Font.BOLD, 12));

			CenteredText.draw(
					"Press keys Up, Right, Down, Left to map new keys", 500,
					500, g, true, 30);

		} else if (playing || paused) {

			g.setFont(new Font("Joystix", Font.BOLD, 40));
			g.setColor(Color.WHITE);
			CenteredText.draw(String.valueOf(score), 500, 500, g, true, 450);
			int i = 0;
			for (Point body : snakeBody) {

				g.setColor(snakeColor.get(i));
				i++;
				g.fillRect(body.x, body.y, bodySize, bodySize);
				g.setColor(Color.BLACK);
				g.drawRect(body.x, body.y, bodySize, bodySize);

			}
			for (i = 0; i < fruitX.size(); i++) {
				g.setColor(Color.BLACK);
				int fx = fruitX.get(i);
				int fy = fruitY.get(i);
				g.drawRect(fx, fy, bodySize, bodySize);

				// g.setColor(Color.WHITE);
				g.setColor(fruitColor.get(i));

				g.fillRect(fx + 1, fy + 1, bodySize - 2, bodySize - 2);

			}
			if (paused) {
				g.setFont(new Font("Joystix", Font.BOLD, 60));
				g.setColor(Color.WHITE);
				CenteredText.draw("Paused", 500, 500, g, true, 200);

				drawColorOptions(g, 300);
			}

		} else if (endGame) {

			g.setFont(new Font("Joystix", Font.BOLD, 40));
			g.setColor(Color.WHITE);
			CenteredText.draw(String.valueOf(score), 500, 500, g, true, 450);

			g.setFont(new Font("Joystix", Font.BOLD, 60));

			CenteredText.draw("You Lose!", 500, 500, g, true, 170);

			g.setFont(new Font("Joystix", Font.BOLD, 26));

			CenteredText.draw("Enter to Restart", 500, 500, g, true, 320);
		} else if (nameEnter) {

			scores.enterName(g, 500, 500, snakeBody.size(), pName);

		} else if (highScores) {

			// scores.setScores(timeSeconds, pName);
			scores.drawScores(g);
		}

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if (startGame && e.getKeyCode() != KeyEvent.VK_ENTER) {

			keyMap[keyIndex] = e.getKeyCode();
			keyIndex++;
			if (keyIndex > 3)
				keyIndex = 0;

		} else if (e.getKeyCode() == upKey) {

			addDirection(Direction.up);

		} else if (e.getKeyCode() == downKey) {

			addDirection(Direction.down);

		} else if (e.getKeyCode() == leftKey) {

			addDirection(Direction.left);

		} else if (e.getKeyCode() == rightKey) {

			addDirection(Direction.right);

		} else if (e.getKeyCode() == KeyEvent.VK_ENTER && !(playing || paused)) {

			if (startGame) {

				playing = true;
				startGame = false;
				setKeys();

			} else if (endGame) {

				resetBody();
				startGame = false;
				playing = true;
				nameEnter = false;
				highScores = false;
				endGame = false;
				pName = "";
				randFruitSetup();
				speed = 10;
				score = 0;

			} else if (nameEnter) {
				nameEnter = false;
				highScores = true;
				scores.setScores(score, pName);
			} else if (highScores) {

				highScores = false;
				endGame = true;
			} else {
				startGame = false;
				playing = true;

			}

		} else if (e.getKeyCode() == KeyEvent.VK_SPACE && (playing || paused)) {

			playing = !playing;
			paused = !paused;
		} else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_STANDARD
				&& nameEnter) {

			if (pName.length() < 10) {
				letter = e.getKeyChar();

				letter = Character.toUpperCase(letter);
				pName = pName.concat(letter.toString());
			}

		} else if (e.getKeyCode() == KeyEvent.VK_M && playing) {

			autoPlay = !autoPlay;

		} else {
			// if (startGame || endGame) {
			switch (e.getKeyCode()) {

			case KeyEvent.VK_R:
				fruitColor.set(randFruitNum(), Color.RED);

				break;
			case KeyEvent.VK_G:
				fruitColor.set(randFruitNum(), Color.GREEN);

				break;
			case KeyEvent.VK_B:
				fruitColor.set(randFruitNum(), Color.CYAN);

				break;
			case KeyEvent.VK_Y:
				fruitColor.set(randFruitNum(), Color.YELLOW);

				break;
			case KeyEvent.VK_O:
				fruitColor.set(randFruitNum(), Color.ORANGE);

				break;
			case KeyEvent.VK_W: // VK_W - White is default case
				fruitColor.set(randFruitNum(), Color.WHITE);

				break;

			}

			// }
		}
	}

	public void drawColorOptions(Graphics g, int colorY) {

		g.setFont(new Font(Font.DIALOG, Font.BOLD, 45));
		g.setColor(Color.RED);
		g.drawString("R", 50, colorY);
		g.setColor(Color.GREEN);
		g.drawString("G", 140, colorY);
		g.setColor(Color.CYAN);
		g.drawString("B", 230, colorY);
		g.setColor(Color.YELLOW);
		g.drawString("Y", 315, colorY);
		g.setColor(Color.ORANGE);
		g.drawString("O", 410, colorY);
		// g.setColor(Color.BLACK);
		// g.drawString("W", 500, colorY);

		g.setColor(Color.WHITE);

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
