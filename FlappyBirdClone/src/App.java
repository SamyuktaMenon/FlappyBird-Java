import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Flappy Bird Clone");
        FlappyBird panel=new FlappyBird();
        frame.add(panel);
        frame.setSize(360, 640);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        SwingUtilities.invokeLater(() -> panel.requestFocusInWindow());
    }
}