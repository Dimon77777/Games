package tunnelRunner;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.Timer;

import utilityClasses.*;

public class TunnelPanel extends JPanel implements ActionListener, KeyListener {

	private boolean UpPressed = false;
	private boolean DownPressed = false;

	private boolean startGame = true;
	private boolean playing = false;
	private boolean endGame = false;
	private boolean nameEnter = false;
	private boolean highScores = false;

	private ScoreInfo scores = new ScoreInfo("tunnel");

	private int holeX = 500;
	private int holeY = 220;

	private String pName = "";
	private Character letter;

	private int widthOfHole = 5;
	private int holeNumber = 500 / widthOfHole;
	private int holeSize = 150;

	private int[] holesX = new int[holeNumber];
	private int[] holesY = new int[holeNumber];

	/*
	 * private int[] holesX = { 500, 500 + (15), 500 + (15) * 2, 500 + (15) * 3,
	 * 500 + (15) * 4, 500 + (15) * 5, 500 + (15) * 6, 500 + (15) * 7, 500 +
	 * (15) * 8, 500 + (15) * 9, 500 + (15) * 10, 500 + (15) * 11, 500 + (15) *
	 * 12, 500 + (15) * 13, 500 + (15) * 14, 500 + (15) * 15};
	 * 
	 * private int[] holesY = { 220, 230, 240, 230, 240, 230, 220, 230, 240,
	 * 230, 240, 230, 220, 230, 240, 250};
	 */
	private int ballX = 20;
	private int ballY = 40;
	private int deltaX = 0;
	private int deltaY = 0;
	private int diameter = 15;
	private int buffer = 0;

	private int ballSpeed = 5;
	private int origBallSpeed = ballSpeed;
	private int holeSpeed = 5;
	private int origHoleSpeed = holeSpeed;

	private int timeSplit = 0;
	private int timeSeconds = 0;
	private boolean paused = false;

	public TunnelPanel() {

		for (int i = 0; i < holesX.length; i++) {
			holesX[i] = 500 + widthOfHole * i;
			holesY[i] = randomY(i);

		}
		setBackground(Color.BLACK);

		setFocusable(true);
		addKeyListener(this);

		Timer timer = new Timer(1000 / 60, this);
		timer.start();

	}

	public void actionPerformed(ActionEvent e) {

		// ifStarted();
		moves();

	}

	public void moves() {

		if (playing) {
			int nextBallX = ballX + deltaX;
			int nextBallY = ballY + deltaY;

			int leftX = ballX + deltaX;
			int rightX = ballX + deltaX + diameter;
			int topY = ballY + deltaY;
			int bottomY = ballY + deltaY + diameter;

			Color topLeftColor = getColor(leftX + buffer, topY + buffer);
			Color topRightColor = getColor(rightX - buffer, topY + buffer);
			Color bottomLeftColor = getColor(leftX + buffer, bottomY - buffer);
			Color bottomRightColor = getColor(rightX - buffer, bottomY - buffer);

			// System.out.println(topRightColor.toString() + "   ");

			if (topLeftColor.equals(Color.WHITE)
					|| topRightColor.equals(Color.WHITE)
					|| bottomLeftColor.equals(Color.WHITE)
					|| bottomRightColor.equals(Color.WHITE)) {
				playing = false;
				// endGame = true;
				nameEnter = true;

			}

			if (UpPressed) {
				deltaY = -ballSpeed;
			} else if (DownPressed) {
				deltaY = ballSpeed;
			} else {
				deltaY = 0;
			}

			for (int i = 0; i < holesX.length; i++) {

				if (holesX[i] <= 0) {
					holesX[i] = 500 + holesX[i];
					holesY[i] = randomY(i);

				}

				holesX[i] -= holeSpeed;

			}

			timeSplit++;
			if (timeSplit == 60) {
				timeSplit = 0;
				timeSeconds++;
			}

			// holeSpeed = (int) (timeSeconds / 10) + 4;

			if (ballY + deltaY + diameter > 0)
				ballY += deltaY;

			// int ballSpeedChange = (int) (timeSeconds / 10);
			// System.out.println(ballSpeed);

		}
		repaint();

	}

	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		g.setColor(Color.WHITE);

