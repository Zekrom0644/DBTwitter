package twitter.gui;

import twitter.model.User;
import twitter.service.TwitterService;

import javax.swing.*;
import java.awt.*;

public class FollowersCardPanel extends JPanel {

    private final TwitterService service;
    private final String currentUserId; // 로그인한 나
    private final String targetUserId;  // 카드에 표시된 사람
    private final Runnable refreshAction; // (선택) 클릭 후 새로고침 동작

    public FollowersCardPanel(TwitterService service, String currentUserId, User targetUser, Runnable refreshAction) {
        this.service = service;
        this.currentUserId = currentUserId;
        this.targetUserId = targetUser.getUserId();
        this.refreshAction = refreshAction;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(230,236,240)));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(0, 60)); // 높이 고정

        // 1. 유저 아이디 표시
        JLabel lblId = new JLabel("@" + targetUserId);
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // 2. 팔로우/언팔로우 버튼
        JButton btn = new JButton();
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // 내 자신이면 버튼 숨기기
        if (currentUserId.equals(targetUserId)) {
            btn.setVisible(false);
        } else {
            // 초기 상태 확인 (이미 팔로우 중인가?)
            updateButtonState(btn);
        }

        // 3. 버튼 클릭 이벤트
        btn.addActionListener(e -> {
            boolean isFollowing = service.isFollowing(currentUserId, targetUserId);

            if (isFollowing) {
                // 이미 팔로우 중 -> 언팔로우 실행
                if (service.unfollow(currentUserId, targetUserId)) {
                    // 성공 시 버튼 상태 변경
                    updateButtonUI(btn, false);
                }
            } else {
                // 팔로우 안 함 -> 팔로우 실행
                if (service.follow(currentUserId, targetUserId)) {
                    // 성공 시 버튼 상태 변경
                    updateButtonUI(btn, true);
                }
            }
            
            // 필요 시 부모 화면 새로고침 (예: 목록 갱신)
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

    // DB 상태를 확인해서 버튼 모양 결정
    private void updateButtonState(JButton btn) {
        boolean isFollowing = service.isFollowing(currentUserId, targetUserId);
        updateButtonUI(btn, isFollowing);
    }

    // 버튼 디자인 변경 (True: 팔로잉 중 / False: 팔로우 안 함)
    private void updateButtonUI(JButton btn, boolean isFollowing) {
        if (isFollowing) {
            btn.setText("Unfollow");
            btn.setBackground(new Color(220, 53, 69)); // 빨간색 (언팔로우)
            btn.setForeground(Color.WHITE);
        } else {
            btn.setText("Follow");
            btn.setBackground(new Color(29, 161, 242)); // 파란색 (팔로우)
            btn.setForeground(Color.WHITE);
        }
    }
}