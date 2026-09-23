package hms.gui.manager;

import hms.gui.HomePanel;
import hms.gui.ProfilePanel;
import hms.model.MedicalManager;

import javax.swing.JTabbedPane;

public class ManagerDashboard extends JTabbedPane {

    public ManagerDashboard(MedicalManager manager) {
        addTab("Home", new HomePanel(manager));
        addTab("Departments", new DepartmentPanel());
        addTab("Assessment Types", new AssessmentTypePanel());
        addTab("Shift Roster", new RosterPanel());
        addTab("Reports", new ReportsPanel());
        addTab("My Profile", new ProfilePanel(manager));
    }
}
