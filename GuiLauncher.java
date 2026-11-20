import database.DatabaseManager;
import gui.MainWindow;
import javax.swing.SwingUtilities;

public class GuiLauncher {
    public static void main(String[] args) {
        DatabaseManager.getInstance();
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
