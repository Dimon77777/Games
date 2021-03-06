package holeInTheWall;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.Timer;

import utilityClasses.*;

public class HolePanel extends JPanel implements ActionListener, KeyListener {

	private boolean UpPressed = false;
	private boolean DownPressed = false;

	private boolean startGame = true;
	private boolean playing = false;
	private boolean endGame = false;
	private boolean nameEnter = false;
	private boolean highScores = false;

	private ScoreInfo scores = new ScoreInfo("gameName");
	private String pName = "";
	private Character letter;

	private int holeX = 500;
	private int holeY = 220;

	private int holeNumber = 2;
	private int[] holesX = { 500, 500 + (170), 500 + (170) * 2 };

	private int[] holesY = { 220, 270, 250 };

	private int ballX = 20;
	private int ballY = 240;
	private int deltaX = 0;
	private int deltaY = 0;
	private int diameter = 20;
	private int buffer = 0;

	private int currentHole = 0;

	private int ballSpeed = 5;
	private int holeSpeed = 4;

	private int timeSplit = 0;
	private int timeSeconds = 0;
	private boolean paused = false;

	public HolePanel() {

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
			int nextBallCenterX = nextBallX + diameter / 2;
			int nextBallCenterY = nextBallY + diameter / 2;

			int leftX = ballX + deltaX;
			int rightX = ballX + deltaX + diameter;
			int topY = ballY + deltaY;
			int bottomY = ballY + deltaY + diameter;
			/*
			 * Color topLeftColor = getColor(leftX + buffer, topY + buffer);
			 * Color topRightColor = getColor(rightX - buffer, topY + buffer);
			 * Color bottomLeftColor = getColor(leftX + buffer, bottomY -
			 * buffer); Color bottomRightColor = getColor(rightX - buffer,
			 * bottomY - buffer);
			 */
			// System.out.println(topRightColor.toString() + "   ");

			/*
			 * if (topLeftColor.equals(Color.WHITE) ||
			 * topRightColor.equals(Color.WHITE) ||
			 * bottomLeftColor.equals(Color.WHITE) ||
			 * bottomRightColor.equals(Color.WHITE)) { playing = false; endGame
			 * = true;
			 * 
			 * }
			 */

			if (UpPressed) {
				deltaY = -ballSpeed;
			} else if (DownPressed) {
				deltaY = ballSpeed;
			} else {
				deltaY = 0;
			}

			// int holeLeft = holesX[currentHole];
			// int holeRight = holesX[currentHole] + 15;
			// int holeMiddleY = 50 + holesY[currentHole];
			//
			// if (holeLeft < rightX) {
			// if (leftX < holeRight) {
			// if ( distance(nextBallCenterX, nextBallCenterY, holeLeft + 7,
			// holeMiddleY) > 40) {
			// playing = false;
			// endGame = true;
			// }
			//
			// } else {
			// currentHole = (currentHole == 2) ? 0 : currentHole + 1;
			// }
			//
			//
			//
			//
			// }

			for (int i = 0; i < holesX.length; i++) {

				if (holesX[i] <= 0) {
					holesX[i] = 500;
					holesY[i] = randomY(i);

				}

				if (distance(nextBallCenterX, nextBallCenterY, holesX[i] + 7,
						holesY[i]) - diameter / 2 < 0
						|| distance(nextBallCenterX, nextBallCenterY,
								holesX[i] + 7, holesY[i] + 100) - diameter / 2 < 0
						|| getColor(ballX + deltaX + diameter,
								ballY + deltaY + diameter / 2).equals(
								Color.WHITE)) {

					playing = false;
					nameEnter = true;

				}

				holesX[i] -= holeSpeed;

			}

			timeSplit++;
			if (timeSplit == 60) {
				timeSplit = 0;
				timeSeconds++;
			}

			holeSpeed = (int) (timeSeconds / 10) + 4;

			if (ballY + deltaY + diameter > 0)
				ballY += deltaY;

			// int ballSpeedChange = (int) (timeSeconds / 10);
			// System.out.println(ballSpeed);

		}
		repaint();

	}

	public double distance(int ballCenterX, int ballCenterY, int boxCenterX,
			int boxCenterY) {

		return Math.sqrt(Math.pow(ballCenterX - boxCenterX, 2)
				+ Math.pow(ballCenterY - boxCenterY, 2));
	}

	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		g.setColor(Color.WHITE);

		if (startGame) {

			g.setFont(new Font("Joystix", Font.BOLD, 40));
			CenteredText.draw("HOLE IN THE", 500, 500, g,
					true, 150);
			// g.drawString("HOLE IN THE", 60, 210);
			CenteredText.draw("WALL", 500, 500, g, true,
					200);
			// g.drawString("WALL", 180, 260);
			g.setFont(new Font("Joystix", Font.BOLD, 20));

			CenteredText.draw("Press Enter to", 500, 500,
					g, true, 300);
			// g.drawString("Press Enter to", 120, 300);
			CenteredText.draw("Start", 500, 500, g, true,
					330);
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
				g.fillRect(holesX[i], 0, 15, 500);
				g.setColor(Color.BLACK);

				g.fillRect(holesX[i], holesY[i], 15, 100);
			}
			g.setColor(Color.WHITE);

			g.fillOval(ballX, ballY, diameter, diameter);

			g.setFont(new Font("Joystix", Font.BOLD, 15));
			g.drawString(String.valueOf(timeSeconds), 5, 15);

			if (paused) {
				g.setFont(new Font("Joystix", Font.BOLD, 60));
				CenteredText.draw("Paused", 500, 500, g,
						true, 200);
			}

		} else if (endGame) {

			g.setFont(new Font("Joystix", Font.BOLD, 60));

			CenteredText.draw("You Lose!", 500, 500, g,
					true, 170);
			// g.drawString("You Lose!", 50, 270);

			g.setFont(new Font("Joystix", Font.BOLD, 26));

			CenteredText.draw("Enter to Restart", 500,
					500, g, true, 320);
			// g.drawString("Enter to Restart", 80, 400);

		} else if (nameEnter) {

			scores.enterName(g, 500, 500, timeSeconds, pName);

		} else if (highScores) {

			// scores.setScores(timeSeconds, pName);
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

		} else if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_STANDARD
				&& nameEnter) {

			if (pName.length() < 10) {
				letter = e.getKeyChar();

				letter = Character.toUpperCase(letter);
				pName = pName.concat(letter.toString());
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
					holesX[i] = 500 + (170) * i;
					holesY[i] = randomY(i);
				}
				holeSpeed = 4;
				ballSpeed = 5;
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
			i = 3;
		int prevHole = holesY[i - 1];
		int randNum = ((int) (Math.random() * 400));
		while (Math.abs(randNum - prevHole) > 200) {
			randNum = ((int) (Math.random() * 400));
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
