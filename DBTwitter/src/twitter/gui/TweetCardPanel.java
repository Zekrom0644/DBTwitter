package twitter.gui;

import twitter.model.Post;
import twitter.service.TwitterService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TweetCardPanel extends JPanel {

    private final TwitterService service;
    private final Post post;
    private final String currentUserId;
    private final Runnable refreshAction;
    private final Runnable openDetail;

    private final Color normalColor = Color.WHITE;
    private final Color hoverColor = new Color(245, 248, 250);
    private final Color clickColor = new Color(235, 241, 245);

    private boolean clicked = false;

    public TweetCardPanel(
            TwitterService service,
            Post post,
            String currentUserId, 
            Runnable refreshAction,
            Runnable openDetail
    ) {
        this.service = service;
        this.post = post;
        this.currentUserId = currentUserId;
        this.refreshAction = refreshAction;
        this.openDetail = openDetail;

        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(normalColor);

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 236, 240)));

        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        /* ------------------------------------
         * 상단(작성자 + 내용)
         * ------------------------------------ */
        JLabel lblUser = new JLabel("@" + post.getWriterId());
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));

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
         * 하단 버튼 패널 (좋아요/싫어요/삭제)
         * ------------------------------------ */
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        btnPanel.setOpaque(false);

        JButton btnLike = new JButton("♡ " + post.getNumLikes());
        JButton btnDislike = new JButton("👎 " + post.getNumDislikes());

        btnLike.setFocusPainted(false);
        btnDislike.setFocusPainted(false);

        // 좋아요 이벤트
        btnLike.addActionListener(e -> {
            boolean isAdded = service.toggleLike(post.getPostId(), currentUserId);
            if (isAdded) post.setNumLikes(post.getNumLikes() + 1);
            else post.setNumLikes(post.getNumLikes() - 1);
            btnLike.setText("♡ " + post.getNumLikes());
        });

        // 싫어요 이벤트
        btnDislike.addActionListener(e -> {
            boolean isAdded = service.toggleDislike(post.getPostId(), currentUserId);
            if (isAdded) post.setNumDislikes(post.getNumDislikes() + 1);
            else post.setNumDislikes(post.getNumDislikes() - 1);
            btnDislike.setText("👎 " + post.getNumDislikes());
        });

        btnPanel.add(btnLike);
        btnPanel.add(btnDislike);

        // ★ [삭제 기능 추가] : 현재 로그인한 유저 == 글 작성자일 때만 버튼 표시
        if (currentUserId.equals(post.getWriterId())) {
            
            JButton btnDelete = new JButton("🗑 Delete");
            btnDelete.setForeground(Color.RED); // 빨간색 글씨
            btnDelete.setFocusPainted(false);
            btnDelete.setBorderPainted(false);  // 테두리 없애기 (심플하게)
            btnDelete.setContentAreaFilled(false); // 배경 투명하게

            btnDelete.addActionListener(e -> {
                // 1. 정말 삭제할지 물어보기
                int answer = JOptionPane.showConfirmDialog(
                        this, 
                        "정말 이 글을 삭제하시겠습니까?", 
                        "Delete Post", 
                        JOptionPane.YES_NO_OPTION
                );

                if (answer == JOptionPane.YES_OPTION) {
                    // 2. 삭제 진행
                    boolean success = service.deletePost(post.getPostId());
                    if (success) {
                        JOptionPane.showMessageDialog(this, "삭제되었습니다.");
                        // 3. 화면 새로고침 (삭제된 글 사라지게)
                        if (refreshAction != null) refreshAction.run();
                    } else {
                        JOptionPane.showMessageDialog(this, "삭제 실패!");
                    }
                }
            });

            btnPanel.add(Box.createHorizontalStrut(20)); // 간격 띄우기
            btnPanel.add(btnDelete);
        }

        add(btnPanel, BorderLayout.SOUTH);

        /* ------------------------------------
         * 마우스 이벤트
         * ------------------------------------ */
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!clicked) setBackground(hoverColor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!clicked) setBackground(normalColor);
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    clicked = true;
                    setBackground(clickColor);
                }
                if (e.getClickCount() == 2) {
                    if (openDetail != null) openDetail.run();
                }
            }
        });
    }
}