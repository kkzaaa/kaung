package hms;

import hms.gui.MainFrame;
import hms.gui.UITheme;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UITheme.apply();
            new MainFrame().setVisible(true);
        });
    }
}
