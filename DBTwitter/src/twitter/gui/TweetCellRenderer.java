package twitter.gui;

import twitter.model.Post;
import twitter.service.TwitterService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TweetCellRenderer extends JPanel implements ListCellRenderer<Post> {

    private final JLabel lblUser = new JLabel();
    private final JLabel lblContent = new JLabel();
    private final JButton btnLike = new JButton();
    private final JButton btnDislike = new JButton();

    private Post currentPost;

    public TweetCellRenderer() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0,0,1,0,new Color(230,236,240)));
        setBackground(Color.WHITE);

        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblContent.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnLike.setBorderPainted(false);
        btnLike.setBackground(Color.WHITE);
        btnLike.setFocusPainted(false);

        btnDislike.setBorderPainted(false);
        btnDislike.setBackground(Color.WHITE);
        btnDislike.setFocusPainted(false);

        // 버튼 클릭 이벤트 처리
        btnLike.addActionListener(e -> {
            if (currentPost != null) {
                currentPost.setNumLikes(currentPost.getNumLikes() + 1);
            }
        });

        btnDislike.addActionListener(e -> {
            if (currentPost != null) {
                currentPost.setNumDislikes(currentPost.getNumDislikes() + 1);
            }
        });

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(10,15,10,15));

        center.add(lblUser);
        center.add(Box.createVerticalStrut(5));
        center.add(lblContent);
        center.add(Box.createVerticalStrut(10));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bottom.setOpaque(false);
        bottom.add(btnLike);
        bottom.add(btnDislike);

        center.add(bottom);

        add(center, BorderLayout.CENTER);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends Post> list,
            Post value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        currentPost = value;

        lblUser.setText("@" + value.getWriterId());
        lblContent.setText("<html>" + value.getContent() + "</html>");

        btnLike.setText("♡ " + value.getNumLikes());
        btnDislike.setText("👎 " + value.getNumDislikes());

        setBackground(isSelected ? new Color(245,248,250) : Color.WHITE);

        return this;
    }
}
