package org.healthtrack.ui;

import org.healthtrack.entity.User;
import org.healthtrack.service.ProviderService;
import org.healthtrack.service.UserProviderLinkService;
import org.healthtrack.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 账户信息管理页面 - 导航到各个子页面
 */
public class AccountManagementFrame extends JFrame {
    
    private final UserService userService;
    private final ProviderService providerService;
    private final UserProviderLinkService userProviderLinkService;
    private final User currentUser;
    
    public AccountManagementFrame(UserService userService, 
                                 ProviderService providerService,
                                 UserProviderLinkService userProviderLinkService,
                                 User currentUser) {
        super("HealthTrack - 账户信息管理");
        this.userService = userService;
        this.providerService = providerService;
        this.userProviderLinkService = userProviderLinkService;
        this.currentUser = currentUser;
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        initUI();
    }
    
    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UIStyleConstants.BACKGROUND);
        
        UIStyleConstants.GradientPanel mainPanel = new UIStyleConstants.GradientPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // 标题
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = UIStyleConstants.createTitleLabel("账户信息管理");
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // 菜单按钮
        JPanel menuPanel = UIStyleConstants.createCardPanel();
        menuPanel.setLayout(new GridLayout(2, 2, 20, 20));
        
        JButton personalInfoButton = createMenuButton("个人信息修改", UIStyleConstants.PRIMARY_BLUE,
            e -> openPersonalInfoFrame());
        
        JButton emailButton = createMenuButton("邮箱管理", UIStyleConstants.SECONDARY_GREEN,
            e -> openEmailManagementFrame());
        
        JButton phoneButton = createMenuButton("电话管理", UIStyleConstants.ACCENT_ORANGE,
            e -> openPhoneManagementFrame());
        
        JButton providerButton = createMenuButton("医疗服务提供者管理", UIStyleConstants.PURPLE,
            e -> openProviderManagementFrame());
        
        menuPanel.add(personalInfoButton);
        menuPanel.add(emailButton);
        menuPanel.add(phoneButton);
        menuPanel.add(providerButton);
        
        // 返回按钮
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        bottomPanel.setOpaque(false);
        JButton backButton = UIStyleConstants.createModernButton("返回主菜单", UIStyleConstants.TEXT_SECONDARY);
        backButton.addActionListener(e -> dispose());
        bottomPanel.add(backButton);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(menuPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setSize(700, 500);
        setLocationRelativeTo(null);
    }
    
    private JButton createMenuButton(String text, Color color, ActionListener listener) {
        JButton button = new JButton("<html><center>" + text + "</center></html>");
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        button.setBackground(color);
        
        // 根据背景色亮度自动选择文字颜色
        double brightness = (color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114);
        if (brightness > 128) {
            button.setForeground(UIStyleConstants.TEXT_PRIMARY);
        } else {
            button.setForeground(UIStyleConstants.TEXT_PRIMARY);
        }
        
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(40, 20, 40, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(listener);
        
        final Color originalColor = color;
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                Color hoverColor = originalColor.brighter();
                button.setBackground(hoverColor);
                double hoverBrightness = (hoverColor.getRed() * 0.299 + hoverColor.getGreen() * 0.587 + hoverColor.getBlue() * 0.114);
                if (hoverBrightness > 128) {
                    button.setForeground(UIStyleConstants.TEXT_PRIMARY);
                } else {
                    button.setForeground(UIStyleConstants.TEXT_PRIMARY);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
                double brightness = (originalColor.getRed() * 0.299 + originalColor.getGreen() * 0.587 + originalColor.getBlue() * 0.114);
                if (brightness > 128) {
                    button.setForeground(UIStyleConstants.TEXT_PRIMARY);
                } else {
                    button.setForeground(UIStyleConstants.TEXT_PRIMARY);
                }
            }
        });
        
        return button;
    }
    
    private void openPersonalInfoFrame() {
        PersonalInfoFrame frame = new PersonalInfoFrame(userService, currentUser);
        frame.setVisible(true);
    }
    
    private void openEmailManagementFrame() {
        EmailManagementFrame frame = new EmailManagementFrame(userService, currentUser);
        frame.setVisible(true);
    }
    
    private void openPhoneManagementFrame() {
        PhoneManagementFrame frame = new PhoneManagementFrame(userService, currentUser);
        frame.setVisible(true);
    }
    
    private void openProviderManagementFrame() {
        ProviderManagementFrame frame = new ProviderManagementFrame(userService, providerService, 
            userProviderLinkService, currentUser);
        frame.setVisible(true);
    }
}

