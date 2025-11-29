package twitter.gui;

import twitter.service.TwitterService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtUserId = new JTextField();
    private JPasswordField txtPwd = new JPasswordField();

    public LoginFrame(TwitterService service) {

        setTitle("Twitter Login");
        setSize(350, 520);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel base = new JPanel();
        base.setLayout(new BoxLayout(base, BoxLayout.Y_AXIS));
        base.setBackground(Color.WHITE);
        base.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(base);

        // ======================================================
        //  Logo (48px)
        // ======================================================
        ImageIcon raw = new ImageIcon(getClass().getResource("/logo_twitter_48.png"));
        Image scaled = raw.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(scaled));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Log in on Twitter");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        base.add(logo);
        base.add(title);

        // ======================================================
        // Input fields
        // ======================================================
        txtUserId.setPreferredSize(new Dimension(280, 40));
        txtUserId.setMaximumSize(new Dimension(280, 40));
        txtUserId.setBorder(BorderFactory.createTitledBorder("User ID"));

        txtPwd.setPreferredSize(new Dimension(280, 40));
        txtPwd.setMaximumSize(new Dimension(280, 40));
        txtPwd.setBorder(BorderFactory.createTitledBorder("Password"));

        base.add(txtUserId);
        base.add(Box.createVerticalStrut(15));
        base.add(txtPwd);
        base.add(Box.createVerticalStrut(20));

        // ======================================================
        // Login button
        // ======================================================
        JButton loginBtn = new JButton("Log in");
        loginBtn.setPreferredSize(new Dimension(280, 42));
        loginBtn.setMaximumSize(new Dimension(280, 42));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setFocusPainted(false);
        loginBtn.setBackground(new Color(29, 161, 242));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.putClientProperty("JButton.buttonType", "roundRect");

        base.add(loginBtn);
        base.add(Box.createVerticalStrut(25));

        // ======================================================
        // Sign up link
        // ======================================================
        JLabel bottomText = new JLabel(
                "<html><center><font color='#1DA1F2'>Don't have an account? Sign up</font></center></html>"
        );
        bottomText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bottomText.setAlignmentX(Component.CENTER_ALIGNMENT);

        bottomText.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Sign up 클릭 → 회원가입 화면으로 이동
        bottomText.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new SignupFrame(service).setVisible(true);
                dispose();
            }
        });

        base.add(bottomText);

        // ======================================================
        // Login Action
        // ======================================================
        loginBtn.addActionListener(e -> {
            if (service.login(txtUserId.getText(), new String(txtPwd.getPassword()))) {
                new MainFrame(service, txtUserId.getText()).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Login failed");
            }
        });
        loginBtn.putClientProperty("keepColor", true);
        ThemeManager.applyTheme(getContentPane());
        
        // =========================
        // Dark Mode 버튼 추가
        // =========================
        JButton darkBtn = new JButton(ThemeManager.isDark ? "Light" : "Dark");
        darkBtn.setFocusPainted(false);
        darkBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        darkBtn.setMargin(new Insets(2, 8, 2, 8));
        darkBtn.setBackground(new Color(240,240,240));
        darkBtn.setForeground(Color.BLACK);
        darkBtn.putClientProperty("keepColor", true);
        
        if (ThemeManager.isDark) {
            darkBtn.setBackground(Color.BLACK);
            darkBtn.setForeground(Color.WHITE);
        } else {
            darkBtn.setBackground(Color.WHITE);
            darkBtn.setForeground(Color.BLACK);
        }

        
        darkBtn.addActionListener(e -> {
            ThemeManager.isDark = !ThemeManager.isDark;
            ThemeManager.applyTheme(getContentPane());
            darkBtn.setText(ThemeManager.isDark ? "Light" : "Dark");
        });

        // ------------------------------
        // Wrapper Layout
        // ------------------------------
        JPanel wrapper = new JPanel(new BorderLayout());

        // 상단 바 생성 (Dark 버튼을 왼쪽 위에만 배치)
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        topBar.setBackground(Color.WHITE);
        topBar.add(darkBtn);

        // 구역 배치
        wrapper.add(topBar, BorderLayout.NORTH);   // 왼쪽 상단 구석
        wrapper.add(base, BorderLayout.CENTER);

        add(wrapper);
    }
}
