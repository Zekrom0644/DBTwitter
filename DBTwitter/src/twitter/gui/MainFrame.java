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

    /* CardLayout 패널 이름 상수 */
    private final JPanel contentPanel = new JPanel(new CardLayout());
    private final String CARD_HOME = "HOME";
    private final String CARD_EXPLORE = "EXPLORE"; // ★ 추가됨
    private final String CARD_FOLLOWERS = "FOLLOWERS";
    private final String CARD_DETAIL = "DETAIL";

    /* 화면 패널들 */
    private JPanel timelinePanel;
    private JPanel explorePanel;   // ★ 추가됨
    private JPanel followersPanel;
    private JPanel postDetailWrapper;

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
    }

    /* ======================================================
     * Sidebar (메뉴)
     * ====================================================== */
    private void initSidebar() {

        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(Color.WHITE);
        side.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 236, 240)));
        side.setPreferredSize(new Dimension(160, getHeight()));

        // 로고
        JLabel logo = new JLabel();
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        java.net.URL imgUrl = getClass().getResource("/logo_twitter_48.png");
        if (imgUrl != null) {
            ImageIcon icon = new ImageIcon(imgUrl);
            Image scaled = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(scaled));
        } else {
            logo.setText("Twitter");
            logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        }

        side.add(Box.createVerticalStrut(25));
        side.add(logo);
        side.add(Box.createVerticalStrut(25));

        // [메뉴 버튼들]
        side.add(createMenuButton("Home", e -> showHome()));
        side.add(Box.createVerticalStrut(15));
        
        // ★ [New] Explore (추천 친구) 버튼 추가
        side.add(createMenuButton("Explore", e -> showExplore()));
        side.add(Box.createVerticalStrut(15));
        
        side.add(createMenuButton("Followers", e -> showFollowers()));
        side.add(Box.createVerticalStrut(15));

        // 비밀번호 변경
        side.add(createMenuButton("Password", e -> {
            new ChangePasswordDialog(this, service, userId).setVisible(true);
        }));

        side.add(Box.createVerticalGlue());

        // 로그아웃
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setPreferredSize(new Dimension(120, 40));
        logoutBtn.setMaximumSize(new Dimension(120, 40));
        logoutBtn.putClientProperty("JButton.buttonType", "roundRect");
        logoutBtn.addActionListener(e -> logout());

        side.add(logoutBtn);
        side.add(Box.createVerticalStrut(25));

        add(side, BorderLayout.WEST);
    }

    private JButton createMenuButton(String text, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(140, 36));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 5));
        btn.addActionListener(listener);
        return btn;
    }

    /* ======================================================
     * Content Panels 초기화
     * ====================================================== */
    private void initContentPanels() {

        // 1. HOME 화면
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(20, 40, 15, 40));

        JLabel title = new JLabel("Home");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.add(title, BorderLayout.WEST);

        JButton tweetBtn = new JButton("Tweet");
        tweetBtn.setPreferredSize(new Dimension(110, 40));
        tweetBtn.setBackground(new Color(29, 161, 242));
        tweetBtn.setForeground(Color.WHITE);
        tweetBtn.putClientProperty("JButton.buttonType", "roundRect");
        tweetBtn.addActionListener(e -> openWritePostDialog());
        header.add(tweetBtn, BorderLayout.EAST);
        homePanel.add(header, BorderLayout.NORTH);

        timelinePanel = new JPanel();
        timelinePanel.setLayout(new BoxLayout(timelinePanel, BoxLayout.Y_AXIS));
        timelinePanel.setBackground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(timelinePanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        homePanel.add(scroll, BorderLayout.CENTER);

        // Refresh 버튼
        JPanel bottom = new JPanel();
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 25, 10));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(120, 38));
        refreshBtn.addActionListener(e -> loadTimeline());
        bottom.add(refreshBtn);
        homePanel.add(bottom, BorderLayout.SOUTH);

        contentPanel.add(homePanel, CARD_HOME);

        // 2. EXPLORE 화면 (★ 추가됨)
        explorePanel = new JPanel(new BorderLayout());
        explorePanel.setBackground(Color.WHITE);
        contentPanel.add(explorePanel, CARD_EXPLORE);

        // 3. FOLLOWERS 화면
        followersPanel = new JPanel(new BorderLayout());
        followersPanel.setBackground(Color.WHITE);
        contentPanel.add(followersPanel, CARD_FOLLOWERS);

        // 4. DETAIL 화면
        postDetailWrapper = new JPanel(new BorderLayout());
        postDetailWrapper.setBackground(Color.WHITE);
        contentPanel.add(postDetailWrapper, CARD_DETAIL);
    }

    /* ======================================================
     * 화면 전환 메서드
     * ====================================================== */
    private void showHome() {
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, CARD_HOME);
        loadTimeline();
    }

    // ★ [New] Explore 화면 로직 (추천 친구)
    private void showExplore() {
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, CARD_EXPLORE);
        explorePanel.removeAll();

        JLabel title = new JLabel("Who to follow");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        explorePanel.add(title, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(Color.WHITE);

        // 서비스에서 추천 목록 가져오기
        List<User> recommendations = service.getRecommendations(userId);

        if (recommendations.isEmpty()) {
            JLabel msg = new JLabel("No new users to follow.");
            msg.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 0));
            list.add(msg);
        } else {
            for (User u : recommendations) {
                // FollowersCardPanel을 재사용 (Follow 버튼이 파란색으로 뜰 것임)
                list.add(new FollowersCardPanel(service, userId, u, null));
            }
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        explorePanel.add(scroll, BorderLayout.CENTER);
        
        explorePanel.revalidate();
        explorePanel.repaint();
    }

    private void showFollowers() {
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, CARD_FOLLOWERS);
        followersPanel.removeAll();

        JLabel fTitle = new JLabel("Followers"); // 나를 팔로우하는 사람
        fTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        fTitle.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        followersPanel.add(fTitle, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(Color.WHITE);

        List<User> followers = service.getFollowers(userId);
        
        if (followers.isEmpty()) {
            JLabel noF = new JLabel("You have no followers yet.");
            noF.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 0));
            list.add(noF);
        } else {
            for (User targetUser : followers) {
                list.add(new FollowersCardPanel(service, userId, targetUser, null));
            }
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        followersPanel.add(scroll, BorderLayout.CENTER);

        followersPanel.revalidate();
        followersPanel.repaint();
    }

    /* ======================================================
     * 기타 메서드 (Timeline, Logout 등)
     * ====================================================== */
    private void loadTimeline() {
        timelinePanel.removeAll();
        List<Post> posts = service.getTimeline(userId);
        for (Post p : posts) {
            TweetCardPanel card = new TweetCardPanel(service, p, userId, this::refreshHome, () -> showPostDetail(p));
            timelinePanel.add(card);
        }
        timelinePanel.revalidate();
        timelinePanel.repaint();
    }

    private void refreshHome() { loadTimeline(); }
    private void openWritePostDialog() { new WritePostDialog(this, service, userId).setVisible(true); loadTimeline(); }
    private void logout() { dispose(); new LoginFrame(service).setVisible(true); }

    private void showPostDetail(Post post) {
        postDetailWrapper.removeAll();
        PostDetailPanel detail = new PostDetailPanel(service, userId, post, this::showHome);
        postDetailWrapper.add(detail, BorderLayout.CENTER);
        postDetailWrapper.revalidate();
        postDetailWrapper.repaint();
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, CARD_DETAIL);
    }
}