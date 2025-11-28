package twitter.gui;

import twitter.model.Post;
import twitter.service.TwitterService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TweetCardPanel extends JPanel {

    private final TwitterService service;
    private final Post post;
    private final Runnable refreshAction;
    private final Runnable openDetail;

    private final Color normalColor = Color.WHITE;
    private final Color hoverColor = new Color(245, 248, 250);
    private final Color clickColor = new Color(235, 241, 245);

    private boolean clicked = false;

    public TweetCardPanel(
            TwitterService service,
            Post post,
            Runnable refreshAction,
            Runnable openDetail
    ) {
        this.service = service;
        this.post = post;
        this.refreshAction = refreshAction;
        this.openDetail = openDetail;

        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(normalColor);

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 236, 240)));

        // 트위터 카드 폭 고정 + 중앙 정렬
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        /* ------------------------------------
         * 상단(작성자 + 내용)
         * ------------------------------------ */
        JLabel lblUser = new JLabel("@" + post.getWriterId());
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel lblContent = new JLabel("<html>" + post.getContent() + "</html>");
        lblContent.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        top.add(lblUser);
        top.add(Box.createVerticalStrut(6));
        top.add(lblContent);

        add(top, BorderLayout.CENTER);

        /* ------------------------------------
         * 좋아요 / 싫어요 버튼
         * ------------------------------------ */
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        btnPanel.setOpaque(false);

        JButton btnLike = new JButton("♡ " + post.getNumLikes());
        JButton btnDislike = new JButton("👎 " + post.getNumDislikes());

        btnLike.setFocusPainted(false);
        btnDislike.setFocusPainted(false);

        btnLike.addActionListener(e -> {
            post.setNumLikes(post.getNumLikes() + 1);
            btnLike.setText("♡ " + post.getNumLikes());
            if (refreshAction != null) refreshAction.run();
        });

        btnDislike.addActionListener(e -> {
            post.setNumDislikes(post.getNumDislikes() + 1);
            btnDislike.setText("👎 " + post.getNumDislikes());
            if (refreshAction != null) refreshAction.run();
        });

        btnPanel.add(btnLike);
        btnPanel.add(btnDislike);

        add(btnPanel, BorderLayout.SOUTH);

        /* ------------------------------------
         * 마우스 이벤트 (Hover, Single Click, Double Click)
         * ------------------------------------ */
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!clicked) setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!clicked) setBackground(normalColor);
            }

            @Override
            public void mouseClicked(MouseEvent e) {

                // 단일 클릭 → 진한 색 유지
                if (e.getClickCount() == 1) {
                    clicked = true;
                    setBackground(clickColor);
                }

                // 더블 클릭 → 상세 화면으로 이동
                if (e.getClickCount() == 2) {
                    if (openDetail != null) openDetail.run();
                }
            }
        });
    }
}
