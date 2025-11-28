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
    private final Runnable goBack;

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

        JLabel lbl = new JLabel("Tweet");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 26));

        header.add(backBtn, BorderLayout.WEST);
        header.add(lbl, BorderLayout.CENTER);

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
         * 포스트 본문
         * -------------------- */
        JPanel postPanel = new JPanel();
        postPanel.setLayout(new BoxLayout(postPanel, BoxLayout.Y_AXIS));
        postPanel.setBackground(new Color(245, 248, 250));
        postPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        postPanel.setMinimumSize(new Dimension(0, 450));  // 화면 2/3 정도 높이
        postPanel.setPreferredSize(new Dimension(0, 450));

        JLabel writer = new JLabel("@" + post.getWriterId());
        writer.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel content = new JLabel("<html>" + post.getContent() + "</html>");
        content.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        postPanel.add(writer);
        postPanel.add(Box.createVerticalStrut(10));
        postPanel.add(content);
        postPanel.add(Box.createVerticalStrut(20));

        /* 버튼 */
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btns.setOpaque(false);

        JButton like = new JButton("♡ " + post.getNumLikes());
        JButton dislike = new JButton("👎 " + post.getNumDislikes());
        JButton share = new JButton("⟳ Share");
        JButton report = new JButton("⚠ Report");

        btns.add(like);
        btns.add(dislike);
        btns.add(share);
        btns.add(report);

        postPanel.add(btns);

        main.add(postPanel);
        main.add(Box.createVerticalStrut(20));

        /* --------------------
         * 댓글 목록
         * -------------------- */
        List<Comment> comments = service.getComments(post.getPostId());

        for (Comment c : comments) {
            main.add(buildCommentCard(c));
            main.add(Box.createVerticalStrut(10));
        }

        main.add(Box.createVerticalStrut(15));

        /* --------------------
         * 댓글 입력창
         * -------------------- */
        JPanel write = new JPanel(new BorderLayout());
        write.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JTextArea input = new JTextArea();
        input.setLineWrap(true);
        input.setWrapStyleWord(true);
        input.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        input.setRows(3);

        JScrollPane inputScroll = new JScrollPane(input);
        inputScroll.setBorder(BorderFactory.createEmptyBorder());

        JButton replyBtn = new JButton("Reply");
        replyBtn.setPreferredSize(new Dimension(80, 40));
        replyBtn.addActionListener(e -> {
            String txt = input.getText().trim();
            if (!txt.isEmpty()) {
                service.addComment(post.getPostId(), userId, txt);
                goBack.run(); // 타임라인 새로고침
            }
        });

        write.add(inputScroll, BorderLayout.CENTER);
        write.add(replyBtn, BorderLayout.EAST);

        main.add(write);

        JScrollPane scroll = new JScrollPane(main);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        return scroll;
    }

    /* ======================================================
     * 댓글 카드
     * ====================================================== */
    private JPanel buildCommentCard(Comment c) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel writer = new JLabel("@" + c.getWriterId());
        writer.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel text = new JLabel("<html>" + c.getContent() + "</html>");
        text.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        card.add(writer);
        card.add(Box.createVerticalStrut(5));
        card.add(text);

        return card;
    }
}
