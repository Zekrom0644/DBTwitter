package twitter.gui;

import twitter.model.Comment;
import twitter.service.TwitterService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CommentDialog extends JDialog {

    private final TwitterService service;
    private final String userId;
    private final String postId;

    private final DefaultListModel<Comment> commentModel = new DefaultListModel<>();
    private final JList<Comment> commentList = new JList<>(commentModel);

    public CommentDialog(Window window, TwitterService service,
                         String userId, String postId) {
        super(window, "Comments");
        this.service = service;
        this.userId = userId;
        this.postId = postId;

        setSize(500, 500);
        setLocationRelativeTo(window);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        initUI();
        loadComments();
    }

    private void initUI() {
        /* ---------------------- 상단 제목 ---------------------- */
        JLabel title = new JLabel("Comments", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        add(title, BorderLayout.NORTH);

        /* ---------------------- 댓글 리스트 ---------------------- */
        commentList.setCellRenderer(new CommentRenderer());
        commentList.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(commentList);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);

        /* ---------------------- 댓글 작성 영역 ---------------------- */
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JTextField txt = new JTextField();
        bottom.add(txt, BorderLayout.CENTER);

        JButton replyBtn = new JButton("Reply");
        replyBtn.setPreferredSize(new Dimension(80, 36));
        replyBtn.setBackground(new Color(29, 161, 242));
        replyBtn.setForeground(Color.WHITE);
        replyBtn.putClientProperty("JButton.buttonType", "roundRect");

        replyBtn.addActionListener(e -> {
            String content = txt.getText().trim();
            if (content.isEmpty()) return;

            // DB 저장
            // service.addComment(postId, userId, content);

            txt.setText("");
            loadComments();
        });

        bottom.add(replyBtn, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadComments() {
        commentModel.clear();

        // DB 연동 전 → 더미
        List<Comment> list = service.getComments(postId);

        for (Comment c : list)
            commentModel.addElement(c);
    }

    /* ---------------------- 댓글 렌더러 ---------------------- */
    static class CommentRenderer extends JPanel implements ListCellRenderer<Comment> {

        private final JLabel lblUser = new JLabel();
        private final JLabel lblText = new JLabel();

        public CommentRenderer() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
                    new Color(230, 236, 240)));
            setBackground(Color.WHITE);

            lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblText.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            JPanel center = new JPanel();
            center.setOpaque(false);
            center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
            center.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            center.add(lblUser);
            center.add(lblText);

            add(center, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Comment> list,
                                                      Comment value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            lblUser.setText("@" + value.getWriterId());
            lblText.setText(value.getContent());

            setBackground(isSelected ?
                    new Color(245, 248, 250) : Color.WHITE);
            return this;
        }
    }
}
