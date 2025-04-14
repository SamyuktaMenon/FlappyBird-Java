import java.awt.*;
import java.util.Random;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Pipe {
    BufferedImage topPipeImage;
    BufferedImage bottomPipeImage;
    private boolean scored = false;
    
    public boolean isScored() {
        return scored;
    }
    public void setScored(boolean scored) {
        this.scored = scored;
    }

    private int x;
    private int width = 80;
    private int gap = 150;
    private int heightTop;
    private int heightBottom;
    private double speed = 2.5;
    private int screenHeight;
    private Random rand;

    public void update(int score, int pipeSpeed) {
        x -= pipeSpeed;
        if (x + width < 0) {
            x = 360;
            randomizeHeights();
        }
    }
    
    public Pipe(int x, int screenHeight) {
        try{
            topPipeImage = ImageIO.read(getClass().getResource("toppipe.png"));
            bottomPipeImage = ImageIO.read(getClass().getResource("bottompipe.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.x = x;
        this.screenHeight = screenHeight;
        rand = new Random();
        randomizeHeights();
    }

    private void randomizeHeights() {
        heightTop = rand.nextInt(screenHeight / 2);
        heightBottom = screenHeight - heightTop - gap;
    }

    public void update(int speed) {
        x -= speed;
    }

    public void reset(int newX) {
        scored = false;
        x = newX;
        randomizeHeights();
    }

    public void draw(Graphics g) {
        if (topPipeImage != null && bottomPipeImage != null) {
            g.drawImage(topPipeImage, x, 0, width, heightTop, null);
            g.drawImage(bottomPipeImage, x, heightTop+gap, width, heightBottom, null);
        } else {
            g.setColor(Color.GREEN);
            g.fillRect(x, 0, width, heightTop);
            g.fillRect(x, heightTop+gap, width, heightBottom);
        }
    }

    public Rectangle getTopBounds() {
        return new Rectangle(x, 0, width, heightTop);
    }

    public Rectangle getBottomBounds() {
        return new Rectangle(x, heightTop + gap, width, heightBottom);
    }

    public int getX() {
        return x;
    }

    public int getWidth() {
        return width;
    }
}