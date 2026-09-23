package hms.gui;

import hms.gui.admin.AdminDashboard;
import hms.gui.doctor.DoctorDashboard;
import hms.gui.manager.ManagerDashboard;
import hms.gui.patient.PatientDashboard;
import hms.model.Admin;
import hms.model.Doctor;
import hms.model.MedicalManager;
import hms.model.Patient;
import hms.model.User;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame {
    private static final String TITLE = "Hospital Management System";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final LoginForm loginForm;
    private JPanel currentDashboard;
    private MenuItem selectedMenuItem;

    public MainFrame() {
        super(TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);

        loginForm = new LoginForm(this);
        cardPanel.add(loginForm, "LOGIN");
        add(cardPanel, BorderLayout.CENTER);
        cardLayout.show(cardPanel, "LOGIN");
    }

    public void openDashboardFor(User user) {
        user.showDashboard();
        JTabbedPane dashboard;
        if (user instanceof Admin admin) {
            dashboard = new AdminDashboard(admin);
        } else if (user instanceof MedicalManager manager) {
            dashboard = new ManagerDashboard(manager);
        } else if (user instanceof Doctor doctor) {
            dashboard = new DoctorDashboard(doctor);
        } else if (user instanceof Patient patient) {
            dashboard = new PatientDashboard(patient);
        } else {
            return;
        }

        currentDashboard = new JPanel(new BorderLayout());
        currentDashboard.add(buildHeader(user), BorderLayout.NORTH);
        currentDashboard.add(buildBody(dashboard), BorderLayout.CENTER);

        cardPanel.add(currentDashboard, "DASHBOARD");
        cardLayout.show(cardPanel, "DASHBOARD");
        setTitle(TITLE + " - " + user.getFullName() + " (" + user.getRole() + ")");
    }

    private JPanel buildHeader(User user) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel appName = new JLabel("+  Hospital Management System");
        appName.setFont(UITheme.font(Font.BOLD, 18));
        appName.setForeground(Color.WHITE);
        header.add(appName, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        JLabel nameLabel = new JLabel(user.getFullName());
        nameLabel.setFont(UITheme.font(Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);
        right.add(nameLabel);

        JLabel roleLabel = new JLabel(user.getRole());
        roleLabel.setFont(UITheme.font(Font.BOLD, 11));
        roleLabel.setForeground(UITheme.PRIMARY_DARK);
        roleLabel.setOpaque(true);
        roleLabel.setBackground(UITheme.PRIMARY_LIGHT);
        roleLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        right.add(roleLabel);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        right.add(logoutButton);

        header.add(right, BorderLayout.EAST);
        return header;
    }

    // Turns the dashboard's tabs into a side menu + page area, so each dashboard stays a simple list of addTab calls.
    private JPanel buildBody(JTabbedPane tabs) {
        CardLayout pageLayout = new CardLayout();
        JPanel pages = new JPanel(pageLayout);
        pages.setBackground(UITheme.BACKGROUND);

        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(Color.WHITE);
        menu.setPreferredSize(new Dimension(260, 0));
        menu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, UITheme.BORDER),
                BorderFactory.createEmptyBorder(16, 0, 16, 0)));

        JLabel menuTitle = new JLabel("MENU");
        menuTitle.setFont(UITheme.font(Font.BOLD, 11));
        menuTitle.setForeground(UITheme.TEXT_MUTED);
        menuTitle.setBorder(BorderFactory.createEmptyBorder(0, 24, 10, 0));
        menu.add(menuTitle);

        MenuItem first = null;
        while (tabs.getTabCount() > 0) {
            String title = tabs.getTitleAt(0);
            Component page = tabs.getComponentAt(0);
            tabs.removeTabAt(0);
            pages.add(wrapPage(title, page), title);

            MenuItem item = new MenuItem(title);
            item.addActionListener(e -> {
                pageLayout.show(pages, title);
                selectMenuItem(item);
            });
            menu.add(item);
            if (first == null) {
                first = item;
            }
        }
        menu.add(Box.createVerticalGlue());
        selectMenuItem(first);

        JPanel body = new JPanel(new BorderLayout());
        body.add(menu, BorderLayout.WEST);
        body.add(pages, BorderLayout.CENTER);
        return body;
    }

    private Component wrapPage(String title, Component page) {
        if (page instanceof HomePanel home) {
            home.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
            return home;
        }

        Component content = page;
        if (page instanceof Container c && c.getLayout() instanceof GridBagLayout) {
            JPanel leftAligned = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            leftAligned.add(page);
            JPanel top = new JPanel(new BorderLayout());
            top.add(leftAligned, BorderLayout.NORTH);
            content = top;
        }

        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        card.add(content, BorderLayout.CENTER);
        paintBackground(card, UITheme.CARD);

        JPanel wrapper = new JPanel(new BorderLayout(0, 16));
        wrapper.setBackground(UITheme.BACKGROUND);
        wrapper.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        wrapper.add(UITheme.title(title), BorderLayout.NORTH);
        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    private void paintBackground(Component c, Color color) {
        if (c instanceof JPanel || c instanceof JViewport) {
            c.setBackground(color);
        }
        if (c instanceof Container container) {
            for (Component child : container.getComponents()) {
                paintBackground(child, color);
            }
        }
    }

    private void selectMenuItem(MenuItem item) {
        if (selectedMenuItem != null) {
            selectedMenuItem.setForeground(UITheme.TEXT);
            selectedMenuItem.setFont(UITheme.font(Font.PLAIN, 14));
        }
        selectedMenuItem = item;
        item.setForeground(UITheme.PRIMARY_DARK);
        item.setFont(UITheme.font(Font.BOLD, 14));
    }

    public void logout() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?",
                "Logout", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        if (currentDashboard != null) {
            cardPanel.remove(currentDashboard);
            currentDashboard = null;
        }
        loginForm.clearFields();
        cardLayout.show(cardPanel, "LOGIN");
        setTitle(TITLE);
    }

    private class MenuItem extends JButton {
        private boolean hover;

        MenuItem(String text) {
            super(text);
            setHorizontalAlignment(LEFT);
            setFont(UITheme.font(Font.PLAIN, 14));
            setForeground(UITheme.TEXT);
            setBorder(BorderFactory.createEmptyBorder(11, 24, 11, 16));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setAlignmentX(LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            boolean selected = this == selectedMenuItem;
            if (selected) {
                g.setColor(UITheme.PRIMARY_LIGHT);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(UITheme.PRIMARY);
                g.fillRect(0, 0, 4, getHeight());
            } else if (hover) {
                g.setColor(UITheme.BACKGROUND);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            super.paintComponent(g);
        }
    }
}
