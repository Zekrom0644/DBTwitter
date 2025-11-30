package twitter.gui;

import twitter.model.Comment;
import twitter.service.TwitterService;

import javax.swing.*;
import java.awt.*;

public class CommentCardPanel extends JPanel {

    public CommentCardPanel(TwitterService service, String currentUserId, Comment c) {
        setLayout(new BorderLayout());
        setOpaque(true); // ★ 테마가 제대로 적용되도록 true 유지
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230,236,240)));

        // 상단: 작성자 + 내용
        JLabel user = new JLabel("@" + c.getWriterId());
        user.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // ★ HTML 배경색을 테마에 맞게 강제 적용
        String bg = ThemeManager.isDark ? "#141414" : "#FFFFFF";
        JLabel content = new JLabel(
                "<html><div style='background-color:" + bg + ";'>" 
                + c.getContent() + 
                "</div></html>"
        );
        content.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        content.setOpaque(false);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(true); // ★ panel은 테마색을 따라가야 함
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,10));
        textPanel.add(user);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(content);

        add(textPanel, BorderLayout.CENTER);

        // 하단: 좋아요 버튼
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(true); // ★ 테마 적용

        JButton likeBtn = new JButton("♡ " + c.getNumLikes());
        likeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        likeBtn.setBorderPainted(false);
        likeBtn.setContentAreaFilled(false);
        likeBtn.setFocusPainted(false);
        likeBtn.putClientProperty("keepColor", true);

        likeBtn.addActionListener(e -> {
            boolean increased = service.toggleCommentLike(c.getCommentId(), currentUserId);
            if(increased) c.setNumLikes(c.getNumLikes() + 1);
            else c.setNumLikes(c.getNumLikes() - 1);

            likeBtn.setText("♡ " + c.getNumLikes());
        });

        bottom.add(likeBtn);
        add(bottom, BorderLayout.SOUTH);
    }
}
