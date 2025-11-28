package twitter.gui;

import twitter.service.TwitterService;

import javax.swing.*;
import java.awt.*;

public class WritePostDialog extends JDialog {

    private final TwitterService service;
    private final String userId;

    public WritePostDialog(JFrame parent, TwitterService service, String userId) {
        super(parent, "Write Tweet", true);
        this.service = service;
        this.userId = userId;

        setSize(450, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        initUI();
    }

    private void initUI() {
        /* ---------------------- 상단 제목 ---------------------- */
        JLabel title = new JLabel("Write a Tweet", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        add(title, BorderLayout.NORTH);

        /* ---------------------- 텍스트 입력창 ---------------------- */
        JTextArea txtArea = new JTextArea();
        txtArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtArea.setLineWrap(true);
        txtArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(txtArea);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        add(scroll, BorderLayout.CENTER);

        /* ---------------------- 하단 버튼 ---------------------- */
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(Color.WHITE);

        JButton btnTweet = new JButton("Tweet");
        btnTweet.setPreferredSize(new Dimension(100, 36));
        btnTweet.setBackground(new Color(29,161,242));
        btnTweet.setForeground(Color.WHITE);
        btnTweet.putClientProperty("JButton.buttonType", "roundRect");

        btnTweet.addActionListener(e -> {
            String content = txtArea.getText().trim();
            if (content.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Tweet cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

         // 1. service.writePost의 결과를 변수(isSuccess)에 담습니다.
            boolean isSuccess = service.writePost(userId, content);

            // 2. 결과가 참(true)일 때만 성공 메시지를 띄웁니다.
            if (isSuccess) {
                JOptionPane.showMessageDialog(this, "Tweet posted!");
                dispose(); // 창 닫기
            } else {
                // 3. 실패했다면 에러 메시지를 띄웁니다.
                JOptionPane.showMessageDialog(this, "저장 실패! 콘솔창의 에러를 확인하세요.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        bottom.add(btnTweet);
        add(bottom, BorderLayout.SOUTH);
    }
}
