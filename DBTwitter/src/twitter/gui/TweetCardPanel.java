package twitter.gui;

import twitter.model.Post;
import twitter.service.TwitterService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class TweetCardPanel extends JPanel {

    public TweetCardPanel(
            TwitterService service,
            Post post,
            String currentUserId, 
            Runnable refreshAction,
            Runnable openDetail,
            Consumer<String> openProfile // ★ 추가됨: 프로필 이동 콜백
    ) {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 236, 240)));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        /* ------------------------------------
         * 상단(작성자 + 내용)
         * ------------------------------------ */
        // 작성자 아이디를 버튼처럼 만듦
        JLabel lblUser = new JLabel("<html><a href=''>@" + post.getWriterId() + "</a></html>");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUser.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // 아이디 클릭 시 프로필로 이동
        lblUser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (openProfile != null) openProfile.accept(post.getWriterId());
            }
        });

        JLabel lblContent = new JLabel("<html>" + post.getContent() + "</html>");
        lblContent.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        top.add(lblUser);
        top.add(Box.createVerticalStrut(6));
        top.add(lblContent);

        add(top, BorderLayout.CENTER);

        /* ------------------------------------
         * 하단 버튼 (좋아요/싫어요/삭제)
         * ------------------------------------ */
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        btnPanel.setOpaque(false);

        JButton btnLike = new JButton("♡ " + post.getNumLikes());
        JButton btnDislike = new JButton("👎 " + post.getNumDislikes());

        btnLike.setFocusPainted(false);
        btnDislike.setFocusPainted(false);
        
        btnLike.addActionListener(e -> {
            boolean isAdded = service.toggleLike(post.getPostId(), currentUserId);
            post.setNumLikes(post.getNumLikes() + (isAdded ? 1 : -1));
            btnLike.setText("♡ " + post.getNumLikes());
        });

        btnDislike.addActionListener(e -> {
            boolean isAdded = service.toggleDislike(post.getPostId(), currentUserId);
            post.setNumDislikes(post.getNumDislikes() + (isAdded ? 1 : -1));
            btnDislike.setText("👎 " + post.getNumDislikes());
        });

        btnPanel.add(btnLike);
        btnPanel.add(btnDislike);

        if (currentUserId.equals(post.getWriterId())) {
            JButton btnDelete = new JButton("🗑");
            btnDelete.setForeground(Color.RED);
            btnDelete.setBorderPainted(false);
            btnDelete.setContentAreaFilled(false);
            btnDelete.addActionListener(e -> {
                int ans = JOptionPane.showConfirmDialog(this, "Delete?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (ans == JOptionPane.YES_OPTION && service.deletePost(post.getPostId())) {
                    if (refreshAction != null) refreshAction.run();
                }
            });
            btnPanel.add(btnDelete);
        }

        add(btnPanel, BorderLayout.SOUTH);

        // 카드 전체 클릭 시 상세화면 이동 (아이디 클릭과 겹치지 않게 주의)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && openDetail != null) openDetail.run();
            }
        });
    }
}