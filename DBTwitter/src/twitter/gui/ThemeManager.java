package twitter.gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.JTextComponent;

import java.awt.*;

public class ThemeManager {

    public static boolean isDark = false;

    // 다크/라이트 기본 색
    private static final Color DARK_BG = new Color(20, 20, 20);
    private static final Color DARK_TEXT = new Color(230, 230, 230);

    private static final Color LIGHT_BG = Color.WHITE;
    private static final Color LIGHT_TEXT = Color.BLACK;

    // 필드 배경
    private static final Color DARK_FIELD_BG = new Color(34, 34, 34);
    private static final Color DARK_FIELD_BORDER = new Color(85, 85, 85);

    private static final Color LIGHT_FIELD_BORDER = new Color(160, 160, 160);

    // 다크모드 버튼 색
    private static final Color DARK_BUTTON_BG = new Color(45, 45, 45);
    private static final Color LIGHT_BUTTON_BG = new Color(240, 240, 240);


    /* ===========================================================
     *  applyTheme()
     * =========================================================== */
    public static void applyTheme(Component comp) {

        if (comp == null) return;

        /* -----------------------------------------------------------
         * 1) JButton + keepColor → 색상 유지
         * ----------------------------------------------------------- */
        if (comp instanceof JButton btn) {
            Object flag = btn.getClientProperty("keepColor");

            if (flag instanceof Boolean && (Boolean) flag) {
                // 고유 색상 유지 (예: 파란색 버튼, 빨간색 Delete 버튼 등)
                return;
            }

            // 일반 버튼(keepColor 없는 경우)
            if (isDark) {
                btn.setBackground(DARK_BUTTON_BG);
                btn.setForeground(DARK_TEXT);
            } else {
                btn.setBackground(LIGHT_BUTTON_BG);
                btn.setForeground(LIGHT_TEXT);
            }
        }

        /* -----------------------------------------------------------
         * 2) JTextField / JPasswordField  (TitledBorder 유지)
         * ----------------------------------------------------------- */
        if (comp instanceof JTextField tf) {
            applyFieldTheme(tf);
            return;
        }

        if (comp instanceof JPasswordField pf) {
            applyFieldTheme(pf);
            return;
        }


        /* -----------------------------------------------------------
         * 3) 기본 컴포넌트 색상
         * ----------------------------------------------------------- */
        if (isDark) {
            comp.setBackground(DARK_BG);
            comp.setForeground(DARK_TEXT);
        } else {
            comp.setBackground(LIGHT_BG);
            comp.setForeground(LIGHT_TEXT);
        }

        /* -----------------------------------------------------------
         * 4) 자식 컴포넌트 재귀 적용
         * ----------------------------------------------------------- */
        if (comp instanceof Container cont) {
            for (Component child : cont.getComponents()) {
                applyTheme(child);
            }
        }
    }


    /* ===========================================================
     *  텍스트필드 / 패스워드필드 전용 테마 적용
     * =========================================================== */
    private static void applyFieldTheme(JTextComponent field) {

        if (isDark) {
            field.setBackground(DARK_FIELD_BG);
            field.setForeground(Color.WHITE);
            field.setCaretColor(Color.WHITE);

            Border titled = field.getBorder();
            if (titled instanceof TitledBorder tb) {
                tb.setTitleColor(Color.WHITE);
                tb.setBorder(new LineBorder(DARK_FIELD_BORDER));
            }

        } else {
            field.setBackground(Color.WHITE);
            field.setForeground(Color.BLACK);
            field.setCaretColor(Color.BLACK);

            Border titled = field.getBorder();
            if (titled instanceof TitledBorder tb) {
                tb.setTitleColor(Color.BLACK);
                tb.setBorder(new LineBorder(LIGHT_FIELD_BORDER));
            }
        }
    }
}
