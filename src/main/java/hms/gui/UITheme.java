package hms.gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class UITheme {
    public static final Color PRIMARY = new Color(15, 118, 110);
    public static final Color PRIMARY_DARK = new Color(17, 94, 89);
    public static final Color PRIMARY_LIGHT = new Color(204, 251, 241);
    public static final Color BACKGROUND = new Color(244, 247, 249);
    public static final Color CARD = Color.WHITE;
    public static final Color BORDER = new Color(221, 227, 234);
    public static final Color TEXT = new Color(30, 41, 59);
    public static final Color TEXT_MUTED = new Color(100, 116, 139);
    public static final Color ERROR = new Color(185, 28, 28);

    private static final String FONT_NAME = pickFont();

    public static void apply() {
        UIManager.put("nimbusBase", PRIMARY_DARK);
        UIManager.put("nimbusBlueGrey", new Color(169, 186, 191));
        UIManager.put("control", BACKGROUND);
        UIManager.put("text", TEXT);
        UIManager.put("nimbusFocus", PRIMARY);
        UIManager.put("nimbusSelectionBackground", PRIMARY);
        UIManager.put("nimbusSelectedText", Color.WHITE);
        UIManager.put("Table.alternateRowColor", new Color(240, 246, 246));
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Nimbus look and feel not available, using default: " + e.getMessage());
        }
        UIManager.getLookAndFeelDefaults().put("defaultFont", font(Font.PLAIN, 14));
        UIManager.getLookAndFeelDefaults().put("Table.rowHeight", 28);
    }

    public static Font font(int style, int size) {
        return new Font(FONT_NAME, style, size);
    }

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(font(Font.BOLD, 22));
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel subtitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(font(Font.PLAIN, 14));
        label.setForeground(TEXT_MUTED);
        return label;
    }

    public static JButton primaryButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(font(Font.BOLD, 14));
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(PRIMARY_DARK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY);
            }
        });
        return button;
    }

    private static String pickFont() {
        String[] available = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String preferred : new String[] { "Segoe UI", "Helvetica Neue", "Arial" }) {
            if (Arrays.asList(available).contains(preferred)) {
                return preferred;
            }
        }
        return Font.SANS_SERIF;
    }

    private UITheme() {
    }
}
