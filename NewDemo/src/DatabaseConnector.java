import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/proje";
    private static final String USER = "root"; // MySQL kullanıcı adı
    private static final String PASSWORD = "1234"; // MySQL şifre

    // Veritabanına bağlantı oluşturma
    public static Connection connect() {
        Connection connection = null;
        try {
            // JDBC sürücüsünü yükle
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Veritabanına bağlantı başarılı!");
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver bulunamadı! " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Veritabanı bağlantısı başarısız! " + e.getMessage());
        }
        return connection;
    }

    /**
     * Kullanıcıyı kontrol ederek kaydet (zaten kullanıcı varsa kaydetmez, mevcut kayıtla devam eder)
     * @return İşlem başarılıysa true, başarısızsa false
     */
    public boolean saveUser() {
        
        User user = User.getInstance();
        String kullanici = user.GetUserName();
        
        String checkUserQuery = "SELECT COUNT(*) FROM proje.kullanicigiris WHERE kullanici = ?";
        String insertUserQuery = "INSERT INTO proje.kullanicigiris (kullanici, skor) VALUES (?, ?)";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkUserQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertUserQuery)) {

            // Mevcut kullanıcıyı kontrol et
            checkStmt.setString(1, kullanici);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                // Kullanıcı zaten kayıtlı
                System.out.println("Kullanıcı zaten kayıtlı: " + kullanici);
                return true;
            }

            // Kullanıcı yoksa, yeni kayıt oluştur
            insertStmt.setString(1, kullanici);
            insertStmt.setInt(2, 0); // Skor varsayılan olarak sıfırdan başlatılır
            insertStmt.executeUpdate();
            System.out.println("Yeni kullanıcı kaydedildi: " + kullanici);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kullanıcı skorunu güncelle (yeni skor daha yüksekse günceller)
     * @return İşlem başarılıysa true, başarısızsa false
     */
    public boolean saveScore() {
        
        User user = User.getInstance();
        String kullanici = user.GetUserName();
        int score = user.GetUserScore();
        
        String getAndUpdateScoreQuery = "SELECT skor FROM proje.kullanicigiris WHERE kullanici = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt1 = conn.prepareStatement(getAndUpdateScoreQuery)) {

            // Mevcut skoru al
            pstmt1.setString(1, kullanici);
            ResultSet rs = pstmt1.executeQuery();
            if (rs.next()) {
                int currentScore = rs.getInt("skor");
                // Yeni skor mevcut skordan düşükse güncellemiyoruz
                if (score > currentScore) {
                    String updateScoreQuery = "UPDATE proje.kullanicigiris SET skor = ? WHERE kullanici = ?";
                    try (PreparedStatement pstmt2 = conn.prepareStatement(updateScoreQuery)) {
                        pstmt2.setInt(1, score);
                        pstmt2.setString(2, kullanici);
                        pstmt2.executeUpdate();
                        System.out.println("Skor güncellendi: " + score);
                        return true;
                    }
                } else {
                    System.out.println("Yeni skor mevcut skordan düşük olduğundan güncellenmedi.");
                    return false;
                }
            } else {
                System.out.println("Kullanıcı bulunamadı: " + kullanici);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kullanıcının mevcut skorunu getir
     * @param kullanici Kullanıcı adı
     * @return Kullanıcının skoru ya da kullanıcı bulunmazsa -1
     */
    public int getUserScore(String kullanici) {
        String getScoreQuery = "SELECT skor FROM proje.kullanicigiris WHERE kullanici = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(getScoreQuery)) {

            pstmt.setString(1, kullanici);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("skor");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Kullanıcı bulunamazsa -1 döndür
    }

    /**
     * Kullanıcıyı sil
     * @param kullanici Kullanıcı adı
     * @return Silme işlemi başarılıysa true, başarısızsa false
     */
    public boolean deleteUser(String kullanici) {
        String deleteUserQuery = "DELETE FROM proje.kullanicigiris WHERE kullanici = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(deleteUserQuery)) {

            pstmt.setString(1, kullanici);
            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Kullanıcı silindi: " + kullanici);
                return true;
            } else {
                System.out.println("Kullanıcı bulunamadı: " + kullanici);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}