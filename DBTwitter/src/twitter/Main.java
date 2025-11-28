package twitter;

import javax.swing.SwingUtilities;
import com.formdev.flatlaf.FlatLightLaf;
import twitter.gui.LoginFrame;
import twitter.service.TwitterService;

/**
 * Entry point for the Twitter GUI client.
 * NOTE: You must add flatlaf-x.x.jar to the project classpath.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup(); // modern flat UI
            TwitterService service = new TwitterService();
            new LoginFrame(service).setVisible(true);
        });
    }
}
