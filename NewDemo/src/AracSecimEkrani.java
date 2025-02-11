import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class AracSecimEkrani extends JFrame implements KeyListener, ActionListener {
    private int selectedCarIndex = 0; // Seçilen aracın indeksi
    private final String[] carImages = {
            "./assets/gamecar1.png",
            "./assets/gamecar2.png",
            "./assets/gamecar3.png",
            "./assets/gamecar4.png"
    };
    private final String[] carNames = {"Sedan", "SuperSport", "PistArac", "Off-Road"};
    private Timer timer;
    private int ypos = 0;
    private String kullaniciAdi; // Kullanıcı adı değişkeni eklendi

    public AracSecimEkrani(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;
        setTitle("Araç Seçim Ekranı");
        setBounds(300, 10, 700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        addKeyListener(this);

        // Hareketli arkaplan için zamanlayıcı
        timer = new Timer(50, this); // ActionListener'a bağlandı
        timer.start();
    }

    @Override
    public void paint(Graphics g) {
        // Create offscreen buffer
        Image offscreen = createImage(getWidth(), getHeight());
        Graphics2D g2d = (Graphics2D) offscreen.getGraphics();
        
        // Enable antialiasing for smoother rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Arkaplan çizimi
        g2d.setColor(new Color(34, 139, 34)); // Koyu yeşil arkaplan
        g2d.fillRect(0, 0, 700, 700);
        
        // Yol kenarları
        g2d.setColor(Color.GRAY);
        g2d.fillRect(90, 0, 10, 700);
        g2d.fillRect(600, 0, 10, 700);
        
        // Yol
        g2d.setColor(Color.BLACK);
        g2d.fillRect(100, 0, 500, 700);

        // Yol çizgileri
        g2d.setColor(Color.WHITE);
        for (int i = 0; i <= 700; i += 100) {
            g2d.fillRect(350, (i + ypos) % 700, 10, 70);
        }

        // Başlık
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Araç Seçimi", 270, 100);

        // Araç görselleri
        for (int i = 0; i < carImages.length; i++) {
            ImageIcon carIcon = new ImageIcon(carImages[i]);
            int x = 150 + i * 120;
            int y = 300;
            
            // Seçilen aracın arka planı
            if (i == selectedCarIndex) {
                g2d.setColor(new Color(255, 215, 0, 100)); // Yarı saydam altın rengi
                g2d.fillRect(x - 10, y - 40, carIcon.getIconWidth() + 20, carIcon.getIconHeight() + 60);
                g2d.setColor(Color.YELLOW);
                g2d.drawRect(x - 10, y - 40, carIcon.getIconWidth() + 20, carIcon.getIconHeight() + 60);
            }
            
            carIcon.paintIcon(this, g2d, x, y);
        }

        // Araç isimleri
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < carNames.length; i++) {
            int x = 150 + i * 120;
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(carNames[i]);
            g2d.drawString(carNames[i], x + (carImages[i].length() / 2) - (textWidth / 2), 270);
        }

        // Bilgilendirme paneli
        g2d.setColor(new Color(0, 0, 0, 150)); // Yarı saydam siyah
        g2d.fillRect(150, 500, 400, 100);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(150, 500, 400, 100);
        
        // Bilgilendirme metinleri
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Araç seçmek için ← → yön tuşlarını kullanın", 200, 530);
        g2d.drawString("Oyuna başlamak için ENTER'a basın", 220, 560);

        // Draw the offscreen buffer to the screen
        g.drawImage(offscreen, 0, 0, this);
        
        // Clean up
        g2d.dispose();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            selectedCarIndex = (selectedCarIndex - 1 + carImages.length) % carImages.length;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            selectedCarIndex = (selectedCarIndex + 1) % carImages.length;
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            timer.stop();
            dispose();
            User.getInstance().SetUserCarIndex(selectedCarIndex);
            new CarGame(carNames[selectedCarIndex], kullaniciAdi).setVisible(true);
        }
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == timer) {
            ypos += 10; // Yol hareketini kontrol etmek için
            repaint();
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AracSecimEkrani aracSecimEkrani = new AracSecimEkrani("Kullanıcı Adı");
            aracSecimEkrani.setVisible(true);
        });
    }
}
