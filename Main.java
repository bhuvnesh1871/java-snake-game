
import javax.swing.JFrame;
import java.util.Scanner;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;
import java.util.Random;
import java.io.File;
import java.io.FileWriter;
// import java.util.Scanner;

class GameFrame extends JFrame {

    public GameFrame(String UserName) {

        this.add(new GamePanel(UserName));
        this.setTitle("Snake Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false); // prevents user from re-sizing
        this.pack(); // sizes the frame so that all its contents are at or above their preferred
                     // sizes
        this.setVisible(true);

    }
}

class GamePanel extends JPanel implements ActionListener { // jPanel - simple container class interface

    final int SCREEN_WIDTH = 1100;
    final int SCREEN_HEIGHT = 700;
    final int UNIT_SIZE = 50;
    final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    int DELAY = 125;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 3;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    boolean play = false;
    String UserName;
    Timer timer;
    Random random;

    GamePanel(String UserName) {
        random = new Random();
        this.UserName = UserName;
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true); // indicates whether a component can gain the focus if it is requested to do so
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {

        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();

    }

    public void paintComponent(Graphics g) { // Graphics class is the abstract base class for all graphics contexts
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        if (running) {

            g.setColor(Color.green);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.red);
                } else {
                    g.setColor(Color.white);
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2,
                    g.getFont().getSize());
        } else {
            gameOver(g);
        }

    }

    public void newApple() {
        appleX = random.nextInt((SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }

    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }
        if (x[0] < 0) {
            running = false;
        }
        if (x[0] > SCREEN_WIDTH - UNIT_SIZE) {
            running = false;
        }
        if (y[0] < 0) {
            running = false;
        }
        if (y[0] > SCREEN_HEIGHT - UNIT_SIZE) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {

        try {
            File ScoreFile = new File("ScoreBord.txt");
            if (ScoreFile.createNewFile()) {
                FileWriter exceptionWriter = new FileWriter("ScoreBord.txt");
                exceptionWriter.write("0\n");
                exceptionWriter.write("Nobody");
                exceptionWriter.close();
            }
            try (Scanner myReader = new Scanner(ScoreFile)) {
                int previousHighScore = Integer.parseInt(myReader.nextLine());
                String HighScoreHolder = myReader.nextLine();
                if (previousHighScore < applesEaten)
                    HighScoreHolder = UserName;
                int HighScore = Math.max(previousHighScore, applesEaten);

                g.setColor(Color.blue);
                g.setFont(new Font("Arial", Font.BOLD, 75));
                FontMetrics metrics1 = getFontMetrics(g.getFont());
                g.drawString("High Score:" + HighScoreHolder + " - " + HighScore,
                        (SCREEN_WIDTH - metrics1.stringWidth("High Score:" + HighScoreHolder + " - " + HighScore)) / 2,
                        75);
                try {
                    FileWriter myWriter = new FileWriter("ScoreBord.txt");
                    myWriter.write(HighScore + "\n");
                    myWriter.write(HighScoreHolder);
                    myWriter.close();
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                }
            } catch (NumberFormatException e) {

                e.printStackTrace();
            }
        }

        catch (IOException e) {
            System.out.println("error");
        }

        g.setColor(Color.blue);
        g.setFont(new Font("Arial", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 2, 150);
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, 325);
        g.drawString("Press \"enter\" to play again",
                (SCREEN_WIDTH - metrics2.stringWidth("Press \"enter\" to play again")) / 2, 500);
        g.drawString("Press \"esc\" to quit", (SCREEN_WIDTH - metrics2.stringWidth("Press \"esc\" to quit")) / 2, 600);
    }

    public void actionPerformed(ActionEvent e) { // method of ActionListener

        if (running && play) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter { // abstract adapter class for receiving keyboard events

        public void keyPressed(KeyEvent e) { // polymorphism
            switch (e.getKeyCode()) {
                case KeyEvent.VK_SPACE:
                    play = !play;
                    break;
                case KeyEvent.VK_LEFT:
                    if (play) {
                        if (direction != 'R') {
                            direction = 'L';
                        }
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (play) {
                        if (direction != 'L') {
                            direction = 'R';
                        }
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (play) {
                        if (direction != 'D') {
                            direction = 'U';
                        }
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (play) {
                        if (direction != 'U') {
                            direction = 'D';
                        }
                    }
                    break;
                case KeyEvent.VK_ENTER:
                    if (!running) {
                        setVisible(false);
                        new GameFrame(UserName);
                    }
                    break;
                case KeyEvent.VK_ESCAPE:
                    if (!running) {
                        System.exit(0);
                    }
            }
        }
    }
}

class Main {

    public static void main(String[] args) {
        System.out.println("Enter name :");
        Scanner sc = new Scanner(System.in);
        String UserName = sc.nextLine();
        new GameFrame(UserName);
        sc.close();

    }
}
