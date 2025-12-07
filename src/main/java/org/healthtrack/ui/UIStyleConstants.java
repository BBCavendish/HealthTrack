package org.healthtrack.ui;

import javax.swing.*;
import java.awt.*;

/**
 * UI样式常量类 - 统一管理所有UI的颜色、字体和样式
 */
public class UIStyleConstants {
    
    // ==================== 颜色方案 ====================
    public static final Color PRIMARY_BLUE = new Color(52, 152, 219);     // 主色调 - 蓝色
    public static final Color SECONDARY_GREEN = new Color(46, 204, 113);  // 成功/添加 - 绿色
    public static final Color ACCENT_ORANGE = new Color(230, 126, 34);    // 强调色 - 橙色
    public static final Color DANGER_RED = new Color(231, 76, 60);        // 危险/删除 - 红色
    public static final Color PURPLE = new Color(155, 89, 182);           // 功能色 - 紫色
    public static final Color BACKGROUND = new Color(245, 247, 250);      // 背景色 - 浅灰蓝
    public static final Color CARD_BG = new Color(255, 255, 255);         // 卡片背景 - 白色
    public static final Color TEXT_PRIMARY = new Color(44, 62, 80);       // 主要文字 - 深灰蓝
    public static final Color TEXT_SECONDARY = new Color(127, 140, 141);  // 次要文字 - 灰色
    public static final Color HEADER_BG = new Color(236, 240, 241);       // 表头背景 - 浅灰
    public static final Color BORDER_COLOR = new Color(220, 224, 228);    // 边框色 - 浅灰
    
    // ==================== 字体 ====================
    public static final Font FONT_TITLE = new Font("Microsoft YaHei", Font.BOLD, 28);
    public static final Font FONT_SUBTITLE = new Font("Microsoft YaHei", Font.PLAIN, 16);
    public static final Font FONT_HEADING = new Font("Microsoft YaHei", Font.BOLD, 18);
    public static final Font FONT_BUTTON = new Font("Microsoft YaHei", Font.BOLD, 14);
    public static final Font FONT_LABEL = new Font("Microsoft YaHei", Font.BOLD, 14);
    public static final Font FONT_TEXT = new Font("Microsoft YaHei", Font.PLAIN, 13);
    public static final Font FONT_INPUT = new Font("Microsoft YaHei", Font.PLAIN, 14);
    
    // ==================== 工具方法 ====================
    
    /**
     * 创建现代化按钮
     */
    public static JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setBackground(bgColor);
        
        // 根据背景色亮度自动选择文字颜色
        // 计算亮度 (0-255)
        button.setForeground(TEXT_PRIMARY);
        
        button.setFocusPainted(false);
        button.setOpaque(true); // 确保按钮不透明，背景色能显示
        
        int borderThickness = 1;
        int padding = 10;
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), borderThickness),
            BorderFactory.createEmptyBorder(padding, 25, padding, 25)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 鼠标悬停效果
        final Color originalBg = bgColor;
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Color hoverColor = originalBg.brighter();
                button.setBackground(hoverColor);
                // 悬停时也调整文字颜色
                button.setForeground(TEXT_PRIMARY);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalBg);
                button.setForeground(TEXT_PRIMARY);
            }
        });
        
        return button;
    }
    
    /**
     * 创建现代化输入框
     */
    public static JTextField createModernTextField(int columns) {
        // 如果columns小于25，自动增加到25，确保输入框足够宽
        int actualColumns = Math.max(columns, 25);
        JTextField field = new JTextField(actualColumns);
        field.setFont(FONT_INPUT);
        field.setEditable(true); // 确保可编辑
        field.setEnabled(true); // 确保启用
        field.setBackground(Color.WHITE); // 设置白色背景
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setForeground(TEXT_PRIMARY);
        // 设置首选大小，确保输入框足够宽
        Dimension preferredSize = field.getPreferredSize();
        field.setPreferredSize(new Dimension(Math.max(preferredSize.width, 250), 35));
        return field;
    }
    
    /**
     * 创建现代化密码框
     */
    public static JPasswordField createModernPasswordField(int columns) {
        // 如果columns小于25，自动增加到25，确保输入框足够宽
        int actualColumns = Math.max(columns, 25);
        JPasswordField field = new JPasswordField(actualColumns);
        field.setFont(FONT_INPUT);
        field.setEditable(true); // 确保可编辑
        field.setEnabled(true); // 确保启用
        field.setBackground(Color.WHITE); // 设置白色背景
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setForeground(TEXT_PRIMARY);
        // 设置首选大小，确保输入框足够宽
        Dimension preferredSize = field.getPreferredSize();
        field.setPreferredSize(new Dimension(Math.max(preferredSize.width, 250), 35));
        return field;
    }
    
    /**
     * 创建卡片面板
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(25, 20, 25, 20)  // 增加上下内边距到25像素，左右20像素
        ));
        return panel;
    }
    
    /**
     * 创建标题标签
     */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_TITLE);
        label.setForeground(TEXT_PRIMARY);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
    
    /**
     * 创建副标题标签
     */
    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SUBTITLE);
        label.setForeground(TEXT_SECONDARY);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
    
    /**
     * 创建渐变背景面板
     */
    public static class GradientPanel extends JPanel {
        public GradientPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            GradientPaint gp = new GradientPaint(
                0, 0, new Color(245, 247, 250),
                getWidth(), getHeight(), new Color(228, 233, 240)
            );
            
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}

