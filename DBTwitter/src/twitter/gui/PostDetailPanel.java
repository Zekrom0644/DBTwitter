package twitter.gui;

import twitter.model.Comment;
import twitter.model.Post;
import twitter.service.TwitterService;

import javax.swing.*;
import java.awt.*;
import java.util.List;



public class PostDetailPanel extends JPanel {

    private final TwitterService service;
    private final String userId;
    private final Post post;
    private final Runnable goBack; // 뒤로가기 (타임라인 새로고침 역할도 겸함)
    private JPanel commentsContainer;   // ★ 댓글 컨테이너 추가

    
    public PostDetailPanel(TwitterService service, String userId, Post post, Runnable goBack) {
        this.service = service;
        this.userId = userId;
        this.post = post;
        this.goBack = goBack;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    /* ======================================================
     * 상단 Back 버튼 + 제목
     * ====================================================== */
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(15, 25, 10, 25));

        JButton backBtn = new JButton("< Back");
        backBtn.addActionListener(e -> goBack.run());
        backBtn.setPreferredSize(new Dimension(80, 28));
        
        // 버튼 스타일 (선택사항)
        backBtn.setFocusPainted(false);
        backBtn.setBackground(new Color(245, 248, 250));

        JLabel lbl = new JLabel("Tweet");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);

        header.add(backBtn, BorderLayout.WEST);
        header.add(lbl, BorderLayout.CENTER);
        // 레이아웃 균형을 위해 오른쪽에 더미 컴포넌트 추가
        header.add(Box.createHorizontalStrut(80), BorderLayout.EAST);

        return header;
    }

    /* ======================================================
     * 본문 + 댓글 목록 + 댓글 작성창
     * ====================================================== */
    private JScrollPane buildContent() {

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(Color.WHITE);
        main.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        /* --------------------
         * 1. 포스트 본문 영역
         * -------------------- */
        JPanel postPanel = new JPanel();
        postPanel.setLayout(new BoxLayout(postPanel, BoxLayout.Y_AXIS));
        postPanel.setBackground(new Color(245, 248, 250)); // 연한 회색 배경
        postPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // 작성자
        JLabel writer = new JLabel("@" + post.getWriterId());
        writer.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // 내용
        JLabel content = new JLabel("<html><body style='width: 400px'>" + post.getContent() + "</body></html>");
        content.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        postPanel.add(writer);
        postPanel.add(Box.createVerticalStrut(15));
        postPanel.add(content);
        postPanel.add(Box.createVerticalStrut(25));

        /* --------------------
         * 2. 버튼 영역 (좋아요, 싫어요, 삭제 등)
         * -------------------- */
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btns.setOpaque(false);

        JButton like = new JButton("♡ " + post.getNumLikes());
        JButton dislike = new JButton("👎 " + post.getNumDislikes());
        
        like.setFocusPainted(false);
        dislike.setFocusPainted(false);

        // 좋아요 이벤트
        like.addActionListener(e -> {
            boolean isAdded = service.toggleLike(post.getPostId(), userId);
            if (isAdded) post.setNumLikes(post.getNumLikes() + 1);
            else post.setNumLikes(post.getNumLikes() - 1);
            like.setText("♡ " + post.getNumLikes());
        });

        // 싫어요 이벤트
        dislike.addActionListener(e -> {
            boolean isAdded = service.toggleDislike(post.getPostId(), userId);
            if (isAdded) post.setNumDislikes(post.getNumDislikes() + 1);
            else post.setNumDislikes(post.getNumDislikes() - 1);
            dislike.setText("👎 " + post.getNumDislikes());
        });

        btns.add(like);
        btns.add(dislike);

        // [삭제 버튼] 본인 글일 때만 표시
        if (userId.equals(post.getWriterId())) {
            JButton btnDelete = new JButton("🗑 Delete");
            btnDelete.setForeground(Color.RED);
            btnDelete.setFocusPainted(false);
            btnDelete.setBorderPainted(false);
            btnDelete.setContentAreaFilled(false);

            btnDelete.addActionListener(e -> {
                int answer = JOptionPane.showConfirmDialog(
                        this, "Delete this post?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    if (service.deletePost(post.getPostId())) {
                        JOptionPane.showMessageDialog(this, "Deleted.");
                        goBack.run(); // 삭제 후 목록으로 돌아가기
                    } else {
                        JOptionPane.showMessageDialog(this, "Delete failed.");
                    }
                }
            });
            btns.add(Box.createHorizontalStrut(10));
            btns.add(btnDelete);
        }

        postPanel.add(btns);

        main.add(postPanel);
        main.add(Box.createVerticalStrut(20));

        /* --------------------
         * 3. 댓글 목록 (CommentCardPanel 사용)
         * -------------------- */
        JLabel lblComments = new JLabel("Comments");
        lblComments.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblComments.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));
        main.add(lblComments);

        // ★ 댓글들이 들어갈 영역을 따로 분리
        commentsContainer = new JPanel();
        commentsContainer.setLayout(new BoxLayout(commentsContainer, BoxLayout.Y_AXIS));
        commentsContainer.setOpaque(false);
        main.add(commentsContainer);

        // 기존 댓글 로드
        loadComments();

        main.add(Box.createVerticalStrut(15));

        /* --------------------
         * 4. 댓글 입력창
         * -------------------- */
        JPanel write = new JPanel(new BorderLayout());
        write.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
        write.setBackground(Color.WHITE);

        // ★ 핵심: JTextArea 폭 확장 방지 → reply 버튼이 밀리지 않음
        JTextArea input = new JTextArea() {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.width = 0;    // ★ 폭 무제한 증식 방지
                return d;
            }
        };
        input.setLineWrap(true);
        input.setWrapStyleWord(true);
        input.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        input.setRows(3);

        JScrollPane inputScroll = new JScrollPane(input);
        inputScroll.setBorder(BorderFactory.createEmptyBorder());
        inputScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JButton replyBtn = new JButton("Reply");
        replyBtn.setBackground(new Color(29,161,242));
        replyBtn.setForeground(Color.WHITE);
        replyBtn.setFocusPainted(false);
        replyBtn.putClientProperty("keepColor", true);

        replyBtn.addActionListener(e -> {
            String txt = input.getText().trim();
            if (!txt.isEmpty()) {
                service.addComment(post.getPostId(), userId, txt);

                // ★ 댓글 리스트만 갱신
                loadComments();

                // ★ 입력창 비우기
                input.setText("");

                // ★ 스크롤 맨 아래로 자동 이동
                SwingUtilities.invokeLater(() -> {
                    JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(
                            JScrollPane.class, commentsContainer);
                    if (scrollPane != null) {
                        scrollPane.getVerticalScrollBar().setValue(
                                scrollPane.getVerticalScrollBar().getMaximum());
                    }
                });
            }
        });
        
        

        write.add(inputScroll, BorderLayout.CENTER);
        write.add(replyBtn, BorderLayout.EAST);

        main.add(write);

        // 전체 스크롤
        JScrollPane scroll = new JScrollPane(main);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        
         // ★ 추가: 폭이 부족할 때 가로 스크롤 자동 생성
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        return scroll;
      

    }
    
    // ★ 댓글 로딩 메서드
    private void loadComments() {
        commentsContainer.removeAll();

        List<Comment> comments = service.getComments(post.getPostId());

        if (comments.isEmpty()) {
            JLabel noCmt = new JLabel("No comments yet.");
            noCmt.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            noCmt.setForeground(Color.GRAY);
            noCmt.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            commentsContainer.add(noCmt);
        } else {
            for (Comment c : comments) {
                commentsContainer.add(new CommentCardPanel(service, userId, c));
                commentsContainer.add(Box.createVerticalStrut(10));
            }
        }

        commentsContainer.revalidate();
        commentsContainer.repaint();
    }
}