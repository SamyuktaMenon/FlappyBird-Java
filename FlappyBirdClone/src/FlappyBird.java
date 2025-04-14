import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


public class FlappyBird extends JPanel implements KeyListener {
    boolean gameStarted = false;
    float fadeAlpha = 1.0f;
    boolean fadingIn = false;
    long lastTime = System.nanoTime();
    final double nsPerFrame = 1000000000.0 / 60.0;
    BufferedImage birdUp, birdMid, birdDown;
    BufferedImage currentBirdImage;
    int flapFrame = 0;
    int flapCounter = 0;
    BufferedImage backgroundImage;
    BufferedImage birdImage;
    int score=0;
    int highScore = 0;
    Font scoreFont=new Font("Arial",Font.BOLD,32);
    int birdX=200;
    int birdY=250;
    int birdSize=30;
    int gravity=1;
    int jumpStrength=-10;
    int velocity=0;
    int floorHeight=600-birdSize;

    ArrayList<Plastic> plastics = new ArrayList<>();
    int plasticSpawnRate = 200;
    int plasticHitCount = 0;  

    private void spawnPlastic() {
        if (Math.random() < 0.01) { 
            plastics.add(new Plastic(640));
        }
    }

    private void checkPlasticCollisions() {
        for (Plastic plastic : plastics) {
            if (plastic.getBounds().intersects(new Rectangle(birdX, birdY, birdSize, birdSize))) {
                plasticHitCount++;
                plastics.remove(plastic);
                playSound("hitplastic.wav");
                if (plasticHitCount == 3) {
                    gameOver = true;
                    playSound("gameover.wav");
                    System.out.println("Game Over! Hit plastic 3 times.");
                }
                break;
            }
        }
    }

    private void updatePlastics() {
        for (int i = 0; i < plastics.size(); i++) {
            Plastic plastic = plastics.get(i);
            plastic.update(pipeSpeed); 
            if (plastic.isOffScreen()) {
                plastics.remove(i); 
                i--; 
            }
        }
    }


    ArrayList<Pipe> pipes;
    int pipeSpacing = 200;
    int pipeStartX = 400;

    Timer timer;
    boolean gameOver=false;

    String difficulty = "Medium";
        
    int pipeSpeed = 4;
    
    private void setDifficulty(String difficulty) {
        switch(difficulty) {
            case "Easy":
            pipeSpeed = 2;
            pipeSpacing = 250;
            break;
            case "Medium":
            pipeSpeed = 4; 
            pipeSpacing = 200; 
            break;
            case "Hard":
            pipeSpeed = 6;
            pipeSpacing = 150; 
            break;
            default:
            pipeSpeed = 4;
            break;
        }
    }    

    public FlappyBird(){
        try {
            birdUp = ImageIO.read(getClass().getResource("yellowbird-upflap.png"));
            birdMid = ImageIO.read(getClass().getResource("yellowbird-midflap.png"));
            birdDown = ImageIO.read(getClass().getResource("yellowbird-downflap.png"));
            currentBirdImage = birdMid;
        } catch (IOException e) {
            e.printStackTrace();
        }        
        try {
            backgroundImage = ImageIO.read(getClass().getResource("flappybirdbg.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            birdImage = ImageIO.read(getClass().getResource("flappybird.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pipes = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            pipes.add(new Pipe(pipeStartX + i * pipeSpacing, 640));
        }

        timer=new Timer(1000/60, e ->{
            long now = System.nanoTime();
            double delta = (now - lastTime) / nsPerFrame;
            lastTime = now;

            if (gameOver) return;
            velocity += gravity;
            if (velocity > 15) velocity = 15;
            birdY += velocity;

            spawnPlastic();
            updatePlastics();
            checkPlasticCollisions();

            if (birdY >= floorHeight) {
                birdY = floorHeight;
                velocity = 0;
                if (score > highScore) {
                    highScore = score;
                }
                gameOver = true;
                timer.stop();  
                System.out.println("Game Over! Bird hit the ground.");
            }          

            if (birdY<0){
                birdY=0;
                velocity=0;
            }

            if (birdY>floorHeight){
                birdY=floorHeight;
                velocity=0;
            }

            for (Pipe pipe : pipes) {
                pipe.update(pipeSpeed);
            }

            for (Pipe pipe : pipes) {
                if (!pipe.isScored() && pipe.getX() + pipe.getWidth() < birdX) {
                    score++;
                    pipe.setScored(true);
                    playSound("point.wav");
                    System.out.println("Score: " + score);
                }
            }            

            for (Pipe pipe : pipes) {
                if (pipe.getTopBounds().intersects(new Rectangle(birdX, birdY, birdSize, birdSize)) ||
                    pipe.getBottomBounds().intersects(new Rectangle(birdX, birdY, birdSize, birdSize))) {
                    System.out.println("Game Over!");
                    playSound("hit.wav");    
                    if (score > highScore) {
                        highScore = score;
                    }
                    gameOver=true;
                    timer.stop();
                }
            }

            Pipe firstPipe = pipes.get(0);
            if (firstPipe.getX() + firstPipe.getWidth() < 0) {
                pipes.remove(0);
                int lastX = pipes.get(pipes.size() - 1).getX();
                pipes.add(new Pipe(lastX + pipeSpacing, 640));
            }

            if (fadingIn) {
                fadeAlpha -= 0.05f;
                if (fadeAlpha <= 0) {
                    fadeAlpha = 0;
                    fadingIn = false;
                }
            }

            setDifficulty(difficulty);
            
            flapCounter++;
            if (flapCounter >= 5) {
                flapCounter = 0;
                flapFrame = (flapFrame + 1) % 3;
                switch (flapFrame) {
                    case 0: currentBirdImage = birdUp; break;
                    case 1: currentBirdImage = birdMid; break;
                    case 2: currentBirdImage = birdDown; break;
                }
            }


            repaint(); 
        });

        setFocusable(true);
        addKeyListener(this);
    }

    private void playSound(String fileName) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource(fileName));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    

    @Override 
    public void paintComponent (Graphics g){
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!gameStarted) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            g.drawString("Press 1 for Easy", 100, 200);
            g.drawString("Press 2 for Medium", 100, 250);
            g.drawString("Press 3 for Hard", 100, 300);
            return;
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        g.drawString("Difficulty: " + difficulty, 10, 40);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(Color.CYAN);
            g.fillRect(0, 0, 360, 640);
        }
        if (birdImage != null) {
            g.drawImage(currentBirdImage, birdX, birdY, birdSize, birdSize, null);
        } else {
            g.setColor(Color.YELLOW);
            g.fillRect(birdX, birdY, birdSize, birdSize);
        }

