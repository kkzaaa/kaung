package hms.gui.admin;

import hms.gui.HomePanel;
import hms.gui.ProfilePanel;
import hms.model.Admin;

import javax.swing.JTabbedPane;

public class AdminDashboard extends JTabbedPane {

    public AdminDashboard(Admin admin) {
        addTab("Home", new HomePanel(admin));
        addTab("Register User", new RegisterUserPanel());
        addTab("Manage Users", new UserManagementPanel());
        addTab("Assign Doctor to Manager", new AssignDoctorPanel());
        addTab("Hospital Assets", new AssetManagementPanel());
        addTab("Rates & Insurance", new RatesInsurancePanel());
        addTab("Billing", new BillingPanel());
        addTab("Lab Requests", new LabRequestsPanel());
        addTab("My Profile", new ProfilePanel(admin));
    }
}
