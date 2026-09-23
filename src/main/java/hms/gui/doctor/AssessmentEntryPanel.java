package hms.gui.doctor;

import hms.gui.UITheme;
import hms.model.Assessment;
import hms.model.AssessmentType;
import hms.model.Doctor;
import hms.service.DoctorService;
import hms.util.Constants;
import hms.util.FileHandler;
import hms.util.IdGenerator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class AssessmentEntryPanel extends JPanel {
    private final DoctorService doctorService = new DoctorService();
    private final Doctor doctor;

    private final JComboBox<String> apptBox = new JComboBox<>();
    private final JComboBox<String> typeBox = new JComboBox<>();
    private final CardLayout vitalsLayout = new CardLayout();
    private final JPanel vitalsPanel = new JPanel(vitalsLayout);
    private final Map<String, List<JTextField>> vitalsFieldsByType = new HashMap<>();
    private final Map<String, AssessmentType> typesByID = new HashMap<>();
    private final JTextArea labResultsArea = new JTextArea(3, 20);
    private final JTextArea notesArea = new JTextArea(3, 20);

    public AssessmentEntryPanel(Doctor doctor) {
        this.doctor = doctor;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Appointment"), gbc);
        gbc.gridx = 1;
        add(apptBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Assessment type"), gbc);
        gbc.gridx = 1;
        add(typeBox, gbc);

        loadAssessmentTypes();

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(vitalsPanel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Lab results"), gbc);
        gbc.gridx = 1;
        add(new JScrollPane(labResultsArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Notes"), gbc);
        gbc.gridx = 1;
        add(new JScrollPane(notesArea), gbc);

        JButton saveButton = UITheme.primaryButton("Save assessment");
        JButton refreshButton = new JButton("Refresh appointments");
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(refreshButton, gbc);
        gbc.gridx = 1;
        add(saveButton, gbc);

        typeBox.addActionListener(e -> vitalsLayout.show(vitalsPanel, (String) typeBox.getSelectedItem()));
        refreshButton.addActionListener(e -> loadAppointments());
        saveButton.addActionListener(e -> saveAssessment());

        loadAppointments();
    }

    private void loadAssessmentTypes() {
        Vector<String> typeIDs = new Vector<>();
        for (String[] row : FileHandler.readRecords(Constants.ASSESSMENT_TYPES_FILE)) {
            AssessmentType type = AssessmentType.fromFields(row);
            typesByID.put(type.getTypeID(), type);
            typeIDs.add(type.getTypeID());

            JPanel fieldsPanel = new JPanel(new GridBagLayout());
            List<JTextField> fields = new ArrayList<>();
            GridBagConstraints fgbc = new GridBagConstraints();
            fgbc.insets = new Insets(2, 2, 2, 2);
            int r = 0;
            for (String requiredField : type.getRequiredFields()) {
                JTextField field = new JTextField(10);
                fields.add(field);
                fgbc.gridx = 0;
                fgbc.gridy = r;
                fieldsPanel.add(new JLabel(requiredField + ":"), fgbc);
                fgbc.gridx = 1;
                fieldsPanel.add(field, fgbc);
                r++;
            }
            vitalsFieldsByType.put(type.getTypeID(), fields);
            vitalsPanel.add(fieldsPanel, type.getTypeID());
        }
        typeBox.setModel(new DefaultComboBoxModel<>(typeIDs));
    }

    private void loadAppointments() {
        Vector<String> apptIDs = new Vector<>();
        for (String[] row : FileHandler.readRecords(Constants.APPOINTMENTS_FILE)) {
            boolean ownedByDoctor = row[2].equals(doctor.getDoctorID());
            boolean recordable = !row[6].equals(Constants.STATUS_CANCELLED);
            if (ownedByDoctor && recordable) {
                apptIDs.add(row[0]);
            }
        }
        apptBox.setModel(new DefaultComboBoxModel<>(apptIDs));
    }

    private void saveAssessment() {
        String apptID = (String) apptBox.getSelectedItem();
        String typeID = (String) typeBox.getSelectedItem();
        if (apptID == null || typeID == null) {
            JOptionPane.showMessageDialog(this, "No recordable appointment or assessment type available.");
            return;
        }

        AssessmentType type = typesByID.get(typeID);
        List<JTextField> fields = vitalsFieldsByType.get(typeID);
        StringBuilder vitals = new StringBuilder();
        for (int i = 0; i < type.getRequiredFields().size(); i++) {
            String value = fields.get(i).getText().trim();
            if (value.isBlank()) {
                JOptionPane.showMessageDialog(this, "Please fill in " + type.getRequiredFields().get(i) + ".");
                return;
            }
            if (vitals.length() > 0) {
                vitals.append(",");
            }
            vitals.append(type.getRequiredFields().get(i)).append(":").append(value);
        }

        String patientID = findPatientForAppointment(apptID);
        String assessmentID = IdGenerator.nextId(Constants.ASSESSMENTS_FILE, "AS");
        Assessment assessment = new Assessment(assessmentID, apptID, typeID, doctor.getDoctorID(), patientID,
                vitals.toString(), labResultsArea.getText().trim(), notesArea.getText().trim(),
                LocalDate.now().toString());
        doctorService.recordAssessment(assessment);

        JOptionPane.showMessageDialog(this, "Assessment " + assessmentID + " saved.");
        for (JTextField field : fields) {
            field.setText("");
        }
        labResultsArea.setText("");
        notesArea.setText("");
    }

    private String findPatientForAppointment(String apptID) {
        for (String[] row : FileHandler.readRecords(Constants.APPOINTMENTS_FILE)) {
            if (row[0].equals(apptID)) {
                return row[1];
            }
        }
        return "";
    }
}