		if (startGame) {

			g.setFont(new Font("Joystix", Font.BOLD, 40));
			CenteredText.draw("TUNNEL", 150, g);
			// g.drawString("HOLE IN THE", 60, 210);
			CenteredText.draw("RUNNER", 200, g);
			// g.drawString("WALL", 180, 260);
			g.setFont(new Font("Joystix", Font.BOLD, 20));

			CenteredText.draw("Press Enter to", 300, g);
			// g.drawString("Press Enter to", 120, 300);
			CenteredText.draw("Start", 330, g);
			// g.drawString("Start", 200, 330);

		} else if (playing || paused) {

			// g.fillRect(holeX, holeY + 100, 15, 500 - holeY - 100);

			/*
			 * g.fillRect(leftX, topY, 1, 1); g.fillRect(leftX, bottomY, 1, 1);
			 * g.fillRect(rightX, topY, 1, 1); g.fillRect(rightX, bottomY, 1,
			 * 1);
			 */

			for (int i = 0; i < holesX.length; i++) {

				g.setColor(Color.WHITE);
				g.fillRect(holesX[i], 0, widthOfHole, 500);
				g.setColor(Color.BLACK);

				g.fillRect(holesX[i], holesY[i], widthOfHole, holeSize);
			}
			g.setColor(Color.WHITE);

			g.fillOval(ballX, ballY, diameter, diameter);

			g.setColor(Color.BLACK);
			g.setFont(new Font("Joystix", Font.BOLD, 20));
			FontMetrics f = g.getFontMetrics();
			int w = f.stringWidth(String.valueOf(timeSeconds));
			int h = f.getAscent();

			g.fillRect(3, 3, w + 3, h);

			g.setColor(Color.WHITE);
			g.setFont(new Font("Joystix", Font.BOLD, 20));
			g.drawString(String.valueOf(timeSeconds), 5, 20);

			if (paused) {
				g.setFont(new Font("Joystix", Font.BOLD, 60));
				CenteredText.draw("Paused", 200, g);
			}

		} else if (endGame) {

			g.setColor(Color.WHITE);
			g.setFont(new Font("Joystix", Font.BOLD, 20));
			g.drawString(String.valueOf(timeSeconds), 5, 20);

			g.setFont(new Font("Joystix", Font.BOLD, 60));
			g.setColor(Color.WHITE);
			CenteredText.draw("You Lose!", 170, g);
			// g.drawString("You Lose!", 50, 270);

			g.setFont(new Font("Joystix", Font.BOLD, 26));

			CenteredText.draw("Enter to Restart", 320, g);
			// g.drawString("Enter to Restart", 80, 400);

		} else if (nameEnter) {

			scores.enterName(g, 500, 500, timeSeconds, pName);

		} else if (highScores) {

			//scores.setScores(timeSeconds, pName);
			scores.drawScores(g);
		}

	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	 
	}

	public void keyPressed(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_UP) {
			UpPressed = true;

		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			DownPressed = true;

		}  else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_STANDARD) {

			if (nameEnter) {
				if (pName.length() < 10) {
					letter = e.getKeyChar();

					letter = Character.toUpperCase(letter);
					pName = pName.concat(letter.toString());
				}
			}
		} 
	}

	public void keyReleased(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_UP) {
			UpPressed = false;

		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			DownPressed = false;

		} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			
			if (endGame) {
				holeX = 500;
				for (int i = 0; i < holesX.length; i++) {
					holesX[i] = 500 + (widthOfHole) * i;
					holesY[i] = randomY(i);
				}
				holeSpeed = origHoleSpeed;
				ballSpeed = origBallSpeed;
				timeSplit = 0;
				timeSeconds = 0;
				startGame = false;
				playing = true;
				nameEnter = false;
				highScores = false;
				endGame = false;
				pName = "";
				
			} else if (nameEnter) {
				nameEnter = false;
				highScores = true;
				scores.setScores(timeSeconds, pName);

			} else if (highScores) {
				
				
				highScores = false;
				endGame = true;
			} else {
			startGame = false;
			playing = true;
			}
		}

	}

	public int randomY(int i) {
		// System.out.println(((int) (Math.random() * 6)) * 80);
		/*
		 * int firstX = holesX[0]; holesX[0] = holesX[1]; holesX[1] = holesX[2];
		 * holesX[2] = firstX;
		 * 
		 * int firstY = holesY[0]; holesY[0] = holesY[1]; holesY[1] = holesY[2];
		 * holesY[2] = firstY;
		 */
		if (i == 0)
			i = holesY.length - 1;
		int prevHole = holesY[i - 1];
		int wayPrevHole = prevHole;
		/*
		 * try { if (i - 30 < 0) { i = 124 - i; } wayPrevHole = holesY[i]; }
		 * catch (ArrayIndexOutOfBoundsException e) {
		 * 
		 * }
		 */
		// && Math.abs(randNum - wayPrevHole) > 60
		int randNum = ((int) (Math.random() * 300));
		while (Math.abs(randNum - prevHole) > 20) {
			randNum = ((int) (Math.random() * 300));
		}
		return randNum;

	}

	public static Color getColor(int x, int y) {
		try {
			// System.out.print("(" + x + ", " + y + ")  ");
			return new Robot().getPixelColor(x, y + 40);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Color.BLACK;
		}
	}

}
