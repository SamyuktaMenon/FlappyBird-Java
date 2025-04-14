import java.awt.*;
import java.util.Random;

public class Plastic {
    private int x, y;
    private int width = 30;
    private int height = 30;
    private Random rand;
    
    public Plastic(int screenHeight) {
        rand = new Random();
        this.x = 360;
        this.y = rand.nextInt(screenHeight - 100);
    }

    public void update(int speed) {
        x -= speed;
    }

    public void draw(Graphics g) {
        g.setColor(Color.CYAN);
        g.fillRect(x, y, width, height); 
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isOffScreen() {
        return x + width < 0;
    }
}