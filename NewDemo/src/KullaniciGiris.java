import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KullaniciGiris extends JFrame {
    // JFrame'den türetiliyor
    public KullaniciGiris() {
        // Ana pencere ayarları
        setTitle("Kullanıcı Giriş Formu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 150);
        setLayout(new GridLayout(4, 1, 10, 10)); // Dört satır ve 1 sütun

        // Kullanıcı adı etiketi ve metin alanı
        JLabel userLabel = new JLabel("Kullanıcı Adı:");
        JTextField userTextField = new JTextField();

        // Giriş butonu
        JButton loginButton = new JButton("Giriş");

        // Mesaj göstermek için bir etiket
        JLabel messageLabel = new JLabel("", JLabel.CENTER);

        // Database bağlantı nesnesi
        DatabaseConnector dbConnector = new DatabaseConnector();

        // Butona tıklandığında çalışacak olay
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String kullanici = userTextField.getText();
                User.getInstance().SetUserName(kullanici);

                if (kullanici.isEmpty()) {
                    messageLabel.setText("Kullanıcı adı boş olamaz!");
                    messageLabel.setForeground(Color.RED);
                } else {
                    boolean isSaved = dbConnector.saveUser();
                    if (isSaved) {
                        messageLabel.setText("Kullanıcı başarıyla kaydedildi!");
                        messageLabel.setForeground(Color.GREEN);

                        SwingUtilities.invokeLater(() -> {
                            new MainMenu(kullanici).setVisible(true);
                        });

                        dispose();
                    } else {
                        messageLabel.setText("Kayıt sırasında hata oluştu!");
                        messageLabel.setForeground(Color.RED);
                    }
                }
            }
        });

        // Bileşenleri pencereye ekle
        add(userLabel);
        add(userTextField);
        add(loginButton);
        add(messageLabel);

        // Pencereyi görünür yap
        setLocationRelativeTo(null); // Ekranın ortasında açılır
        setVisible(true);
    }

    public static void main(String[] args) {
        // Swing uygulamalarında grafiksel arayüz işlemleri Event Dispatch Thread'de yapılmalıdır
        SwingUtilities.invokeLater(() -> {
            new KullaniciGiris(); // KullaniciGiris formunu oluştur
        });
    }
}
