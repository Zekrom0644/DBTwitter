package twitter.gui;

import twitter.model.FollowerItem;
import twitter.service.TwitterService;

import javax.swing.*;
import java.awt.*;

public class FollowerListRenderer extends JPanel implements ListCellRenderer<FollowerItem> {

    private JLabel lblUser = new JLabel();
    private JButton btnFollow = new JButton();
    private FollowerItem currentItem;

    private final TwitterService service;
    private final String me;

    public FollowerListRenderer(TwitterService service, String me) {
        this.service = service;
        this.me = me;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(230,236,240)));
        setBackground(Color.WHITE);

        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnFollow.setFocusPainted(false);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        center.add(lblUser, BorderLayout.WEST);
        center.add(btnFollow, BorderLayout.EAST);

        add(center, BorderLayout.CENTER);

        // 버튼 이벤트 처리
        btnFollow.addActionListener(e -> {
            if (currentItem != null) {
                if (currentItem.isFollowing()) {
                    service.unfollow(me, currentItem.getUserId());
                    currentItem.setFollowing(false);
                } else {
                    service.follow(me, currentItem.getUserId());
                    currentItem.setFollowing(true);
                }
                btnFollow.setText(currentItem.isFollowing() ? "Unfollow" : "Follow");
                repaint();
            }
        });
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends FollowerItem> list,
            FollowerItem item,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        currentItem = item;

        lblUser.setText("@" + item.getUserId());
        btnFollow.setText(item.isFollowing() ? "Unfollow" : "Follow");

        setBackground(isSelected ? new Color(245, 248, 250) : Color.WHITE);

        return this;
    }
}
