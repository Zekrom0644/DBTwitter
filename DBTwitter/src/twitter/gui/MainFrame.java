package twitter.gui;

import twitter.model.Post;
import twitter.model.User;
import twitter.model.FollowerItem;
import twitter.service.TwitterService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {

    private final TwitterService service;
    private final String userId;

    private final JPanel contentPanel = new JPanel(new CardLayout());
    private final String CARD_HOME = "HOME";
    private final String CARD_EXPLORE = "EXPLORE";
    private final String CARD_FOLLOWERS = "FOLLOWERS";
    private final String CARD_FOLLOWING = "FOLLOWING"; // ★ 추가
    private final String CARD_DETAIL = "DETAIL";
    private final String CARD_PROFILE = "PROFILE";

    private JPanel timelinePanel;
    private JPanel explorePanel;
    private JPanel followersPanel;
    private JPanel followingPanel; // ★ 추가
    private JPanel postDetailWrapper;
    private JPanel profilePanel;

    public MainFrame(TwitterService service, String userId) {
        this.service = service;
        this.userId = userId;

        setTitle("Twitter - @" + userId);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initSidebar();
        initContentPanels();
        add(contentPanel, BorderLayout.CENTER);
        showHome();

        // 처음 띄울 때 현재 모드(기본: Light)에 맞게 테마 적용
        ThemeManager.applyTheme(getContentPane());
    }

    private void initSidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(Color.WHITE);
        side.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 236, 240)));
        side.setPreferredSize(new Dimension(160, getHeight()));

        JLabel logo = new JLabel("Twitter");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/logo_twitter_48.png"));
            Image scaled = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(scaled));
            logo.setText("");
        } catch (Exception e) {}

        side.add(Box.createVerticalStrut(25));
        side.add(logo);
        side.add(Box.createVerticalStrut(25));

        side.add(createMenuButton("Home", e -> showHome()));
        side.add(Box.createVerticalStrut(15));
        side.add(createMenuButton("Explore", e -> showExplore()));
        side.add(Box.createVerticalStrut(15));
        side.add(createMenuButton("Followers", e -> showFollowers()));
        side.add(Box.createVerticalStrut(15));
        // ★ [New] Following 버튼 추가
        side.add(createMenuButton("Following", e -> showFollowing()));
        side.add(Box.createVerticalStrut(15));
        side.add(createMenuButton("My Profile", e -> showUserProfile(userId)));

        side.add(Box.createVerticalGlue());

        side.add(createMenuButton("Password", e -> new ChangePasswordDialog(this, service, userId).setVisible(true)));
        side.add(Box.createVerticalStrut(15));

        // ★ 다크 모드 토글 버튼 추가
        JButton darkBtn = new JButton(ThemeManager.isDark ? "Light Mode" : "Dark Mode");
        darkBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        darkBtn.setMaximumSize(new Dimension(140, 36));
        darkBtn.setFocusPainted(false);
        // Dark/Light 모드는 ThemeManager가 색을 관리하므로 keepColor 설정 안 함
        darkBtn.addActionListener(e -> {
            ThemeManager.isDark = !ThemeManager.isDark;
            ThemeManager.applyTheme(getContentPane());
            darkBtn.setText(ThemeManager.isDark ? "Light Mode" : "Dark Mode");
        });
        side.add(darkBtn);
        side.add(Box.createVerticalStrut(15));
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        // 이 버튼은 고유 색상을 유지해야 하므로 keepColor 플래그 설정
        logoutBtn.putClientProperty("keepColor", true);
        logoutBtn.addActionListener(e -> { dispose(); new LoginFrame(service).setVisible(true); });
        
        side.add(logoutBtn);
        side.add(Box.createVerticalStrut(25));
        add(side, BorderLayout.WEST);
    }

    private JButton createMenuButton(String text, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(140, 36));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.addActionListener(listener);
        return btn;
    }

    private void initContentPanels() {
        // HOME
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(Color.WHITE);
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(20, 40, 15, 40));
        JLabel title = new JLabel("Home");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        JButton tweetBtn = new JButton("Tweet");
        tweetBtn.setBackground(new Color(29, 161, 242));
        tweetBtn.setForeground(Color.WHITE);
        tweetBtn.setFocusPainted(false);
        // 이 버튼도 고유 색상을 유지해야 하므로 keepColor 플래그 설정
        tweetBtn.putClientProperty("keepColor", true);
        tweetBtn.addActionListener(e -> { new WritePostDialog(this, service, userId).setVisible(true); loadTimeline(); });
        header.add(title, BorderLayout.WEST);
        header.add(tweetBtn, BorderLayout.EAST);
        homePanel.add(header, BorderLayout.NORTH);

        timelinePanel = new JPanel();
        timelinePanel.setLayout(new BoxLayout(timelinePanel, BoxLayout.Y_AXIS));
        homePanel.add(new JScrollPane(timelinePanel), BorderLayout.CENTER);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadTimeline());
        homePanel.add(refreshBtn, BorderLayout.SOUTH);
        contentPanel.add(homePanel, CARD_HOME);

        // EXPLORE
        explorePanel = new JPanel(new BorderLayout());
        explorePanel.setBackground(Color.WHITE);
        contentPanel.add(explorePanel, CARD_EXPLORE);

        // FOLLOWERS
        followersPanel = new JPanel(new BorderLayout());
        followersPanel.setBackground(Color.WHITE);
        contentPanel.add(followersPanel, CARD_FOLLOWERS);

        // FOLLOWING (★ 추가)
        followingPanel = new JPanel(new BorderLayout());
        followingPanel.setBackground(Color.WHITE);
        contentPanel.add(followingPanel, CARD_FOLLOWING);

        // DETAIL
        postDetailWrapper = new JPanel(new BorderLayout());
        contentPanel.add(postDetailWrapper, CARD_DETAIL);

        // PROFILE
        profilePanel = new JPanel(new BorderLayout());
        profilePanel.setBackground(Color.WHITE);
        contentPanel.add(profilePanel, CARD_PROFILE);
    }

    private void showHome() {
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, CARD_HOME);
        loadTimeline();
        // ★ 화면 전환 후 현재 모드에 맞게 다시 테마 적용
        ThemeManager.applyTheme(contentPanel);
    }

    private void showExplore() {
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, CARD_EXPLORE);
        explorePanel.removeAll();

        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchBox.setBackground(Color.WHITE);
        JTextField txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Search");
        searchBox.add(new JLabel("Search:"));
        searchBox.add(txtSearch);
        searchBox.add(btnSearch);
        explorePanel.add(searchBox, BorderLayout.NORTH);

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        explorePanel.add(new JScrollPane(resultPanel), BorderLayout.CENTER);

        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText().trim();
            resultPanel.removeAll();
            if (keyword.isEmpty()) {
                JLabel sub = new JLabel("  Who to follow");
                sub.setFont(new Font("Segoe UI", Font.BOLD, 18));
                resultPanel.add(sub);
                List<User> recs = service.getRecommendations(userId);
                // ★ 수정: 프로필 이동 콜백 추가
                for(User u : recs) resultPanel.add(new FollowersCardPanel(service, userId, u, null, this::showUserProfile));
            } else {
                List<User> users = service.searchUsers(keyword);
                if (!users.isEmpty()) {
                    JLabel sub = new JLabel("  Users matching '" + keyword + "'");
                    sub.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    resultPanel.add(sub);
                    // ★ 수정: 프로필 이동 콜백 추가
                    for(User u : users) resultPanel.add(new FollowersCardPanel(service, userId, u, null, this::showUserProfile));
                    resultPanel.add(Box.createVerticalStrut(20));
                }
                List<Post> posts = service.searchPosts(keyword);
                if (!posts.isEmpty()) {
                    JLabel sub = new JLabel("  Posts matching '" + keyword + "'");
                    sub.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    resultPanel.add(sub);
                    for(Post p : posts) {
                        resultPanel.add(new TweetCardPanel(service, p, userId, null, () -> showPostDetail(p), this::showUserProfile));
                    }
                }
                if(users.isEmpty() && posts.isEmpty()) resultPanel.add(new JLabel("  No results found."));
            }
            resultPanel.revalidate();
            resultPanel.repaint();
        });
        btnSearch.doClick();

        // ★ 화면 전환 후 테마 재적용
        ThemeManager.applyTheme(contentPanel);
    }

    private void showFollowers() {
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, CARD_FOLLOWERS);
        followersPanel.removeAll();
        JLabel fTitle = new JLabel("Followers");
        fTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        fTitle.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        followersPanel.add(fTitle, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(Color.WHITE);
        List<User> followers = service.getFollowers(userId);
        if (followers.isEmpty()) {
            JLabel msg = new JLabel("You have no followers yet.");
            msg.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 0));
            list.add(msg);
        } else {
            for (User u : followers) {
                // ★ 수정: 클릭 시 프로필로 이동
                list.add(new FollowersCardPanel(service, userId, u, null, this::showUserProfile));
            }
        }
        followersPanel.add(new JScrollPane(list), BorderLayout.CENTER);
        followersPanel.revalidate();
        followersPanel.repaint();

        // ★ 화면 전환 후 테마 재적용
        ThemeManager.applyTheme(contentPanel);
    }

    // ★ [New] 내가 팔로우하는 사람 목록 보기
    private void showFollowing() {
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, CARD_FOLLOWING);
        followingPanel.removeAll();
        JLabel fTitle = new JLabel("Following");
        fTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        fTitle.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        followingPanel.add(fTitle, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(Color.WHITE);
        
        // 서비스에서 목록 가져오기
        List<User> following = service.getFollowing(userId);
        
        if (following.isEmpty()) {
            JLabel msg = new JLabel("You are not following anyone yet.");
            msg.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 0));
            list.add(msg);
        } else {
            for (User u : following) {
                // ★ 수정: 클릭 시 프로필로 이동
                list.add(new FollowersCardPanel(service, userId, u, null, this::showUserProfile));
            }
        }
        followingPanel.add(new JScrollPane(list), BorderLayout.CENTER);
        followingPanel.revalidate();
        followingPanel.repaint();

        // ★ 화면 전환 후 테마 재적용
        ThemeManager.applyTheme(contentPanel);
    }

    private void showUserProfile(String targetId) {
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, CARD_PROFILE);
        profilePanel.removeAll();
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        JLabel lblTitle = new JLabel("@" + targetId + "'s Profile");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.add(lblTitle, BorderLayout.WEST);

        if (!targetId.equals(userId)) {
            User targetUser = new User(targetId);
            // 헤더의 팔로우 버튼은 클릭 시 프로필 이동 안 해도 됨 (null)
            FollowersCardPanel followBtnPanel = new FollowersCardPanel(service, userId, targetUser, null, null);
            header.add(followBtnPanel, BorderLayout.EAST);
        }
        profilePanel.add(header, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        List<Post> userPosts = service.getUserPosts(targetId);
        if (userPosts.isEmpty()) {
            listPanel.add(new JLabel("  No tweets yet."));
        } else {
            for (Post p : userPosts) {
                listPanel.add(new TweetCardPanel(service, p, userId, 
                        () -> showUserProfile(targetId), 
                        () -> showPostDetail(p), 
                        this::showUserProfile));
            }
        }
        profilePanel.add(new JScrollPane(listPanel), BorderLayout.CENTER);
        profilePanel.revalidate();
        profilePanel.repaint();

        // ★ 화면 전환 후 테마 재적용
        ThemeManager.applyTheme(contentPanel);
    }

    private void loadTimeline() {
        timelinePanel.removeAll();
        List<Post> posts = service.getTimeline(userId);
        for (Post p : posts) {
            timelinePanel.add(new TweetCardPanel(service, p, userId, this::loadTimeline, () -> showPostDetail(p), this::showUserProfile));
        }
        timelinePanel.revalidate();
        timelinePanel.repaint();
    }
    
    private void showPostDetail(Post post) {
        postDetailWrapper.removeAll();
        PostDetailPanel detail = new PostDetailPanel(service, userId, post, () -> showHome());
        postDetailWrapper.add(detail, BorderLayout.CENTER);
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, CARD_DETAIL);

        // ★ 상세 화면 전환 후 테마 재적용
        ThemeManager.applyTheme(contentPanel);
    }
}
