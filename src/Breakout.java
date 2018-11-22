/*
 * File: Breakout.java
 * -------------------
 * Name: Oleh Kurachenko, Viktoria Kozopas
 * Section Leader: ???
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 40;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH = (WIDTH - (NBRICKS_PER_ROW - 1)
			* BRICK_SEP)
			/ NBRICKS_PER_ROW;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

	/** Number of turns */
	private static final int NTURNS = 3;

	/* Method: run() */
	/** Runs the Breakout program. */
	public void run() {
		setGameBoard();

		countdown(5);

		addMouseListeners();

		while (bricksRemaind != 0 && turnsRemaind != 0) {
			moveBall();
			checkForCollisions();
			pause(DELAY);
		}

		gameIsIn = false;
		if (bricksRemaind == 0)
			woneCongratulations();
		else
			gameOver();

		return;
	}

	private void woneCongratulations() {
		GLabel label = new GLabel("WINNER!");
		label.setFont("Arial-" + ((int) (0.225 * WIDTH)));
		label.setColor(Color.green);
		add(label, WIDTH / 2 - (label.getWidth() / 2), 340);
	}

	private void gameOver() {
		GLabel label = new GLabel("Game Over");
		label.setFont("Arial-" + ((int) (0.175 * WIDTH)));
		label.setColor(Color.red);
		add(label, WIDTH / 2 - (label.getWidth() / 2), 340);
	}

	/**
	 * Check for contact with paddle if paddle is in the one of vertex collision
	 * points
	 * 
	 * Direction multipliers are made to hold right direction while multiple
	 * collisions with the same object
	 * 
	 * @param fx
	 *            - x coordinate of nearest point of found element (paddle)
	 * @param fy
	 *            - y coordinate of nearest point of found element (paddle)
	 * 
	 * @param verticalDirectoinMultiplier
	 *            {1, -1}
	 * @param horizontalDirectionMultiplier
	 *            {1, -1}
	 * @return true if end of collisions check
	 */
	private boolean checkForContactWithPaddle(double fx, double fy,
			double verticalDirectoinMultiplier,
			double horizontalDirectionMultiplier) {

		GObject temp;
		temp = (horizontalDirectionMultiplier == 1) ? getElementAt(ball.getX()
				+ BALL_RADIUS, ball.getY() - 1) : getElementAt(ball.getX()
				+ BALL_RADIUS, ball.getY() + 1 + 2 * BALL_RADIUS);
		if (temp == paddle) {
			ballSpeedY = verticalDirectoinMultiplier * Math.abs(ballSpeedY);
			putOutOfFound(BALL_RADIUS,
					(horizontalDirectionMultiplier == 1) ? -1
							: 1 + 2 * BALL_RADIUS);
			return true;
		}
		temp = (verticalDirectoinMultiplier == 1) ? getElementAt(
				ball.getX() - 1, ball.getY() + BALL_RADIUS) : getElementAt(
				ball.getX() + 2 * BALL_RADIUS + 1, ball.getY() + BALL_RADIUS);
		if (temp == paddle) {
			ballSpeedX = horizontalDirectionMultiplier * Math.abs(ballSpeedX);
			putOutOfFound((horizontalDirectionMultiplier == 1) ? -1
					: BALL_RADIUS * 2 + 1, BALL_RADIUS);
			return true;
		}

		// coordinates of O_PCV vector
		double xv = ball.getX() + BALL_RADIUS - fx, yv = ball.getY()
				+ BALL_RADIUS - fy;
		// (if modulus of vector < radius of ball)
		// -> is physical contact
		double v = Math.sqrt(xv * xv + yv * yv);

		if (v <= BALL_RADIUS)
			if ((horizontalDirectionMultiplier == 1 && ballSpeedX > 0)
					|| (horizontalDirectionMultiplier == -1 && ballSpeedX < 0)) {

				double tempArchive = ballSpeedX;
				ballSpeedX = horizontalDirectionMultiplier
						* Math.abs(ballSpeedY);
				ballSpeedY = verticalDirectoinMultiplier
						* Math.abs(tempArchive);

			} else {

				double cosValue = (ballSpeedX * xv + ballSpeedY * yv)
						/ (BALLSTARTSPEED * v);
				double ballSpeedProectionOnV = BALLSTARTSPEED * cosValue;
				// now V appears as additon vector = -2*ballSpeedProectionOnV
				xv = xv * ballSpeedProectionOnV / v;
				yv = yv * ballSpeedProectionOnV / v;
				xv *= -2;
				yv *= -2;

				ballSpeedX += xv;
				ballSpeedY += yv;

				while (v <= BALL_RADIUS) {
					moveBall();
					xv = ball.getX() + BALL_RADIUS - fx;
					yv = ball.getY() + BALL_RADIUS - fy;
					v = Math.sqrt(xv * xv + yv * yv);
				}

				return true;
			}
		return false;
	}

	/**
	 * Move ball out of the paddle after direction of ball moving was changed.
	 * 
	 * @param ballXTOcollisionPointXAdditor
	 * @param ballYTOcollisionPointYAdditor
	 */
	private void putOutOfFound(double ballXTOcollisionPointXAdditor,
			double ballYTOcollisionPointYAdditor) {
		while (getElementAt(ball.getX() + ballXTOcollisionPointXAdditor,
				ball.getY() + ballXTOcollisionPointXAdditor) == paddle)
			moveBall();
	}

	/**
	 * Check for collision with horizontal or vertical line of paddle
	 * 
	 * Direction multipliers are made to hold right direction while multiple
	 * collisions with the same object
	 * 
	 * 
	 * @param verticalDirectoinMultiplier
	 * @param horizontalDirectionMultiplier
	 * @return true if brick removed
	 */
	private boolean checkForContactWithPaddle(
			double verticalDirectoinMultiplier,
			double horizontalDirectionMultiplier) {

		GObject temp;

		if (horizontalDirectionMultiplier != 0) {
			temp = (horizontalDirectionMultiplier == 1) ? getElementAt(
					ball.getX() + BALL_RADIUS, ball.getY() - 1) : getElementAt(
					ball.getX() + BALL_RADIUS, ball.getY() + 1 + 2
							* BALL_RADIUS);
			if (temp == paddle) {
				ballSpeedY = verticalDirectoinMultiplier * Math.abs(ballSpeedY);
				putOutOfFound(BALL_RADIUS,
						(horizontalDirectionMultiplier == 1) ? -1
								: BALL_RADIUS * 2 + 1);
				return true;
			}
		}
		if (verticalDirectoinMultiplier != 0) {
			temp = (verticalDirectoinMultiplier == 1) ? getElementAt(
					ball.getX() - 1, ball.getY() + BALL_RADIUS) : getElementAt(
					ball.getX() + 2 * BALL_RADIUS + 1, ball.getY()
							+ BALL_RADIUS);
			if (temp == paddle) {
				ballSpeedX = horizontalDirectionMultiplier
						* Math.abs(ballSpeedX);
				putOutOfFound((horizontalDirectionMultiplier == 1) ? -1
						: BALL_RADIUS * 2 + 1, BALL_RADIUS);
				return true;
			}
		}

		return false;
	}

	/**
	 * Check for contact with brick if brick is in the one of vertex collision
	 * points
	 * 
	 * Direction multipliers are made to hold right direction while multiple
	 * collisions with the same object
	 * 
	 * @param fx
	 *            - x coordinate of nearest point of found element (paddle)
	 * @param fy
	 *            - y coordinate of nearest point of found element (paddle)
	 * 
	 * @param verticalDirectoinMultiplier
	 *            {1, -1}
	 * @param horizontalDirectionMultiplier
	 *            {1, -1}
	 * @param found
	 *            - pointer to found object
	 * @return true if end of collisions check
	 */
	private boolean checkForContactWithBrick(double fx, double fy,
			double verticalDirectoinMultiplier,
			double horizontalDirectionMultiplier, GObject found) {
		if (found == bricksRemaindLabel || found == attempRemaindLabel)
			return false;

		// System.out.println(found + " -> " + (found.getWidth()));

		GObject temp;
		temp = (horizontalDirectionMultiplier == 1) ? getElementAt(ball.getX()
				+ BALL_RADIUS, ball.getY() - 1) : getElementAt(ball.getX()
				+ BALL_RADIUS, ball.getY() + 1 + 2 * BALL_RADIUS);

		if (temp != null) {
			ballSpeedY = verticalDirectoinMultiplier * Math.abs(ballSpeedY);
			remove(found);
			bricksRemaind--;
			resetBallRemaindLabel();
			return true;
		}
		temp = (verticalDirectoinMultiplier == 1) ? getElementAt(
				ball.getX() - 1, ball.getY() + BALL_RADIUS) : getElementAt(
				ball.getX() + 2 * BALL_RADIUS + 1, ball.getY() + BALL_RADIUS);

		if (temp != null) {
			ballSpeedX = horizontalDirectionMultiplier * Math.abs(ballSpeedX);
			remove(found);
			bricksRemaind--;
			resetBallRemaindLabel();
			return true;
		}

		// coordinates of O_PCV vector
		double xv = ball.getX() + BALL_RADIUS - fx, yv = ball.getY()
				+ BALL_RADIUS - fy;
		// (if modulus of vector < radius of ball) -> is physical contact
		double v = Math.sqrt(xv * xv + yv * yv);

		if (v <= BALL_RADIUS) {
			double cosValue = (ballSpeedX * xv + ballSpeedY * yv)
					/ (BALLSTARTSPEED * v);
			double ballSpeedProectionOnV = BALLSTARTSPEED * cosValue;
			// now V appears as additon vector = -2*ballSpeedProectionOnV
			xv = xv * ballSpeedProectionOnV / v;
			yv = yv * ballSpeedProectionOnV / v;
			xv *= -2;
			yv *= -2;

			ballSpeedX += xv;
			ballSpeedY += yv;

			/*
			 * while (v <= BALL_RADIUS) { moveBall(); xv = ball.getX() +
			 * BALL_RADIUS - fx; yv = ball.getY() + BALL_RADIUS - fy; v =
			 * Math.sqrt(xv * xv + yv * yv); }
			 */

			remove(found);

			bricksRemaind--;
			resetBallRemaindLabel();
			return true;
		}
		return false;
	}

	/**
	 * Check for collision of ball with horizontal or vertical line of brick
	 * 
	 * @param verticalDirectoinMultiplier
	 * @param horizontalDirectionMultiplier
	 * @param found
	 * @return true if brick removed
	 */
	private boolean checkForContactWithBrick(
			double verticalDirectoinMultiplier,
			double horizontalDirectionMultiplier, GObject found) {
		if (found == bricksRemaindLabel || found == attempRemaindLabel)
			return false;

		GObject temp;
		temp = (horizontalDirectionMultiplier == 1) ? getElementAt(ball.getX()
				+ BALL_RADIUS, ball.getY() - 1) : getElementAt(ball.getX()
				+ BALL_RADIUS, ball.getY() + 1 + 2 * BALL_RADIUS);

		if (temp != null) {
			ballSpeedY = verticalDirectoinMultiplier * Math.abs(ballSpeedY);
			remove(found);
			bricksRemaind--;
			resetBallRemaindLabel();
			return true;
		}
		temp = (verticalDirectoinMultiplier == 1) ? getElementAt(
				ball.getX() - 1, ball.getY() + BALL_RADIUS) : getElementAt(
				ball.getX() + 2 * BALL_RADIUS + 1, ball.getY() + BALL_RADIUS);

		if (temp != null) {
			ballSpeedX = horizontalDirectionMultiplier * Math.abs(ballSpeedX);
			remove(found);
			bricksRemaind--;
			resetBallRemaindLabel();
			return true;
		}
		return false;
	}

	/**
	 * Check for collisions and set reactions on collision
	 * 
	 * @param x
	 *            of collision point
	 * @param y
	 *            of collision point
	 * @param verticalDirectoinMultiplier
	 * @param horizontalDirectionMultiplier
	 * @param collidesWithLinesOnly
	 * @return true if some collision happend
	 */
	private boolean cheakBallCollisionPoint(double x, double y,
			double verticalDirectoinMultiplier,
			double horizontalDirectionMultiplier, boolean collidesWithLinesOnly) {
		GObject found = getElementAt(x, y);

		if (found == null)
			return false;

		if (collidesWithLinesOnly) {

			if (found == paddle) {
				if (checkForContactWithPaddle(verticalDirectoinMultiplier,
						horizontalDirectionMultiplier))
					return true;
			} else {
				if (checkForContactWithBrick(verticalDirectoinMultiplier,
						horizontalDirectionMultiplier, found))
					return true;

			}

		} else {
			if (found == paddle) {
				if (checkForContactWithPaddle(found.getX()
						+ ((horizontalDirectionMultiplier == 1) ? PADDLE_WIDTH
								: 0), found.getY()
						+ ((verticalDirectoinMultiplier == 1) ? PADDLE_HEIGHT
								: 0), verticalDirectoinMultiplier,
						horizontalDirectionMultiplier))
					return true;
			} else {
				if (checkForContactWithBrick(found.getX()
						+ ((horizontalDirectionMultiplier == 1) ? PADDLE_WIDTH
								: 0), found.getY()
						+ ((verticalDirectoinMultiplier == 1) ? PADDLE_HEIGHT
								: 0), verticalDirectoinMultiplier,
						horizontalDirectionMultiplier, found))
					return true;

			}
		}

		return false;
	}

	private void checkForCollisions() {

		cheakBallCollisionPoint(ball.getX() + BALL_RADIUS, ball.getY() - 1, 1,
				1, true);

		cheakBallCollisionPoint(ball.getX() + 2 * BALL_RADIUS + 1, ball.getY()
				+ BALL_RADIUS, 1, -1, true);

		cheakBallCollisionPoint(ball.getX() - 1, ball.getY() + BALL_RADIUS, -1,
				1, true);

		cheakBallCollisionPoint(ball.getX() + BALL_RADIUS, ball.getY()
				+ BALL_RADIUS * 2 + 1, -1, -1, true);

		cheakBallCollisionPoint(ball.getX(), ball.getY(), 1, 1, false);

		cheakBallCollisionPoint(ball.getX() + BALL_RADIUS * 2, ball.getY(), 1,
				-1, false);

		cheakBallCollisionPoint(ball.getX(), ball.getY() + BALL_RADIUS * 2, -1,
				1, false);

		cheakBallCollisionPoint(ball.getX() + BALL_RADIUS * 2, ball.getY()
				+ BALL_RADIUS * 2, -1, -1, false);

	}

	private void moveBall() {
		if (Math.abs(ballSpeedX) < 0.001 || Math.abs(ballSpeedY) < 0.001) {
			ballSpeedX += 0.3;
			ballSpeedY += 0.3;
		}

		ball.move(ballSpeedX, ballSpeedY);
		if (ball.getX() < 0) {
			ball.setLocation(0, ball.getY());
			ballSpeedX *= -1;
		}
		if (ball.getX() > WIDTH - 2 * BALL_RADIUS) {
			ball.setLocation(WIDTH - 2 * BALL_RADIUS, ball.getY());
			ballSpeedX *= -1;
		}
		if (ball.getY() < 0) {
			ball.setLocation(ball.getX(), 0);
			ballSpeedY *= -1;
		}
		if (ball.getY() > HEIGHT - 2 * BALL_RADIUS) {
			// ball.setLocation(ball.getX(), HEIGHT - 2 * BALL_RADIUS);
			// ballSpeedY *= -1;
			setBall();
		}
	}

	/**
	 * moves paddle
	 */
	public void mouseMoved(MouseEvent e) {
		if (gameIsIn)
			if (e.getX() > PADDLE_WIDTH / 2
					&& e.getX() < WIDTH - PADDLE_WIDTH / 2)
				paddle.setLocation(e.getX() - PADDLE_WIDTH / 2, paddle.getY());
	}

	private void setGameBoard() {
		this.setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);

		for (int i = 0; i < NBRICK_ROWS; i++)
			for (int j = 0; j < NBRICKS_PER_ROW; j++) {
				addBrick(j * (BRICK_WIDTH + BRICK_SEP) + 2, BRICK_Y_OFFSET + i
						* (BRICK_HEIGHT + BRICK_SEP), rowColor(i));
			}
		bricksRemaind = NBRICK_ROWS * NBRICKS_PER_ROW;

		paddle = new GRect(WIDTH / 2 - PADDLE_WIDTH / 2, HEIGHT
				- PADDLE_Y_OFFSET - PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);

		ball = new GOval(WIDTH / 2 - BALL_RADIUS, HEIGHT / 2 - BALL_RADIUS,
				BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		ball.setColor(Color.gray);
		ball.setFillColor(Color.gray);
		add(ball);

		resetBallRemaindLabel();
		resetAttempsLabel();

		setBall();
	}

	private void resetBallRemaindLabel() {
		if (bricksRemaindLabel != null)
			remove(bricksRemaindLabel);

		bricksRemaindLabel = new GLabel("" + bricksRemaind);
		bricksRemaindLabel.setColor(Color.gray);
		bricksRemaindLabel.setFont("Arial-36");
		add(bricksRemaindLabel, WIDTH - bricksRemaindLabel.getWidth() - 5,
				HEIGHT - 5);
	}

	private void resetAttempsLabel() {
		if (attempRemaindLabel != null)
			remove(attempRemaindLabel);

		attempRemaindLabel = new GLabel("Attemps:" + turnsRemaind);
		attempRemaindLabel.setColor(Color.gray);
		attempRemaindLabel.setFont("Arial-36");
		add(attempRemaindLabel, 5, HEIGHT - 5);
	}

	/**
	 * Moves ball to the middle and resets speed of the ball
	 * 
	 * Correct speed to Y-proection of S - vector of speed is not less then
	 * 1/HORIZONTAL_BALLSPEED_PROECTION_DIVADER
	 */
	private void setBall() {
		turnsRemaind--;
		resetAttempsLabel();

		remove(ball);
		add(ball, WIDTH / 2 - BALL_RADIUS, HEIGHT / 2 - BALL_RADIUS);
		if (turnsRemaind != NTURNS && turnsRemaind != 0)
			countdown(2);

		do {
			ballSpeedX = rgen.nextDouble(-100, 100);
			ballSpeedY = rgen.nextDouble(-100, 100);

			double tempMod = Math.sqrt(Math.pow(ballSpeedX, 2)
					+ Math.pow(ballSpeedY, 2));

			tempMod = BALLSTARTSPEED / tempMod;

			ballSpeedX *= tempMod;
			ballSpeedY *= tempMod;
		} while (Math.abs(ballSpeedY) < BALLSTARTSPEED
				/ HORIZONTAL_BALLSPEED_PROECTION_DIVADER);

	}

	/**
	 * Adds labels with numbers from "form" to 0, each is being hold on screen
	 * for 1000 ms Then adds label "START!"
	 * 
	 * autocorrects width according to screen width
	 * 
	 * @param from
	 */
	private void countdown(int from) {
		GLabel label;
		for (int i = from; i > 0; i--) {
			label = new GLabel("" + i);
			label.setFont("Arial-" + ((int) (0.375 * WIDTH)));
			label.setColor(Color.DARK_GRAY);
			add(label, WIDTH / 2 - (label.getWidth() / 2), 350);
			pause(1000);
			remove(label);
		}
		label = new GLabel("START!");
		label.setFont("Arial-" + ((int) (0.275 * WIDTH)));
		label.setColor(Color.red);
		add(label, WIDTH / 2 - (label.getWidth() / 2), 340);
		pause(500);
		remove(label);
	}

	/**
	 * According to task
	 * 
	 * @param rowNumber
	 * @return Color for bricks in row rowNumber
	 */
	private Color rowColor(int rowNumber) {
		if (rowNumber < NBRICK_ROWS / 5)
			return Color.red;
		if (rowNumber < NBRICK_ROWS * 2 / 5)
			return Color.orange;
		if (rowNumber < NBRICK_ROWS * 3 / 5)
			return Color.yellow;
		if (rowNumber < NBRICK_ROWS * 4 / 5)
			return Color.green;
		return Color.cyan;
	}

	/**
	 * Adds brick according to task
	 * 
	 * @param x
	 * @param y
	 * @param color
	 */
	private void addBrick(double x, double y, Color color) {
		GRect tempRect = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
		tempRect.setFilled(true);
		tempRect.setFillColor(color);
		tempRect.setColor(color);
		add(tempRect);
	}

	private RandomGenerator rgen = RandomGenerator.getInstance();

	private GRect paddle;
	private GOval ball;
	private int bricksRemaind, turnsRemaind = NTURNS + 1;
	private GLabel bricksRemaindLabel, attempRemaindLabel;

	private static final double BALLSTARTSPEED = 1;
	private static final double HORIZONTAL_BALLSPEED_PROECTION_DIVADER = 3;
	private static final double DELAY = 2;

	private boolean gameIsIn = true;

	private double ballSpeedX, ballSpeedY;

	public static void main(String[] args) {
		new Breakout().start(args);
	}
}
