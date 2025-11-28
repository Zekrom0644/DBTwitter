package twitter.gui;

import twitter.model.Comment;

import javax.swing.*;
import java.awt.*;

public class CommentCardPanel extends JPanel {

    public CommentCardPanel(Comment c) {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230,236,240)));

        JLabel user = new JLabel("@" + c.getWriterId());
        user.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel content = new JLabel("<html>" + c.getContent() + "</html>");

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));

        top.add(user);
        top.add(Box.createVerticalStrut(5));
        top.add(content);

        add(top, BorderLayout.CENTER);
    }
}
