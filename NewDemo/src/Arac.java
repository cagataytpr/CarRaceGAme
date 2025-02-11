public class Arac {
    private int hiz; // Hız
    private int saglik; // Sağlık
    private int maksimumHiz; // Maksimum hız
    protected boolean yetenekAktif = false;
    protected int yetenekSuresi = 0;
    protected int yetenekCooldown = 0;
    protected static final int YETENEK_COOLDOWN_SURESI = 300; // 5 saniye (60 fps'de)

    public Arac(int maksimumHiz, int saglik) {
        this.hiz = 0; // Başlangıç hızı 0
        this.maksimumHiz = maksimumHiz;
        this.saglik = saglik;
    }

    // Getter ve Setter yöntemleri
    public int getHiz() {
        return hiz;
    }

    public void setHiz(int hiz) {
        if (hiz <= maksimumHiz) {
            this.hiz = hiz;
        } else {
            this.hiz = maksimumHiz; // Maksimum hızı aşmamak için
        }
    }

    public int getSaglik() {
        return saglik;
    }

    public void setSaglik(int saglik) {
        if (saglik >= 0) {
            this.saglik = saglik;
        }
    }

    public int getMaksimumHiz() {
        return maksimumHiz;
    }

    public void setMaksimumHiz(int maksimumHiz) {
        this.maksimumHiz = maksimumHiz;
    }

    // Yetenek durumunu kontrol et
    public boolean isYetenekAktif() {
        return yetenekAktif;
    }

    // Yetenek kullanılabilir mi?
    public boolean yetenekKullanilabilir() {
        return yetenekCooldown <= 0;
    }

    // Yetenek süresini ve cooldown'ı güncelle
    public void yetenekGuncelle() {
        if (yetenekAktif) {
            yetenekSuresi--;
            if (yetenekSuresi <= 0) {
                yetenekAktif = false;
                yetenekCooldown = YETENEK_COOLDOWN_SURESI;
            }
        } else if (yetenekCooldown > 0) {
            yetenekCooldown--;
        }
    }

    // Özel yetenek - alt sınıflar override edecek
    public void ozelYetenekKullan() {
        // Alt sınıflar bu metodu override edecek
    }
}