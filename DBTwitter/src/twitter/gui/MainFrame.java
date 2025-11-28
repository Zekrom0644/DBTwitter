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

    /* CardLayout 기본 패널 */
    private final JPanel contentPanel = new JPanel(new CardLayout());
    private final String CARD_HOME = "HOME";
    private final String CARD_FOLLOWERS = "FOLLOWERS";
    private final String CARD_DETAIL = "DETAIL";

    /* Home 화면 */
    private JPanel timelinePanel;

    /* Followers 화면 */
    private JPanel followersPanel;

    /* Detail 화면 */
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
     * Sidebar (사이드바 메뉴)
     * ====================================================== */
    private void initSidebar() {

        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(Color.WHITE);
        side.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 236, 240)));
        side.setPreferredSize(new Dimension(160, getHeight()));

        JLabel logo = new JLabel();
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 로고 이미지 로드 (없을 경우 텍스트 처리)
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

        // 메뉴 버튼들
        side.add(createMenuButton("Home", e -> showHome()));
        side.add(Box.createVerticalStrut(15));
        side.add(createMenuButton("Followers", e -> showFollowers()));

        // [New] 비밀번호 변경 버튼 추가
        side.add(Box.createVerticalStrut(15));
        side.add(createMenuButton("Password", e -> {
            new ChangePasswordDialog(this, service, userId).setVisible(true);
        }));

        side.add(Box.createVerticalGlue());

        // 로그아웃 버튼
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
     * Content Panels (HOME + FOLLOWERS + DETAIL)
     * ====================================================== */
    private void initContentPanels() {

        /* ------------------ HOME 화면 ------------------ */
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

        /* 타임라인 패널 */
        timelinePanel = new JPanel();
        timelinePanel.setLayout(new BoxLayout(timelinePanel, BoxLayout.Y_AXIS));
        timelinePanel.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(timelinePanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        homePanel.add(scroll, BorderLayout.CENTER);

        /* Refresh Button */
        JPanel bottom = new JPanel();
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 25, 10));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(120, 38));
        refreshBtn.addActionListener(e -> loadTimeline());
        bottom.add(refreshBtn);

        homePanel.add(bottom, BorderLayout.SOUTH);

        contentPanel.add(homePanel, CARD_HOME);

        /* ------------------ FOLLOWERS 화면 ------------------ */
        followersPanel = new JPanel(new BorderLayout());
        followersPanel.setBackground(Color.WHITE);
        contentPanel.add(followersPanel, CARD_FOLLOWERS);

        /* ------------------ DETAIL 화면 ------------------ */
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

        List<User> fl = service.getFollowers(userId);
        for (User u : fl) {
            FollowerItem item = new FollowerItem(u.getUserId(), service.isFollowing(userId, u.getUserId()));
            list.add(new FollowersCardPanel(service, userId, item, this::showFollowers));
        }

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        followersPanel.add(scroll, BorderLayout.CENTER);

        followersPanel.revalidate();
        followersPanel.repaint();
    }

    /* ======================================================
     * 타임라인 로드
     * ====================================================== */
    private void loadTimeline() {

        timelinePanel.removeAll();

        // 서비스에서 '내 글 + 팔로잉 글' 목록 가져오기
        List<Post> posts = service.getTimeline(userId);

        for (Post p : posts) {
            // ★ 수정: 생성자에 userId 전달 (좋아요/삭제 권한 확인용)
            TweetCardPanel card = new TweetCardPanel(
                    service,
                    p,
                    userId, 
                    this::refreshHome,
                    () -> showPostDetail(p)
            );
            timelinePanel.add(card);
        }

        timelinePanel.revalidate();
        timelinePanel.repaint();
    }

    private void refreshHome() {
        loadTimeline();
    }

    private void openWritePostDialog() {
        new WritePostDialog(this, service, userId).setVisible(true);
        loadTimeline();
    }

    private void logout() {
        dispose();
        new LoginFrame(service).setVisible(true);
    }

    /* ======================================================
     * 상세 화면 표시
     * ====================================================== */
    private void showPostDetail(Post post) {

        postDetailWrapper.removeAll();

        // ★ 수정: PostDetailPanel에도 userId 전달
        PostDetailPanel detail = new PostDetailPanel(service, userId, post, this::showHome);

        postDetailWrapper.add(detail, BorderLayout.CENTER);
        postDetailWrapper.revalidate();
        postDetailWrapper.repaint();

        ((CardLayout) contentPanel.getLayout()).show(contentPanel, CARD_DETAIL);
    }
}