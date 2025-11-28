package twitter.gui;

import twitter.service.TwitterService;
import javax.swing.*;
import java.awt.*;

public class ChangePasswordDialog extends JDialog {

    public ChangePasswordDialog(Frame owner, TwitterService service, String userId) {
        super(owner, "Change Password", true);
        setSize(350, 200);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPasswordField txtNewPwd = new JPasswordField();
        txtNewPwd.setBorder(BorderFactory.createTitledBorder("New Password"));
        
        JButton btnChange = new JButton("Change");
        btnChange.addActionListener(e -> {
            String newPwd = new String(txtNewPwd.getPassword());
            if(newPwd.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter new password.");
                return;
            }
            
            if(service.changePassword(userId, newPwd)) {
                JOptionPane.showMessageDialog(this, "Password changed successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to change password.");
            }
        });
        
        panel.add(txtNewPwd);
        panel.add(btnChange);
        
        add(panel, BorderLayout.CENTER);
    }
}