package twitter.gui;

import javax.swing.*;
import java.awt.*;

public class ThemeManager {

    public static boolean isDark = false;

    private static final Color DARK_BG = new Color(20, 20, 20);
    private static final Color DARK_TEXT = new Color(230, 230, 230);

    private static final Color LIGHT_BG = Color.WHITE;
    private static final Color LIGHT_TEXT = Color.BLACK;

    public static void applyTheme(Component comp) {
        if (comp == null) return;

        // JButton이면서 keepColor=true 면 색 보존
        if (comp instanceof JButton btn) {
            Object flag = btn.getClientProperty("keepColor");
            if (flag instanceof Boolean && (Boolean) flag) {
                // 고유 색상 보존 → 테마 적용하지 않음
                return;
            }
        }
        
     // ★ JTextField 전용 테마
        if (comp instanceof JTextField tf) {

            if (isDark) {
                tf.setBackground(new Color(34, 34, 34));   // 짙은 회색
                tf.setForeground(Color.WHITE);
                tf.setCaretColor(Color.WHITE);
                tf.setBorder(BorderFactory.createLineBorder(new Color(85, 85, 85)));  // 중간 회색
            } else {
                tf.setBackground(Color.WHITE);
                tf.setForeground(Color.BLACK);
                tf.setCaretColor(Color.BLACK);
                tf.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150))); // 연한 회색
            }
            return;
        }

        // ★ JPasswordField 전용 테마
        if (comp instanceof JPasswordField pf) {

            if (isDark) {
                pf.setBackground(new Color(34, 34, 34));
                pf.setForeground(Color.WHITE);
                pf.setCaretColor(Color.WHITE);
                pf.setBorder(BorderFactory.createLineBorder(new Color(85, 85, 85)));
            } else {
                pf.setBackground(Color.WHITE);
                pf.setForeground(Color.BLACK);
                pf.setCaretColor(Color.BLACK);
                pf.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
            }
            return;
        }


        // 일반 패널/라벨/버튼 테마 적용
        if (isDark) {
            comp.setBackground(DARK_BG);
            comp.setForeground(DARK_TEXT);
        } else {
            comp.setBackground(LIGHT_BG);
            comp.setForeground(LIGHT_TEXT);
        }

        // 자식까지 재귀 적용
        if (comp instanceof Container cont) {
            for (Component child : cont.getComponents()) {
                applyTheme(child);
            }
        }
    }
}
