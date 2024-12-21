import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.io.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class InventorySystem {
    // 商品类
    static class Product {
        private String id;          // 商品编号
        private String name;        // 商品名称
        private double price;       // 商品价格
        private int quantity;       // 库存数量
        private String category;    // 商品类别
        private String supplier;    // 供应商
        private double purchasePrice; // 进货价格
        
        public Product(String id, String name, double price, int quantity, String category, String supplier, double purchasePrice) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.category = category;
            this.supplier = supplier;
            this.purchasePrice = purchasePrice;
        }
        
        // getter和setter方法
        public String getId() { return id; }
        public String getName() { return name; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public String getCategory() { return category; }
        public String getSupplier() { return supplier; }
        public double getPurchasePrice() { return purchasePrice; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public void setPurchasePrice(double purchasePrice) { this.purchasePrice = purchasePrice; }
    }
    
    private Map<String, Product> inventory;  // 使用Map存储商品信息
    private List<String> transactions;       // 存储交易记录
    private List<PurchaseRecord> purchaseRecords; // 存储进货记录
    private List<SaleRecord> saleRecords;    // 存储销售记录
    private JFrame frame;
    private JPanel mainPanel;
    private String currentUser;              // 当前登录用户
    private Map<String, String> userAccounts;  // 存储用户账号信息
    
    // 进货记录类
    static class PurchaseRecord {
        private String productId;
        private int quantity;
        private double price;
        private Date date;
        private String supplier;
        
        public PurchaseRecord(String productId, int quantity, double price, String supplier) {
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
            this.date = new Date();
            this.supplier = supplier;
        }
        
        public String getProductId() { return productId; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
        public Date getDate() { return date; }
        public String getSupplier() { return supplier; }
    }

    // 销售记录类
    static class SaleRecord {
        private String productId;
        private int quantity;
        private double price;
        private Date date;
        private String customer;
        
        public SaleRecord(String productId, int quantity, double price, String customer) {
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
            this.date = new Date();
            this.customer = customer;
        }
        
        public String getProductId() { return productId; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
        public Date getDate() { return date; }
        public String getCustomer() { return customer; }
    }
    
    public InventorySystem() {
        inventory = new HashMap<>();
        transactions = new ArrayList<>();
        purchaseRecords = new ArrayList<>();
        saleRecords = new ArrayList<>();
        userAccounts = new HashMap<>();
        // 添加默认管理员账号
        userAccounts.put("admin", "admin");
        showLoginDialog();
    }
    
    private void showLoginDialog() {
        JDialog loginDialog = new JDialog((Frame)null, "辽宁工程技术大学进销存管理系统登录", true);
        loginDialog.setLayout(null);
        
        // 设置渐变背景
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(0, 51, 153), 
                                                    0, getHeight(), new Color(255, 255, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(null);
        backgroundPanel.setBounds(0, 0, 600, 500);
        
        // 创建标题标签
        JLabel titleLabel = new JLabel("辽宁工程技术大学", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 40, 600, 60);
        
        JLabel subtitleLabel = new JLabel("进销存管理系统", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setBounds(0, 100, 600, 40);
        
        // 创建圆角面板
        JPanel inputPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 220));
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
        };
        inputPanel.setLayout(null);
        inputPanel.setBounds(100, 160, 400, 280);
        inputPanel.setOpaque(false);
        
        // 创建输入框和标签
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        
        Font labelFont = new Font("微软雅黑", Font.PLAIN, 16);
        Font inputFont = new Font("微软雅黑", Font.PLAIN, 14);
        
        JLabel userLabel = new JLabel("用户名");
        JLabel passLabel = new JLabel("密  码");
        userLabel.setFont(labelFont);
        passLabel.setFont(labelFont);
        
        // 设置组件位置和样式
        userLabel.setBounds(50, 40, 80, 30);
        usernameField.setBounds(50, 75, 300, 35);
        passLabel.setBounds(50, 120, 80, 30);
        passwordField.setBounds(50, 155, 300, 35);
        
        usernameField.setFont(inputFont);
        passwordField.setFont(inputFont);
        
        // 自定义按钮样式
        JButton loginButton = new JButton("登录") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0, 51, 153), 
                                                    0, getHeight(), new Color(0, 102, 204));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                super.paintComponent(g);
            }
        };
        JButton registerButton = new JButton("注册") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0, 102, 204), 
                                                    0, getHeight(), new Color(0, 153, 255));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                super.paintComponent(g);
            }
        };
        
        loginButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        registerButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        
        loginButton.setForeground(Color.WHITE);
        registerButton.setForeground(Color.WHITE);
        loginButton.setContentAreaFilled(false);
        registerButton.setContentAreaFilled(false);
        loginButton.setBorderPainted(false);
        registerButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        registerButton.setFocusPainted(false);
        
        loginButton.setBounds(50, 210, 140, 40);
        registerButton.setBounds(210, 210, 140, 40);
        
        // 添加鼠标悬停效果
        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginButton.setFont(new Font("微软雅黑", Font.BOLD, 15));
            }
            public void mouseExited(MouseEvent e) {
                loginButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
            }
        });
        
        registerButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                registerButton.setFont(new Font("微软雅黑", Font.BOLD, 15));
            }
            public void mouseExited(MouseEvent e) {
                registerButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
            }
        });
        
        // 添加登录逻辑
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());
            
            if(userAccounts.containsKey(username) && 
               userAccounts.get(username).equals(password)) {
                currentUser = username;
                loginDialog.dispose();
                createAndShowGUI();
            } else {
                JOptionPane.showMessageDialog(loginDialog, 
                    "用户名或密码错误!", 
                    "登录失败", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        registerButton.addActionListener(e -> {
            loginDialog.dispose();
            showRegisterDialog();
        });
        
        // 添加组件到面板
        inputPanel.add(userLabel);
        inputPanel.add(usernameField);
        inputPanel.add(passLabel);
        inputPanel.add(passwordField);
        inputPanel.add(loginButton);
        inputPanel.add(registerButton);
        
        // 添加组件到背景面板
        backgroundPanel.add(titleLabel);
        backgroundPanel.add(inputPanel);
        
        // 添加背景面板到对话框
        loginDialog.add(backgroundPanel);
        
        loginDialog.setSize(600, 500);
        loginDialog.setLocationRelativeTo(null);
        loginDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        loginDialog.setVisible(true);
    }
    
    private void showRegisterDialog() {
        JDialog registerDialog = new JDialog((Frame)null, "用户注册", true);
        registerDialog.setLayout(null);
        
        // 设置渐变背景
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(135, 206, 235), 
                                                    0, getHeight(), new Color(255, 255, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(null);
        backgroundPanel.setBounds(0, 0, 500, 400);
        
        // 创建标题标签
        JLabel titleLabel = new JLabel("新用户注册", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setBounds(0, 20, 500, 40);
        
        // 创建输入面板
        JPanel inputPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 220));
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
            }
        };
        inputPanel.setLayout(null);
        inputPanel.setBounds(50, 80, 400, 280);
        inputPanel.setOpaque(false);
        
        Font labelFont = new Font("微软雅黑", Font.BOLD, 14);
        Font inputFont = new Font("微软雅黑", Font.PLAIN, 14);
        
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        
        JLabel userLabel = new JLabel("用户名:");
        JLabel passLabel = new JLabel("密  码:");
        JLabel confirmLabel = new JLabel("确认密码:");
        
        userLabel.setFont(labelFont);
        passLabel.setFont(labelFont);
        confirmLabel.setFont(labelFont);
        
        usernameField.setFont(inputFont);
        passwordField.setFont(inputFont);
        confirmPasswordField.setFont(inputFont);
        
        userLabel.setBounds(30, 30, 80, 30);
        usernameField.setBounds(120, 30, 250, 35);
        passLabel.setBounds(30, 80, 80, 30);
        passwordField.setBounds(120, 80, 250, 35);
        confirmLabel.setBounds(30, 130, 80, 30);
        confirmPasswordField.setBounds(120, 130, 250, 35);
        
        // 创建按钮
        JButton registerButton = new JButton("注册") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(52, 168, 83),
                        0, getHeight(), new Color(42, 148, 73));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                super.paintComponent(g);
            }
        };
        
        JButton cancelButton = new JButton("返回") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(234, 67, 53),
                        0, getHeight(), new Color(214, 47, 33));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                super.paintComponent(g);
            }
        };
        
        registerButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        cancelButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        
        registerButton.setForeground(Color.WHITE);
        cancelButton.setForeground(Color.WHITE);
        
        registerButton.setContentAreaFilled(false);
        cancelButton.setContentAreaFilled(false);
        registerButton.setBorderPainted(false);
        cancelButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        cancelButton.setFocusPainted(false);
        
        registerButton.setBounds(120, 190, 110, 35);
        cancelButton.setBounds(260, 190, 110, 35);
        
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 添加鼠标悬停效果
        registerButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                registerButton.setFont(new Font("微软雅黑", Font.BOLD, 15));
            }
            public void mouseExited(MouseEvent e) {
                registerButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
            }
        });
        
        cancelButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                cancelButton.setFont(new Font("微软雅黑", Font.BOLD, 15));
            }
            public void mouseExited(MouseEvent e) {
                cancelButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
            }
        });
        
        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());
            String confirmPassword = String.valueOf(confirmPasswordField.getPassword());
            
            if(username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(registerDialog, "用户名和密码不能为空!");
                return;
            }
            
            if(!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(registerDialog, "两次输入的密码不一致!");
                return;
            }
            
            if(userAccounts.containsKey(username)) {
                JOptionPane.showMessageDialog(registerDialog, "用户名已存在!");
                return;
            }
            
            userAccounts.put(username, password);
            JOptionPane.showMessageDialog(registerDialog, "注册成功!");
            registerDialog.dispose();
            showLoginDialog();
        });
        
        cancelButton.addActionListener(e -> {
            registerDialog.dispose();
            showLoginDialog();
        });
        
        // 添加组件到面板
        inputPanel.add(userLabel);
        inputPanel.add(usernameField);
        inputPanel.add(passLabel);
        inputPanel.add(passwordField);
        inputPanel.add(confirmLabel);
        inputPanel.add(confirmPasswordField);
        inputPanel.add(registerButton);
        inputPanel.add(cancelButton);
        
        backgroundPanel.add(titleLabel);
        backgroundPanel.add(inputPanel);
        
        registerDialog.add(backgroundPanel);
        registerDialog.setSize(500, 400);
        registerDialog.setLocationRelativeTo(null);
        registerDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        registerDialog.setVisible(true);
    }
    
    private void createAndShowGUI() {
        frame = new JFrame("进销存系统 - 当前用户: " + currentUser);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // 创建渐变背景面板
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(240, 248, 255),
                        0, getHeight(), new Color(255, 255, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        // 创建标题面板
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(70, 130, 180),
                        getWidth(), 0, new Color(100, 149, 237));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        titlePanel.setPreferredSize(new Dimension(800, 80));
        titlePanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("进销存管理系统", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // 创建主功能按钮面板
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 3, 20, 20));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // 创建功能按钮
        String[] buttonTexts = {
            "添加商品", "进货管理", "销售管理",
            "库存查询", "统计报表", "用户管理",
            "系统设置", "帮助", "退出系统"
        };
        Color[] buttonColors = {
            new Color(52, 152, 219), new Color(46, 204, 113), new Color(155, 89, 182),
            new Color(52, 73, 94), new Color(231, 76, 60), new Color(241, 196, 15),
            new Color(230, 126, 34), new Color(149, 165, 166), new Color(192, 57, 43)
        };

        for (int i = 0; i < buttonTexts.length; i++) {
            final int index = i;
            JButton button = new JButton(buttonTexts[i]) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gp = new GradientPaint(0, 0, buttonColors[index],
                            0, getHeight(), buttonColors[index].darker());
                    g2d.setPaint(gp);
                    g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                    super.paintComponent(g);
                }
            };
            button.setFont(new Font("微软雅黑", Font.BOLD, 20));
            button.setForeground(Color.WHITE);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // 添加鼠标悬停效果
            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    button.setFont(new Font("微软雅黑", Font.BOLD, 22));
                }
                public void mouseExited(MouseEvent e) {
                    button.setFont(new Font("微软雅黑", Font.BOLD, 20));
                }
            });

            // 添加按钮事件
            switch(i) {
                case 0: button.addActionListener(e -> showAddProductDialog()); break;
                case 1: button.addActionListener(e -> showPurchaseDialog()); break;
                case 2: button.addActionListener(e -> showSellDialog()); break;
                case 3: button.addActionListener(e -> showCheckInventoryDialog()); break;
                case 4: button.addActionListener(e -> showStatsDialog()); break;
                case 5: button.addActionListener(e -> showUserManagementDialog()); break;
                case 8: button.addActionListener(e -> {
                    frame.dispose();
                    showLoginDialog();
                }); break;
            }

            mainPanel.add(button);
        }

        // 添加状态栏
        JPanel statusBar = new JPanel();
        statusBar.setBackground(new Color(236, 240, 241));
        statusBar.setPreferredSize(new Dimension(800, 30));
        JLabel statusLabel = new JLabel("当前用户: " + currentUser + "    系统时间: " + new Date());
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusBar.add(statusLabel);

        // 组装界面
        backgroundPanel.add(titlePanel, BorderLayout.NORTH);
        backgroundPanel.add(mainPanel, BorderLayout.CENTER);
        backgroundPanel.add(statusBar, BorderLayout.SOUTH);
        frame.add(backgroundPanel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private void showAddProductDialog() {
        JDialog dialog = new JDialog(frame, "添加商品", true);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(new Color(240, 248, 255));
        
        // 创建标题面板
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(52, 152, 219),
                        getWidth(), 0, new Color(41, 128, 185));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        titlePanel.setBounds(0, 0, 500, 60);
        titlePanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("添加新商品", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // 创建输入面板
        JPanel inputPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 220));
                g2d.fillRoundRect(10, 10, getWidth()-20, getHeight()-20, 20, 20);
            }
        };
        inputPanel.setBounds(0, 60, 500, 340);
        inputPanel.setOpaque(false);
        
        Font labelFont = new Font("微软雅黑", Font.BOLD, 14);
        Font inputFont = new Font("微软雅黑", Font.PLAIN, 14);
        
        String[] labels = {"商品编号:", "商品名称:", "商品价格:", "初始库存:", "商品类别:", "供应商:"};
        JTextField[] fields = new JTextField[6];
        
        for(int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setFont(labelFont);
            label.setBounds(50, 30 + i*45, 80, 30);
            
            fields[i] = new JTextField();
            fields[i].setFont(inputFont);
            fields[i].setBounds(140, 30 + i*45, 280, 30);
            
            inputPanel.add(label);
            inputPanel.add(fields[i]);
        }
        
        // 创建按钮
        JButton confirmButton = new JButton("确认添加") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(46, 204, 113),
                        0, getHeight(), new Color(39, 174, 96));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                super.paintComponent(g);
            }
        };
        confirmButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setContentAreaFilled(false);
        confirmButton.setBorderPainted(false);
        confirmButton.setFocusPainted(false);
        confirmButton.setBounds(140, 300, 280, 35);
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        confirmButton.addActionListener(e -> {
            try {
                String id = fields[0].getText();
                String name = fields[1].getText();
                double price = Double.parseDouble(fields[2].getText());
                int quantity = Integer.parseInt(fields[3].getText());
                String category = fields[4].getText();
                String supplier = fields[5].getText();
                
                addProduct(id, name, price, quantity, category, supplier);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "请输入有效的数字!");
            }
        });
        
        inputPanel.add(confirmButton);
        
        dialog.add(titlePanel);
        dialog.add(inputPanel);
        
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(frame);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }
    
    private void showPurchaseDialog() {
        JDialog dialog = new JDialog(frame, "进货管理", true);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(new Color(240, 248, 255));
        
        // 创建标题面板
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(46, 204, 113),
                        getWidth(), 0, new Color(39, 174, 96));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        titlePanel.setBounds(0, 0, 500, 60);
        titlePanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("商品进货", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // 创建输入面板
        JPanel inputPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 220));
                g2d.fillRoundRect(10, 10, getWidth()-20, getHeight()-20, 20, 20);
            }
        };
        inputPanel.setBounds(0, 60, 500, 340);
        inputPanel.setOpaque(false);
        
        Font labelFont = new Font("微软雅黑", Font.BOLD, 14);
        Font inputFont = new Font("微软雅黑", Font.PLAIN, 14);
        
        // 添加输入组件
        JLabel idLabel = new JLabel("商品编号:");
        JLabel quantityLabel = new JLabel("进货数量:");
        JLabel supplierLabel = new JLabel("供应商:");
        JLabel priceLabel = new JLabel("进货价格:");
        
        JTextField idField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField supplierField = new JTextField();
        JTextField priceField = new JTextField();
        
        idLabel.setFont(labelFont);
        quantityLabel.setFont(labelFont);
        supplierLabel.setFont(labelFont);
        priceLabel.setFont(labelFont);
        
        idField.setFont(inputFont);
        quantityField.setFont(inputFont);
        supplierField.setFont(inputFont);
        priceField.setFont(inputFont);
        
        idLabel.setBounds(50, 30, 80, 30);
        idField.setBounds(140, 30, 280, 30);
        quantityLabel.setBounds(50, 80, 80, 30);
        quantityField.setBounds(140, 80, 280, 30);
        supplierLabel.setBounds(50, 130, 80, 30);
        supplierField.setBounds(140, 130, 280, 30);
        priceLabel.setBounds(50, 180, 80, 30);
        priceField.setBounds(140, 180, 280, 30);
        
        // 创建确认按钮
        JButton confirmButton = new JButton("确认进货") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(46, 204, 113),
                        0, getHeight(), new Color(39, 174, 96));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                super.paintComponent(g);
            }
        };
        confirmButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setContentAreaFilled(false);
        confirmButton.setBorderPainted(false);
        confirmButton.setFocusPainted(false);
        confirmButton.setBounds(140, 180, 200, 35);
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        confirmButton.addActionListener(e -> {
            try {
                String id = idField.getText();
                int quantity = Integer.parseInt(quantityField.getText());
                purchase(id, quantity);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "请输入有效的数字!");
            }
        });
        
        // 添加组件
        inputPanel.add(idLabel);
        dialog.add(confirmButton);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }
    
    private void showSellDialog() {
        JDialog dialog = new JDialog(frame, "销售管理", true);
        dialog.setLayout(new GridLayout(4, 2));
        
        JTextField idField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField customerField = new JTextField();
        
        Font font = new Font("宋体", Font.PLAIN, 22);  // 增大字体
        idField.setFont(font);
        quantityField.setFont(font);
        customerField.setFont(font);
        
        dialog.add(new JLabel("商品编号:")).setFont(font);
        dialog.add(idField);
        dialog.add(new JLabel("销售数量:")).setFont(font);
        dialog.add(quantityField);
        dialog.add(new JLabel("客户信息:")).setFont(font);
        dialog.add(customerField);
        
        JButton confirmButton = new JButton("确认");
        confirmButton.setFont(font);
        confirmButton.addActionListener(e -> {
            try {
                String id = idField.getText();
                int quantity = Integer.parseInt(quantityField.getText());
                sell(id, quantity);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "请输入有效的数字");
            }
        });
        
        dialog.add(confirmButton);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }
    
    private void showCheckInventoryDialog() {
        JDialog dialog = new JDialog(frame, "库存查询", true);
        dialog.setLayout(new BorderLayout(5, 5));
        
        JPanel searchPanel = new JPanel(new FlowLayout());
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("搜索");
        
        Font font = new Font("宋体", Font.PLAIN, 22);  // 增大字体
        searchField.setFont(font);
        searchButton.setFont(font);
        
        searchPanel.add(new JLabel("商品编号/名称:")).setFont(font);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // 创建表格显示库存信息
        String[] columnNames = {"编号", "名称", "价格", "库存", "类别", "供应商"};
        Object[][] data = new Object[inventory.size()][6];
        int i = 0;
        for(Product p : inventory.values()) {
            data[i][0] = p.getId();
            data[i][1] = p.getName();
            data[i][2] = p.getPrice();
            data[i][3] = p.getQuantity();
            data[i][4] = p.getCategory();
            data[i][5] = p.getSupplier();
            i++;
        }
        
        JTable table = new JTable(data, columnNames);
        table.setFont(font);
        table.getTableHeader().setFont(font);
        table.setRowHeight(30);  // 增加行高
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        dialog.add(searchPanel, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }
    
    private void showStatsDialog() {
        JDialog dialog = new JDialog(frame, "统计报表", true);
        dialog.setLayout(new BorderLayout(5, 5));
        
        JTabbedPane tabbedPane = new JTabbedPane();
        Font font = new Font("宋体", Font.PLAIN, 20);
        tabbedPane.setFont(font);
        
        // 销售统计面板
        JPanel salesPanel = new JPanel(new BorderLayout(5, 5));
        String[] salesColumns = {"商品编号", "商品名称", "销售数量", "销售金额", "毛利润"};
        DefaultTableModel salesModel = new DefaultTableModel(salesColumns, 0);
        JTable salesTable = new JTable(salesModel);
        salesTable.setFont(font);
        salesTable.getTableHeader().setFont(font);
        salesTable.setRowHeight(30);
        
        // 创建刷新按钮
        JButton refreshButton = new JButton("刷新数据");
        refreshButton.setFont(font);
        
        // 创建汇总信息面板
        JPanel salesSummary = new JPanel(new FlowLayout(FlowLayout.LEFT));
        salesSummary.setFont(font);
        
        // 创建刷新方法
        Runnable refreshData = () -> {
            salesModel.setRowCount(0);
            
            double totalSales = 0;
            double totalProfit = 0;
            
            for(SaleRecord record : saleRecords) {
                Product product = inventory.get(record.getProductId());
                double saleAmount = record.getQuantity() * record.getPrice();
                double profit = saleAmount - (record.getQuantity() * product.getPurchasePrice());
                
                salesModel.addRow(new Object[]{
                    record.getProductId(),
                    product.getName(),
                    record.getQuantity(),
                    String.format("%.2f", saleAmount),
                    String.format("%.2f", profit)
                });
                
                totalSales += saleAmount;
                totalProfit += profit;
            }
            
            // 更新汇总信息
            salesSummary.removeAll();
            salesSummary.add(new JLabel(String.format("总销售额: ¥%.2f    总利润: ¥%.2f", totalSales, totalProfit)));
            salesSummary.revalidate();
            salesSummary.repaint();
        };
        
        // 添加刷新按钮事件
        refreshButton.addActionListener(e -> refreshData.run());
        
        // 初始化数据
        refreshData.run();
        
        salesPanel.add(new JScrollPane(salesTable), BorderLayout.CENTER);
        salesPanel.add(salesSummary, BorderLayout.SOUTH);
        
        // 库存统计面板
        JPanel inventoryPanel = new JPanel(new BorderLayout(5, 5));
        String[] inventoryColumns = {"商品类别", "商品数量", "库存总值"};
        DefaultTableModel inventoryModel = new DefaultTableModel(inventoryColumns, 0);
        JTable inventoryTable = new JTable(inventoryModel);
        inventoryTable.setFont(font);
        inventoryTable.getTableHeader().setFont(font);
        inventoryTable.setRowHeight(30);
        
        // 按类别统计库存
        Map<String, Integer> categoryQuantity = new HashMap<>();
        Map<String, Double> categoryValue = new HashMap<>();
        
        for(Product product : inventory.values()) {
            String category = product.getCategory();
            categoryQuantity.merge(category, product.getQuantity(), Integer::sum);
            categoryValue.merge(category, 
                product.getQuantity() * product.getPurchasePrice(), Double::sum);
        }
        
        for(String category : categoryQuantity.keySet()) {
            inventoryModel.addRow(new Object[]{
                category,
                categoryQuantity.get(category),
                String.format("%.2f", categoryValue.get(category))
            });
        }
        
        inventoryPanel.add(new JScrollPane(inventoryTable), BorderLayout.CENTER);
        
        // 添加选项卡
        tabbedPane.addTab("销售统计", salesPanel);
        tabbedPane.addTab("库存统计", inventoryPanel);
        
        dialog.add(tabbedPane, BorderLayout.CENTER);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }
    
    private void showUserManagementDialog() {
        JDialog dialog = new JDialog(frame, "用户管理", true);
        dialog.setLayout(new BorderLayout(5, 5));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        Font font = new Font("宋体", Font.PLAIN, 22);  // 增大字体
        
        JButton addUserButton = new JButton("添加用户");
        JButton editUserButton = new JButton("编辑用户");
        JButton deleteUserButton = new JButton("删除用户");
        
        addUserButton.setFont(font);
        editUserButton.setFont(font);
        deleteUserButton.setFont(font);
        
        buttonPanel.add(addUserButton);
        buttonPanel.add(editUserButton);
        buttonPanel.add(deleteUserButton);
        
        // 用户列表
        String[] columnNames = {"用户名", "角色", "创建时间", "最后登录"};
        Object[][] data = {
            {"admin", "管理员", "2023-01-01", "2023-06-20"}
        };
        
        JTable table = new JTable(data, columnNames);
        table.setFont(font);
        table.getTableHeader().setFont(font);
        table.setRowHeight(30);  // 增加行高
        
        dialog.add(buttonPanel, BorderLayout.NORTH);
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }
    
    // 添加新商品
    public void addProduct(String id, String name, double price, int quantity, String category, String supplier) {
        try {
            if (inventory.containsKey(id)) {
                throw new IllegalArgumentException("商品已存在");
            }
            double purchasePrice = price * 0.8; // 假设进货价是售价的80%
            inventory.put(id, new Product(id, name, price, quantity, category, supplier, purchasePrice));
            recordTransaction("添加商品: " + id);
            JOptionPane.showMessageDialog(frame, "添加商品成功");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "添加商品失败: " + e.getMessage());
        }
    }
    
    // 进货
    public void purchase(String id, int quantity) {
        try {
            if (!inventory.containsKey(id)) {
                throw new IllegalArgumentException("商品不存在");
            }
            Product product = inventory.get(id);
            
            // 创建进货对话框
            JDialog purchaseDialog = new JDialog(frame, "进货管理", true);
            purchaseDialog.setLayout(new BorderLayout());
            
            // 创建表单面板
            JPanel formPanel = new JPanel(new GridLayout(4, 2, 15, 15));
            formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
            
            Font labelFont = new Font("微软雅黑", Font.BOLD, 14);
            Font fieldFont = new Font("微软雅黑", Font.PLAIN, 14);
            
            JLabel idLabel = new JLabel("商品编号:");
            JLabel nameLabel = new JLabel("商品名称:");
            JLabel quantityLabel = new JLabel("进货数量:");
            JLabel priceLabel = new JLabel("进货价格:");
            
            idLabel.setFont(labelFont);
            nameLabel.setFont(labelFont);
            quantityLabel.setFont(labelFont);
            priceLabel.setFont(labelFont);
            
            JTextField idField = new JTextField(id);
            JTextField nameField = new JTextField(product.getName());
            JTextField quantityField = new JTextField(String.valueOf(quantity));
            JTextField priceField = new JTextField(String.valueOf(product.getPurchasePrice()));
            
            idField.setFont(fieldFont);
            nameField.setFont(fieldFont);
            quantityField.setFont(fieldFont);
            priceField.setFont(fieldFont);
            
            idField.setEditable(false);
            nameField.setEditable(false);
            
            formPanel.add(idLabel);
            formPanel.add(idField);
            formPanel.add(nameLabel);
            formPanel.add(nameField);
            formPanel.add(quantityLabel);
            formPanel.add(quantityField);
            formPanel.add(priceLabel);
            formPanel.add(priceField);
            
            // 创建按钮面板
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
            
            JButton confirmButton = new JButton("确认进货") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gp = new GradientPaint(0, 0, new Color(52, 168, 83),
                            0, getHeight(), new Color(42, 148, 73));
                    g2d.setPaint(gp);
                    g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                    super.paintComponent(g);
                }
            };
            
            JButton cancelButton = new JButton("取消") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gp = new GradientPaint(0, 0, new Color(234, 67, 53),
                            0, getHeight(), new Color(214, 47, 33));
                    g2d.setPaint(gp);
                    g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                    super.paintComponent(g);
                }
            };
            
            confirmButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
            cancelButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
            
            confirmButton.setForeground(Color.WHITE);
            cancelButton.setForeground(Color.WHITE);
            
            confirmButton.setPreferredSize(new Dimension(120, 35));
            cancelButton.setPreferredSize(new Dimension(120, 35));
            
            confirmButton.setContentAreaFilled(false);
            cancelButton.setContentAreaFilled(false);
            confirmButton.setBorderPainted(false);
            cancelButton.setBorderPainted(false);
            confirmButton.setFocusPainted(false);
            cancelButton.setFocusPainted(false);
            
            confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            confirmButton.addActionListener(e -> {
                try {
                    int qty = Integer.parseInt(quantityField.getText());
                    double price = Double.parseDouble(priceField.getText());
                    
                    if(qty <= 0) {
                        JOptionPane.showMessageDialog(purchaseDialog, "进货数量必须大于0!");
                        return;
                    }
                    if(price <= 0) {
                        JOptionPane.showMessageDialog(purchaseDialog, "进货价格必须大于0!");
                        return;
                    }
                    
                    product.setQuantity(product.getQuantity() + qty);
                    product.setPurchasePrice(price);
                    recordTransaction("进货: " + id + ", 数量: " + qty + ", 单价: " + price);
                    JOptionPane.showMessageDialog(purchaseDialog, "进货成功!");
                    purchaseDialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(purchaseDialog, "请输入有效的数字!");
                }
            });
            
            cancelButton.addActionListener(e -> purchaseDialog.dispose());
            
            buttonPanel.add(confirmButton);
            buttonPanel.add(cancelButton);
            
            purchaseDialog.add(formPanel, BorderLayout.CENTER);
            purchaseDialog.add(buttonPanel, BorderLayout.SOUTH);
            
            purchaseDialog.setSize(500, 400);
            purchaseDialog.setLocationRelativeTo(frame);
            purchaseDialog.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "进货失败: " + e.getMessage());
        }
    }
    // 销售
    public void sell(String id, int quantity) {
        try {
            if (!inventory.containsKey(id)) {
                throw new IllegalArgumentException("商品不存在");
            }
            Product product = inventory.get(id);
            if (product.getQuantity() < quantity) {
                throw new IllegalArgumentException("库存不足");
            }
            product.setQuantity(product.getQuantity() - quantity);
            recordTransaction("销售: " + id + ", 数量: " + quantity);
            JOptionPane.showMessageDialog(frame, "销售成功");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "销售失败: " + e.getMessage());
        }
    }
    
    // 查询库存
    public void checkInventory(String id) {
        try {
            if (!inventory.containsKey(id)) {
                throw new IllegalArgumentException("商品不存在");
            }
            Product product = inventory.get(id);
            String message = String.format("商品信息:\n编号: %s\n名称: %s\n价格: %.2f\n库存: %d",
                product.getId(), product.getName(), product.getPrice(), product.getQuantity());
            JOptionPane.showMessageDialog(frame, message);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "查询失败: " + e.getMessage());
        }
    }
    
    // 记录交易
    private void recordTransaction(String transaction) {
        transactions.add(new Date() + ": " + transaction);
        saveTransactions();  // 保存到文件
    }
    
    // 保存交易记录到文件
    private void saveTransactions() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("transactions.txt", true))) {
            writer.println(transactions.get(transactions.size() - 1));
        } catch (IOException e) {
            System.out.println("保存交易记录失败: " + e.getMessage());
        }
    }
    
    // 主方法用于测试
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InventorySystem());
    }
}
