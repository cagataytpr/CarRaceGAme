public class SporArac extends Arac {
    public SporArac(int maksimumHiz, int saglik) {
        super(maksimumHiz, saglik);
    }

    public void turboKullan() {
        int yeniHiz = getHiz() + 50; // Turbo ile hız ekle
        setHiz(yeniHiz); // Güncellenmiş hızı ayarla
    }
}