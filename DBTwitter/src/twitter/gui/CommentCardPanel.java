package twitter.gui;

import twitter.model.Comment;
import twitter.service.TwitterService;

import javax.swing.*;
import java.awt.*;

public class CommentCardPanel extends JPanel {

    public CommentCardPanel(TwitterService service, String currentUserId, Comment c) {
        setLayout(new BorderLayout());
        setOpaque(true);   // ★ ThemeManager가 다크/라이트 색을 적용할 수 있음
        // setBackground(Color.WHITE);  // ★ 삭제: 다크모드에서 배경이 하얀색으로 고정되는 문제 원인
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230,236,240)));

        // 상단: 작성자 + 내용
        JLabel user = new JLabel("@" + c.getWriterId());
        user.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JLabel content = new JLabel("<html>" + c.getContent() + "</html>");
        content.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(true);   // ★ 테마 적용을 위해 true로 변경
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,10));
        textPanel.add(user);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(content);

        add(textPanel, BorderLayout.CENTER);

        // 하단: 좋아요 버튼
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(true);   // ★ 테마 적용을 위해 true로 변경
        
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