        for (Pipe pipe : pipes) {
            pipe.draw(g);
        }

        for (Plastic plastic : plastics) {
            plastic.draw(g);
        } 

        g.setColor(Color.WHITE);
        g.setFont(scoreFont);
        g.drawString("Score: " + score, 10, 40);
        g.drawString("Plastic Hits: " + plasticHitCount + " / 3", 10, 70);

        if (gameOver) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            String gameOverText = "Game Over!";
            int gameOverWidth = g.getFontMetrics().stringWidth(gameOverText);
            g.drawString(gameOverText, (getWidth() - gameOverWidth) / 2, 250);  
        
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            int lineSpacing = 40;
        
            int yPosition = 290;
            
            String message = plasticHitCount >= 3 ? "Bird ate too much plastic!" : "You hit a pipe or the ground!";
            int messageWidth = g.getFontMetrics().stringWidth(message);
            g.drawString(message, (getWidth() - messageWidth) / 2, yPosition);
            yPosition += lineSpacing;
        
            String restartText = "Press 'R' to restart";
            int restartWidth = g.getFontMetrics().stringWidth(restartText);
            if (yPosition + lineSpacing < getHeight()) {
                g.drawString(restartText, (getWidth() - restartWidth) / 2, yPosition);
                yPosition += lineSpacing;
            }
            
            String highScoreText = "High Score: " + highScore;
            int highScoreWidth = g.getFontMetrics().stringWidth(highScoreText);
            g.drawString(highScoreText, (getWidth() - highScoreWidth) / 2, yPosition);
        }        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_1) {
            difficulty = "Easy";
            setDifficulty(difficulty);
            gameStarted = true;
            if (!timer.isRunning()) timer.start();
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_2) {
            difficulty = "Medium";
            setDifficulty(difficulty);
            gameStarted = true;
            if (!timer.isRunning()) timer.start();
            repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_3) {
            difficulty = "Hard";
            setDifficulty(difficulty);
            gameStarted = true;
            if (!timer.isRunning()) timer.start();
            repaint();
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE && gameStarted && !gameOver) {
            velocity = jumpStrength;
            playSound("flap.wav");
        } else if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
            restartGame();
        }
    }
 
    @Override
    public void keyReleased(KeyEvent e){}

    @Override
    public void keyTyped(KeyEvent e){}

    private void restartGame() {
        score = 0;
        plasticHitCount = 0;
        birdY = 250;
        velocity = 0;
        pipes.clear();
        fadeAlpha = 1.0f;
        fadingIn = true;

        for (int i = 0; i < 3; i++) {
            pipes.add(new Pipe(pipeStartX + i * pipeSpacing, 640));
        }

        gameOver = false;
        gameStarted=false;
        lastTime = System.nanoTime();
        timer.stop();
        repaint();
    }
    public static void main(String[] args){
        JFrame frame = new JFrame("Flappy Bird Clone");
        FlappyBird panel = new FlappyBird();
        frame.add(panel);
        frame.setSize(360, 640);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        panel.requestFocus();
    }
}