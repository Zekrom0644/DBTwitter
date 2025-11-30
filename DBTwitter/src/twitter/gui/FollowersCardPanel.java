package twitter.gui;

import twitter.model.User;
import twitter.service.TwitterService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class FollowersCardPanel extends JPanel {

    private final TwitterService service;
    private final String currentUserId;
    private final String targetUserId;
    private final Runnable refreshAction;

    // ★ 수정됨: openProfile 콜백 추가
    public FollowersCardPanel(TwitterService service, String currentUserId, User targetUser, Runnable refreshAction, Consumer<String> openProfile) {
        this.service = service;
        this.currentUserId = currentUserId;
        this.targetUserId = targetUser.getUserId();
        this.refreshAction = refreshAction;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(230,236,240)));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(0, 60));

        // 1. 유저 아이디 표시 (클릭 가능하게 설정)
        JLabel lblId = new JLabel("@" + targetUserId);
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // ★ 프로필 이동 기능 추가
        if (openProfile != null) {
            lblId.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lblId.setToolTipText("View Profile");
            lblId.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    openProfile.accept(targetUserId);
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    lblId.setForeground(new Color(29, 161, 242)); // 파란색 하이라이트
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    lblId.setForeground(Color.BLACK);
                }
            });
        }

        // 2. 팔로우/언팔로우 버튼
        JButton btn = new JButton();
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.putClientProperty("keepColor", true);
        btn.setOpaque(true);
        
        if (currentUserId.equals(targetUserId)) {
            btn.setVisible(false);
        } else {
            updateButtonState(btn);
        }

        btn.addActionListener(e -> {
            boolean isFollowing = service.isFollowing(currentUserId, targetUserId);
            if (isFollowing) {
                if (service.unfollow(currentUserId, targetUserId)) updateButtonUI(btn, false);
            } else {
                if (service.follow(currentUserId, targetUserId)) updateButtonUI(btn, true);
            }
            if (refreshAction != null) refreshAction.run();
        });

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.setOpaque(false);
        left.add(lblId);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(btn);

        add(left, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);
    }

    private void updateButtonState(JButton btn) {
        boolean isFollowing = service.isFollowing(currentUserId, targetUserId);
        updateButtonUI(btn, isFollowing);
    }

    private void updateButtonUI(JButton btn, boolean isFollowing) {
        if (isFollowing) {
            btn.setText("Unfollow");
            btn.setBackground(new Color(220, 53, 69));
            btn.setForeground(Color.WHITE);
        } else {
            btn.setText("Follow");
            btn.setBackground(new Color(29, 161, 242));
            btn.setForeground(Color.WHITE);
        }
    }

}
