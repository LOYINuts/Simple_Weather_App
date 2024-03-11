import javax.swing.*;

/**
 * @author yushihang
 * @version 1.0
 */
public class AppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WeatherAppGui().setVisible(true);
            }
        });
    }
}
