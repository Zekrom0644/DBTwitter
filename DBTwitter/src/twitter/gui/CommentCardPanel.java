package twitter.gui;

import twitter.model.Comment;
import twitter.service.TwitterService;

import javax.swing.*;
import java.awt.*;

public class CommentCardPanel extends JPanel {

    public CommentCardPanel(TwitterService service, String currentUserId, Comment c) {
        setLayout(new BorderLayout());

        // ★ 댓글 카드 전체 배경을 테마에 맞게 설정
        setBackground(ThemeManager.isDark ? new Color(34,34,34) : Color.WHITE);
        setOpaque(true);

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230,236,240)));

        // 상단: 작성자 + 내용
        JLabel user = new JLabel("@" + c.getWriterId());
        user.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // ★ 테마에 따라 글씨색 변경
        user.setForeground(ThemeManager.isDark ? Color.WHITE : Color.BLACK);

        JLabel content = new JLabel("<html>" + c.getContent() + "</html>");
        content.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        content.setOpaque(false);

        // ★ 테마에 따라 글씨색 변경 (여기가 핵심!)
        content.setForeground(ThemeManager.isDark ? Color.WHITE : Color.BLACK);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,10));
        textPanel.add(user);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(content);

        add(textPanel, BorderLayout.CENTER);

        // 하단: 좋아요 버튼
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);

        JButton likeBtn = new JButton("♡ " + c.getNumLikes());
        likeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        likeBtn.setBorderPainted(false);
        likeBtn.setContentAreaFilled(false);
        likeBtn.setFocusPainted(false);
        likeBtn.putClientProperty("keepColor", true);

        // ★ 다크모드에서도 좋아요 글자색은 고정 유지
        likeBtn.setForeground(ThemeManager.isDark ? Color.WHITE : Color.BLACK);

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
