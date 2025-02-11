public class Main {
    public static void main(String[] args) {
        // Swing uygulamalarında grafiksel arayüz işlemleri Event Dispatch Thread'de yapılmalıdır
        javax.swing.SwingUtilities.invokeLater(() -> {
            // KullaniciGiris formunu oluştur ve görünür yap
            new KullaniciGiris().setVisible(true);
        });
    }
}