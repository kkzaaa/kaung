package hms.gui;

import hms.model.Admin;
import hms.model.Doctor;
import hms.model.MedicalManager;
import hms.model.Patient;
import hms.model.User;
import hms.util.Constants;
import hms.util.FileHandler;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

public class HomePanel extends JPanel {
    private final User user;
    private final JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 16, 16));

    public HomePanel(User user) {
        this.user = user;
        setLayout(new GridBagLayout());
        setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.gridy = 0;
        add(UITheme.title("Welcome back, " + user.getFullName()), gbc);

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"));
        gbc.gridy = 1;
        gbc.insets = new Insets(4, 0, 24, 0);
        add(UITheme.subtitle(roleName() + " dashboard  |  " + today), gbc);

        cardsPanel.setOpaque(false);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 24, 0);
        add(cardsPanel, gbc);

        gbc.gridy = 3;
        gbc.weighty = 1;
        add(UITheme.subtitle("Use the menu on the left to get started."), gbc);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refresh();
            }
        });
        refresh();
    }

    private String roleName() {
        if (user instanceof Admin) {
            return "Admin";
        } else if (user instanceof MedicalManager) {
            return "Medical Manager";
        } else if (user instanceof Doctor) {
            return "Doctor";
        }
        return "Patient";
    }

    private void refresh() {
        cardsPanel.removeAll();
        String today = LocalDate.now().toString();

        if (user instanceof Admin) {
            addCard("Total users", count(Constants.USERS_FILE, r -> true));
            addCard("Doctors", count(Constants.DOCTORS_FILE, r -> true));
            addCard("Patients", count(Constants.PATIENTS_FILE, r -> true));
            addCard("Pending lab requests", count(Constants.LAB_REQUESTS_FILE, r -> r[4].equals(Constants.LAB_PENDING)));
        } else if (user instanceof MedicalManager) {
            addCard("Departments", count(Constants.DEPARTMENTS_FILE, r -> true));
            addCard("Doctors", count(Constants.DOCTORS_FILE, r -> true));
            addCard("Appointments", count(Constants.APPOINTMENTS_FILE, r -> true));
            double revenue = 0;
            for (String[] row : FileHandler.readRecords(Constants.BILLING_FILE)) {
                revenue += Double.parseDouble(row[3]);
            }
            addCard("Total revenue", String.format("RM %.2f", revenue));
        } else if (user instanceof Doctor doctor) {
            String id = doctor.getDoctorID();
            addCard("Today's appointments", count(Constants.APPOINTMENTS_FILE,
                    r -> r[2].equals(id) && r[3].equals(today) && !r[6].equals(Constants.STATUS_CANCELLED)));
            addCard("Open appointments", count(Constants.APPOINTMENTS_FILE,
                    r -> r[2].equals(id) && isOpen(r[6])));
            addCard("Pending lab requests", count(Constants.LAB_REQUESTS_FILE,
                    r -> r[1].equals(id) && !r[4].equals(Constants.LAB_COMPLETED)));
            addCard("My rating", averageRating(id));
        } else if (user instanceof Patient patient) {
            String id = patient.getPatientID();
            addCard("Upcoming appointments", count(Constants.APPOINTMENTS_FILE,
                    r -> r[1].equals(id) && isOpen(r[6])));
            addCard("Completed visits", count(Constants.APPOINTMENTS_FILE,
                    r -> r[1].equals(id) && r[6].equals(Constants.STATUS_COMPLETED)));
            addCard("Prescriptions", count(Constants.PRESCRIPTIONS_FILE, r -> r[2].equals(id)));
            addCard("Unpaid bills", count(Constants.BILLING_FILE, r -> r[1].equals(id) && r[6].equals("UNPAID")));
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private boolean isOpen(String status) {
        return status.equals(Constants.STATUS_BOOKED) || status.equals(Constants.STATUS_RESCHEDULED);
    }

    private String count(String file, Predicate<String[]> matches) {
        int total = 0;
        for (String[] row : FileHandler.readRecords(file)) {
            if (matches.test(row)) {
                total++;
            }
        }
        return String.valueOf(total);
    }

    private String averageRating(String doctorID) {
        int sum = 0;
        int n = 0;
        for (String[] row : FileHandler.readRecords(Constants.FEEDBACK_FILE)) {
            if (row[2].equals(doctorID)) {
                sum += Integer.parseInt(row[4]);
                n++;
            }
        }
        return n == 0 ? "-" : String.format("%.1f / 5", (double) sum / n);
    }

    private void addCard(String label, String value) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(UITheme.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, UITheme.PRIMARY),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UITheme.BORDER),
                        BorderFactory.createEmptyBorder(14, 14, 14, 14))));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(UITheme.font(Font.BOLD, 26));
        valueLabel.setForeground(UITheme.PRIMARY);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(UITheme.subtitle(label), BorderLayout.SOUTH);
        cardsPanel.add(card);
    }
}
