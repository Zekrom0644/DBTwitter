package twitter.gui;

import twitter.model.FollowerItem;
import twitter.service.TwitterService;

import javax.swing.*;
import java.awt.*;

public class FollowersCardPanel extends JPanel {

    public FollowersCardPanel(TwitterService service, String me, FollowerItem item, Runnable refreshAction) {

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(230,236,240)));
        setBackground(Color.WHITE);

        JLabel lblId = new JLabel("@" + item.getUserId());
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton btn = new JButton(item.isFollowing() ? "Unfollow" : "Follow");
        btn.setFocusPainted(false);

        btn.addActionListener(e -> {
            if (item.isFollowing()) {
                service.unfollow(me, item.getUserId());
                item.setFollowing(false);
            } else {
                service.follow(me, item.getUserId());
                item.setFollowing(true);
            }
            btn.setText(item.isFollowing() ? "Unfollow" : "Follow");
            if (refreshAction != null) refreshAction.run();
        });

        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(10,15,10,15));
        inner.add(lblId, BorderLayout.WEST);
        inner.add(btn, BorderLayout.EAST);

        add(inner, BorderLayout.CENTER);
    }
}
