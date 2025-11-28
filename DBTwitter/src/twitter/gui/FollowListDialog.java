package twitter.gui;

import twitter.service.TwitterService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FollowListDialog extends JDialog {

    private final DefaultListModel<String> model = new DefaultListModel<>();

    public FollowListDialog(Frame owner, TwitterService service, String userId) {
        super(owner, "Followers", true);
        setSize(400, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        JList<String> followerList = new JList<>(model);
        followerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabs.addTab("Followers", new JScrollPane(followerList));

        DefaultListModel<String> followingModel = new DefaultListModel<>();
        JList<String> followingList = new JList<>(followingModel);
        tabs.addTab("Following", new JScrollPane(followingList));

        add(tabs, BorderLayout.CENTER);

        /* TODO: 실제 DB 연동
        List<String> followers = service.getFollowers(userId);
        for (String f : followers) model.addElement(f);

        List<String> following = service.getFollowing(userId);
        for (String f : following) followingModel.addElement(f);*/
    }
}
