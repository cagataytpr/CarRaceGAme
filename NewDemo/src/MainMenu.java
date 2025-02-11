import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.Random;

public class MainMenu extends JFrame {
    private String kullaniciAdi;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel menuPanel;
    private JPanel leaderboardPanel;
    private JPanel settingsPanel;
    private Timer animationTimer;
    private int ypos = 0;
    private Random random = new Random();
    private int[] carYPositions;
    private int[] carXPositions;
    private ImageIcon[] carIcons;

    public MainMenu(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
        setupFrame();
        setupAnimationAssets();
        createPanels();
        startAnimation();
        setVisible(true);
    }

    private void setupAnimationAssets() {
        // Araba konumlarını başlat
        carYPositions = new int[3];
        carXPositions = new int[3];
        carIcons = new ImageIcon[3];
        
        // Rasgele başlangıç pozisyonları
        for (int i = 0; i < 3; i++) {
            carYPositions[i] = -100 - random.nextInt(300);
            carXPositions[i] = 150 + random.nextInt(300);
            carIcons[i] = new ImageIcon("./assets/gamecar" + (i + 1) + ".png");
        }
    }

    private void startAnimation() {
        animationTimer = new Timer(50, e -> {
            ypos = (ypos + 10) % 700;
            
            // Arabaları hareket ettir
            for (int i = 0; i < carYPositions.length; i++) {
                carYPositions[i] += 5;
                if (carYPositions[i] > 700) {
                    carYPositions[i] = -100;
                    carXPositions[i] = 150 + random.nextInt(300);
                }
            }
            
            repaint();
        });
        animationTimer.start();
    }

    private void setupFrame() {
        setTitle("Car Game - Ana Menü");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void createPanels() {
        // Ana panel ve card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawAnimatedBackground(g);
            }
        };
        mainPanel.setOpaque(false);
        add(mainPanel);
        
        // Alt panelleri oluştur
        createMenuPanel();
        createLeaderboardPanel();
        createSettingsPanel();
        
        // Panelleri ana panele ekle
        mainPanel.add(menuPanel, "menu");
        mainPanel.add(leaderboardPanel, "leaderboard");
        mainPanel.add(settingsPanel, "settings");
        
        // Başlangıçta ana menüyü göster
        cardLayout.show(mainPanel, "menu");
    }

    private void drawAnimatedBackground(Graphics g) {
        // Arkaplan
        g.setColor(new Color(34, 139, 34));
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // Yol
        g.setColor(Color.BLACK);
        g.fillRect(100, 0, 500, getHeight());
        
        // Yol kenarları
        g.setColor(Color.GRAY);
        g.fillRect(90, 0, 10, getHeight());
        g.fillRect(600, 0, 10, getHeight());
        
        // Yol çizgileri
        g.setColor(Color.WHITE);
        for (int i = 0; i <= getHeight(); i += 100) {
            g.fillRect(350, (i + ypos) % getHeight(), 10, 70);
        }
        
        // Hareketli arabalar
        for (int i = 0; i < carIcons.length; i++) {
            carIcons[i].paintIcon(this, g, carXPositions[i], carYPositions[i]);
        }
    }

    private void createMenuPanel() {
        menuPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                // Panel şeffaf olsun
            }
        };
        menuPanel.setOpaque(false);
        
        // Hoş geldin mesajı
        JLabel welcomeLabel = new JLabel("Hoş Geldin, " + kullaniciAdi);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(300, 50, 300, 40);
        menuPanel.add(welcomeLabel);
        
        // Butonları oluştur
        createMenuButton("Oyna", 300, 150, e -> startGame());
        createMenuButton("Sıralama", 300, 250, e -> cardLayout.show(mainPanel, "leaderboard"));
        createMenuButton("Ayarlar", 300, 350, e -> cardLayout.show(mainPanel, "settings"));
        createMenuButton("Çıkış", 300, 450, e -> System.exit(0));
    }

    private void createMenuButton(String text, int x, int y, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBounds(x, y, 200, 50);
        button.addActionListener(action);
        button.setBackground(new Color(0, 100, 0));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        menuPanel.add(button);
    }

    private void createLeaderboardPanel() {
        leaderboardPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                // Panel şeffaf olsun
            }
        };
        leaderboardPanel.setOpaque(false);
        
        // Başlık
        JLabel titleLabel = new JLabel("En Yüksek Skorlar", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        leaderboardPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Skor tablosu
        String[] columnNames = {"Sıra", "Kullanıcı", "Skor"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowHeight(30);
        
        // Veritabanından skorları çek
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT kullanici, skor FROM kullanicigiris ORDER BY skor DESC LIMIT 10")) {
            
            ResultSet rs = stmt.executeQuery();
            int rank = 1;
            while (rs.next()) {
                model.addRow(new Object[]{
                    rank++,
                    rs.getString("kullanici"),
                    rs.getInt("skor")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        leaderboardPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Geri dönüş butonu
        JButton backButton = new JButton("Ana Menüye Dön");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menu"));
        leaderboardPanel.add(backButton, BorderLayout.SOUTH);
    }

    private void createSettingsPanel() {
        settingsPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // Panel şeffaf olsun
            }
        };
        settingsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Ses ayarı
        JLabel volumeLabel = new JLabel("Ses Seviyesi:");
        volumeLabel.setForeground(Color.WHITE);
        JSlider volumeSlider = new JSlider(0, 100, 50);
        
        // Zorluk seviyesi
        JLabel difficultyLabel = new JLabel("Zorluk Seviyesi:");
        difficultyLabel.setForeground(Color.WHITE);
        String[] difficulties = {"Kolay", "Orta", "Zor"};
        JComboBox<String> difficultyCombo = new JComboBox<>(difficulties);
        
        // Araç seçimi
        JLabel carLabel = new JLabel("Varsayılan Araç:");
        carLabel.setForeground(Color.WHITE);
        String[] cars = {"Sedan", "OffRoad", "SuperSport", "PistArac"};
        JComboBox<String> carCombo = new JComboBox<>(cars);
        
        // Geri dönüş butonu
        JButton backButton = new JButton("Ana Menüye Dön");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "menu"));
        
        // Bileşenleri panele ekle
        gbc.gridx = 0; gbc.gridy = 0;
        settingsPanel.add(volumeLabel, gbc);
        gbc.gridx = 1;
        settingsPanel.add(volumeSlider, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        settingsPanel.add(difficultyLabel, gbc);
        gbc.gridx = 1;
        settingsPanel.add(difficultyCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        settingsPanel.add(carLabel, gbc);
        gbc.gridx = 1;
        settingsPanel.add(carCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        settingsPanel.add(backButton, gbc);
    }

    private void startGame() {
        this.dispose(); // Ana menüyü kapat
        SwingUtilities.invokeLater(() -> {
            AracSecimEkrani aracSecimEkrani = new AracSecimEkrani(kullaniciAdi);
            aracSecimEkrani.setVisible(true);
        });
    }

    // Test için main metodu
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu("Test User"));
    }
}
