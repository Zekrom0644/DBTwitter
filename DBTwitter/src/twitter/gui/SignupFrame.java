package twitter.gui;

import twitter.service.TwitterService;

import javax.swing.*;
import java.awt.*;

public class SignupFrame extends JFrame {

    private JTextField txtUserId = new JTextField();
    private JPasswordField txtPwd = new JPasswordField();
    private JPasswordField txtPwd2 = new JPasswordField();

    public SignupFrame(TwitterService service) {

        setTitle("Sign up for Twitter");
        setSize(350, 560);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel base = new JPanel();
        base.setLayout(new BoxLayout(base, BoxLayout.Y_AXIS));
        base.setBackground(Color.WHITE);
        base.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(base);

        // ------------------------------------------------------
        // Logo & Title
        // ------------------------------------------------------
        ImageIcon raw = new ImageIcon(getClass().getResource("/logo_twitter_48.png"));
        Image scaled = raw.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(scaled));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Create your account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        base.add(logo);
        base.add(title);

        // ------------------------------------------------------
        // Input fields
        // ------------------------------------------------------
        txtUserId.setPreferredSize(new Dimension(280, 40));
        txtUserId.setMaximumSize(new Dimension(280, 40));
        txtUserId.setBorder(BorderFactory.createTitledBorder("User ID"));

        txtPwd.setPreferredSize(new Dimension(280, 40));
        txtPwd.setMaximumSize(new Dimension(280, 40));
        txtPwd.setBorder(BorderFactory.createTitledBorder("Password"));

        txtPwd2.setPreferredSize(new Dimension(280, 40));
        txtPwd2.setMaximumSize(new Dimension(280, 40));
        txtPwd2.setBorder(BorderFactory.createTitledBorder("Confirm Password"));

        base.add(txtUserId);
        base.add(Box.createVerticalStrut(15));
        base.add(txtPwd);
        base.add(Box.createVerticalStrut(15));
        base.add(txtPwd2);
        base.add(Box.createVerticalStrut(25));

        // ------------------------------------------------------
        // Sign up button
        // ------------------------------------------------------
        JButton signupBtn = new JButton("Sign up");
        signupBtn.setPreferredSize(new Dimension(280, 42));
        signupBtn.setMaximumSize(new Dimension(280, 42));
        signupBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupBtn.setBackground(new Color(29, 161, 242));
        signupBtn.setForeground(Color.WHITE);
        signupBtn.setFocusPainted(false);
        signupBtn.putClientProperty("JButton.buttonType", "roundRect");
        signupBtn.putClientProperty("keepColor", true);
        signupBtn.setOpaque(true);
        
        base.add(signupBtn);
        base.add(Box.createVerticalStrut(25));

        // ------------------------------------------------------
        // Back to login
        // ------------------------------------------------------
        JLabel back = new JLabel(
                "<html><center><font color='#1DA1F2'>Back to login</font></center></html>"
        );
        back.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));

        back.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new LoginFrame(service).setVisible(true);
                dispose();
            }
        });

        base.add(back);

        // ------------------------------------------------------
        // Sign up action
        // ------------------------------------------------------
        signupBtn.addActionListener(e -> {

            String id = txtUserId.getText();
            String pwd = new String(txtPwd.getPassword());
            String pwd2 = new String(txtPwd2.getPassword());

            if (id.isEmpty() || pwd.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }
            if (!pwd.equals(pwd2)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.");
                return;
            }

            if (service.signup(id, pwd)) {
                JOptionPane.showMessageDialog(this, "Sign up successful.");
                new LoginFrame(service).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "User already exists.");
            }
        });
        
        ThemeManager.applyTheme(getContentPane());
    }
}

