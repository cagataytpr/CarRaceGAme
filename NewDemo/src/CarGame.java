import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.Timer;

public class CarGame extends JPanel implements KeyListener,ActionListener
{
    private Arac arac;
    private String carName;
    private String carImage;
    private int xpos=300;
    private int ypos=700;
    private ImageIcon car;
    private Timer timer;
    private JFrame frame;
    Random random=new Random();
    private TimeOfDay timeOfDay;
    private Weather weather;
    private int[] weatherParticleX = new int[20];
    private int[] weatherParticleY = new int[20];

    private int num1=400,num2=0,num3=0;
    private int tree1ypos=400,tree2ypos=-200,tree3ypos=-500,tree4ypos=100,tree5ypos=-300,tree6ypos=500;
    private int roadmove=0;
    private int carxpos[]={100,200,300,400,500};
    private int carypos[]= {-240,-480,-720,-960,-1200};
    private int cxpos1=0,cxpos2=2,cxpos3=4;
    private int cypos1=random.nextInt(5),cypos2=random.nextInt(5),cypos3=random.nextInt(5);
    int y1pos=carypos[cypos1],y2pos=carypos[cypos2],y3pos=carypos[cypos3];
    private ImageIcon car1,car2,car3;
    private int score=0,delay=100,speed=90;
    private ImageIcon tree1,tree2,tree3;
    private boolean rightrotate=false,gameover=false,paint=false;
    private boolean healthDecreased = false; // Sağlığın azalma durumunu kontrol etmek için
    private int invulnerableTime = 0; // Çarpışma sonrası koruma süresi
    private static final int INVULNERABLE_PERIOD = 100; // Koruma süresi sabiti
    private String kullaniciAdi; // Kullanıcı adı
    private DatabaseConnector dbConnector; // Veritabanı bağlantısı için nesne
    private int roadSpeed = 0;  // Yol hızı değişkeni
    public CarGame(String title , String kullaniciAdi )
    {
        this.carName = title;
        this.kullaniciAdi = kullaniciAdi;
        
        // Frame oluştur
        frame = new JFrame(title);
        frame.setSize(700,700);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Panel ayarları
        setDoubleBuffered(true);
        setPreferredSize(new Dimension(700, 700));
        addKeyListener(this);
        setFocusable(true);
        setLayout(null);

        // Zaman sistemini başlat
        timeOfDay = new TimeOfDay();
        
        // Veritabanı bağlantısı
        this.dbConnector = new DatabaseConnector();
        
        // Hava durumu sistemi başlatılıyor
        weather = new Weather();
        
        // Hava durumu parçacıkları için başlangıç pozisyonları
        for(int i = 0; i < 20; i++) {
            weatherParticleX[i] = random.nextInt(700);
            weatherParticleY[i] = random.nextInt(800);
        }
        
        // Araç tipine göre nesne oluştur
        switch (title.toLowerCase()) {
            case "offroad":
                arac = new OffRoad();
                break;
            case "sedan":
                arac = new Sedan();
                break;
            case "supersport":
                arac = new SuperSport();
                break;
            case "pistarac":
                arac = new PistArac();
                break;
            default:
                arac = new Sedan(); // Varsayılan olarak Sedan
                break;
        }
        
        // Frame'e panel'i ekle
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
        
        // Timer'ı başlat
        timer = new Timer(delay, this);
        timer.start();
    }
    public void paintComponent(Graphics g) {
        Image offscreen = createImage(getWidth(), getHeight());
        Graphics2D g2d = (Graphics2D) offscreen.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Arka plan - gece/gündüz efektiyle
        Color backgroundColor = timeOfDay.modifyColor(new Color(34, 139, 34));
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, 700, 700);
        
        // Ana yol - gece/gündüz efektiyle
        g2d.setColor(timeOfDay.modifyColor(Color.BLACK));
        g2d.fillRect(100, 0, 500, 700);
        
        // Yol kenarları - gece/gündüz efektiyle
        g2d.setColor(timeOfDay.modifyColor(Color.GRAY));
        g2d.fillRect(90, 0, 10, 700);  // Sol kenar
        g2d.fillRect(600, 0, 10, 700); // Sağ kenar
        
        // Hava durumu efektleri
        if (weather != null) {
            switch(weather.getCurrentWeather()) {
                case RAINY:
                    g2d.setColor(new Color(155, 155, 155, 50));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(new Color(100, 149, 237, 180));
                    for(int i = 0; i < weatherParticleY.length; i++) {
                        g2d.fillRect(weatherParticleX[i], weatherParticleY[i], 2, 10);
                        weatherParticleY[i] += 15;
                        if(weatherParticleY[i] > getHeight()) {
                            weatherParticleY[i] = 0;
                            weatherParticleX[i] = random.nextInt(getWidth());
                        }
                    }
                    break;
                    
                case SNOWY:
                    g2d.setColor(new Color(200, 200, 200, 50));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(new Color(255, 255, 255, 180));
                    for(int i = 0; i < weatherParticleY.length; i++) {
                        g2d.fillOval(weatherParticleX[i], weatherParticleY[i], 5, 5);
                        weatherParticleY[i] += 5;
                        weatherParticleX[i] += random.nextInt(3) - 1;
                        if(weatherParticleY[i] > getHeight()) {
                            weatherParticleY[i] = 0;
                            weatherParticleX[i] = random.nextInt(getWidth());
                        }
                    }
                    break;
                    
                case SUNNY:
                    g2d.setColor(new Color(255, 255, 0, 20));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    break;
            }
        }
        
        // Göstergeler - En üstte çizilmeli
        // Skor göstergesi
        g2d.setColor(Color.gray);
        g2d.fillRect(130,10,180,50);
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(135,15, 170, 40);

        // Hız göstergesi
        g2d.setColor(Color.gray);
        g2d.fillRect(320,10,150,50);
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(325,15, 140, 40);

        // Sağlık göstergesi
        g2d.setColor(Color.gray);
        g2d.fillRect(480,10,100,50);
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(485,15, 90, 40);

        // Zaman göstergesi
        g2d.setColor(Color.gray);
        g2d.fillRect(590,10,100,50);
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(595,15, 90, 40);

        // Hava durumu göstergesi
        g2d.setColor(Color.gray);
        g2d.fillRect(10,10,110,60);  // Yüksekliği artırıldı
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(15,15, 100, 50);  // Yüksekliği artırıldı

        // Gösterge yazıları
        g2d.setColor(Color.white);
        g2d.setFont(new Font("Arial",Font.BOLD,20));
        
        // Skor
        g2d.drawString("Score: " + score, 140, 42);
        
        // Hız
        g2d.drawString(Math.min(speed, arac.getMaksimumHiz()) + " Km/h", 330, 42);
        
        // Sağlık
        g2d.drawString("HP: " + arac.getSaglik(), 490, 42);
        
        // Zaman durumu
        g2d.setFont(new Font("Arial",Font.BOLD,15));
        g2d.drawString(timeOfDay.getCurrentPhase().getName(), 600, 42);
        
        // Hava durumu - İki satır halinde
        g2d.setFont(new Font("Arial",Font.BOLD,14));
        String[] weatherWords = weather.getWeatherEffect().split(" ");
        if (weatherWords.length > 1) {
            g2d.drawString(weatherWords[0], 20, 35);
            g2d.drawString(weatherWords[1], 20, 55);
        } else {
            g2d.drawString(weather.getWeatherEffect(), 20, 45);
        }

        if (!gameover) {
            score++;
            if (speed < arac.getMaksimumHiz()) {
                speed++;
            }
        }

        // Yol çizimi
        if(roadmove==0)
        {
            for(int i=0; i<=700; i+=100)
            {
                g2d.setColor(Color.WHITE);
                g2d.fillRect(350, i,10, 70);

            }
            roadmove=1;
        }
        else if(roadmove==1)
        {
            for(int i=50; i<=700; i+=100)
            {
                g2d.setColor(Color.WHITE);
                g2d.fillRect(350, i,10, 70);
            }
            roadmove=0;
        }
        
        // Ağaç çizimi
        tree1=new ImageIcon("./assets/tree1.png");
        tree1.paintIcon(this, g2d, 0, tree1ypos);
        num1=random.nextInt(500);
        tree1ypos+=50;

        tree2=new ImageIcon("./assets/tree2.png");
        tree2.paintIcon(this, g2d, 0,tree2ypos );
        tree2ypos+=50;

        tree3=new ImageIcon("./assets/tree3.png");
        tree3.paintIcon(this,g2d,0,tree3ypos);
        tree3ypos+=50;
        tree1.paintIcon(this,g2d,600,tree4ypos);
        tree4ypos+=50;
        tree3.paintIcon(this, g2d,600,tree5ypos);
        tree5ypos+=50;
        tree2.paintIcon(this, g2d,600,tree6ypos);
        tree6ypos+=50;

        // Ağaçların ekran dışına çıkması durumunda yeniden konumlandırma
        if(tree1ypos>700)
        {
            num1=random.nextInt(500);
            tree1ypos=-num1;
        }
        if(tree2ypos>700)
        {
            num1=random.nextInt(500);
            tree2ypos=-num1;
        }
        if(tree3ypos>700)
        {
            num1=random.nextInt(500);
            tree3ypos=-num1;
        }
        if(tree4ypos>700)
        {
            num1=random.nextInt(500);
            tree4ypos=-num1;
        }
        if(tree5ypos>700)
        {
            num1=random.nextInt(500);
            tree5ypos=-num1;
        }
        if(tree6ypos>700)
        {
            num1=random.nextInt(500);
            tree6ypos=-num1;
        }

        // Araç çizimi
        car=new ImageIcon(CarDataManager.getInstance().getCarData(User.getInstance().GetUserCarIndex()).CarImagePath());
        car.paintIcon(this,g2d,xpos,ypos);
        ypos-=40;
        if(ypos<500)
        {
            ypos=500;
        }

        // Karşı araç çizimi
        car1=new ImageIcon("./assets/gamecar2.png");
        car2=new ImageIcon("./assets/gamecar3.png");
        car3=new ImageIcon("./assets/gamecar4.png");

        car1.paintIcon(this, g2d, carxpos[cxpos1], y1pos);
        car2.paintIcon(this, g2d, carxpos[cxpos2], y2pos);
        car3.paintIcon(this, g2d, carxpos[cxpos3], y3pos);
        y1pos+=50;
        y2pos+=50;
        y3pos+=50;
        if(y1pos>700)
        {
//			cxpos1++;
//			if(cxpos1>4)
//			{
//				cxpos1=0;
//			}
            cxpos1=random.nextInt(5);
            cypos1=random.nextInt(5);
            y1pos=carypos[cypos1];


        }
        if(y2pos>700)
        {
            cxpos2++;
            if(cxpos2>4)
            {
                cxpos2=0;
            }

            cxpos2=random.nextInt(5);
            cypos2=random.nextInt(5);
            y2pos=carypos[cypos2];

        }
        if(y3pos>700)
        {
            cxpos3++;
            if(cxpos3>4)
            {
                cxpos3=0;
            }
            cxpos3=random.nextInt(5);
            cypos3=random.nextInt(5);
            y3pos=carypos[cypos3];
        }

        if(cxpos1==cxpos2 && cypos1>-100 && cypos2>-100)
        {


            cxpos1-=1;
            if(cxpos1<0)
            {
                cxpos1+=2;
            }
        }
        if(cxpos1==cxpos3&& cypos1>-100 && cypos3>-100)
        {
            cxpos3-=1;
            if(cxpos3<0)
            {
                cxpos3+=2;
            }
        }
        if(cxpos2==cxpos3&& cypos3>-100 && cypos2>-100)
        {
            cxpos2-=1;
            if(cxpos2<0)
            {
                cxpos2+=2;
            }
        }
        if(cxpos1<2 && cxpos2<2 && cxpos3<2)
        {
            if(cxpos1==0 && cxpos2==0 && cxpos3==1)
            {
                cxpos3++;
                cxpos2++;
            }
            else if(cxpos1==0 && cxpos2==1 && cxpos3==0)
            {
                cxpos3++;
                cxpos2++;
            }
            else if(cxpos1==1 && cxpos2==0 && cxpos3==0)
            {
                cxpos1++;
                cxpos2++;
            }
        }

        // Hız kontrolü ve gecikme ayarı
        if(score % 50 == 0) {
            delay -= 10;
            if(delay < 60) {
                delay = 60;
            }
        }

        // Gecikme uygula
        try {
            TimeUnit.MILLISECONDS.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Çarpışma kontrolü
        boolean collision = false;

        // Çarpışma kontrolü için araç boyutları
        int carWidth = 40;  // Araç genişliği - küçültüldü
        int carHeight = 80; // Araç yüksekliği - küçültüldü
        int hitboxOffset = 10; // Kenarlardan içeri kaydırma miktarı

        // Oyuncu aracının çarpışma kutusu - kenarlardan içeri kaydırılmış
        int playerLeft = xpos + hitboxOffset;
        int playerRight = xpos + carWidth - hitboxOffset;
        int playerTop = ypos + hitboxOffset;
        int playerBottom = ypos + carHeight - hitboxOffset;

        // Diğer araçlar için çarpışma kontrolü
        for (int i = 0; i < 3; i++) {
            int otherX = 0;
            int otherY = 0;

            // Hangi aracı kontrol ediyoruz?
            switch (i) {
                case 0:
                    otherX = carxpos[cxpos1];
                    otherY = y1pos;
                    break;
                case 1:
                    otherX = carxpos[cxpos2];
                    otherY = y2pos;
                    break;
                case 2:
                    otherX = carxpos[cxpos3];
                    otherY = y3pos;
                    break;
            }

            // Diğer aracın çarpışma kutusu - kenarlardan içeri kaydırılmış
            int otherLeft = otherX + hitboxOffset;
            int otherRight = otherX + carWidth - hitboxOffset;
            int otherTop = otherY + hitboxOffset;
            int otherBottom = otherY + carHeight - hitboxOffset;

            // Çarpışma kontrolü - iki dikdörtgenin kesişimi
            if (playerLeft < otherRight &&
                    playerRight > otherLeft &&
                    playerTop < otherBottom &&
                    playerBottom > otherTop) {

                collision = true;
                arac.setSaglik(arac.getSaglik() - 1);

                // Çarpışan aracı yukarı taşı
                switch (i) {
                    case 0:
                        y1pos = -240;
                        cypos1 = random.nextInt(5);
                        break;
                    case 1:
                        y2pos = -480;
                        cypos2 = random.nextInt(5);
                        break;
                    case 2:
                        y3pos = -720;
                        cypos3 = random.nextInt(5);
                        break;
                }

                System.out.println("Çarpışma! Kalan can: " + arac.getSaglik());
                if (arac.getSaglik() <= 0) {
                    gameover = true;
                    System.out.println("Oyun bitti!");
                }
                break; // İlk çarpışmadan sonra döngüden çık
            }
        }

        // Skor güncelleme
        User.getInstance().SetUserScore(score);

        if (gameover) {
            g2d.setColor(Color.gray);
            g2d.fillRect(120, 210, 460, 240); // "Game Over" kutusu
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(130, 220, 440, 220);
            g2d.setFont(new Font("Serif", Font.BOLD, 50));
            g2d.setColor(Color.yellow);
            g2d.drawString("Game Over!", 210, 270); // "Game Over!"
            g2d.setColor(Color.white);
            g2d.setFont(new Font("Arial", Font.BOLD, 30));
            g2d.drawString("Press Enter to Restart", 190, 340); // Restart mesajı
            g2d.drawString("Your Score: " + score, 230, 390); // Skor yazdırma


            System.out.println(User.getInstance().GetUserScore());

            /** Bu aşamada NEW RECORD'u kontrol ediyoruz */
            if (!paint) {
                // Veritabanından oyuncunun eski skorunu çek
                int currentHighScore = dbConnector.getUserScore(User.getInstance().GetUserName());

                // Mevcut skoru kullanarak yeni skoru veritabanına kaydetmeye çalış (eğer skor daha yüksekse).
                User.getInstance().SetUserScore(score);
                boolean isSaved = dbConnector.saveScore();
                String newRecordMessage = "";

                // Eğer skoru geçmişsek, "New Record" mesajını ekler.
                if (score > currentHighScore) {
                    newRecordMessage = "NEW RECORD!";
                }

                // Rekor mesajını çizdir
                g2d.setFont(new Font("Arial", Font.BOLD, 25));
                g2d.setColor(Color.red); // Rekor yazısı kırmızı renkte
                g2d.drawString(newRecordMessage, 230, 430); // Rekoru mesajını konumlandır

                // "paint" güncellemesini bir kez işaretleriz, bununla tekrar güncellenmesini engelleriz.
                paint = true;
            }
        } else {
            repaint(); // Eğer oyun bitmemişse ekran yeniden çizilir
        }

        // Buffer'ı ekrana çiz
        g.drawImage(offscreen, 0, 0, this);
        
        // Belleği temizle
        g2d.dispose();
    }

    public static void main(String args[])
    {
        CarGame c = new CarGame("Car Game", "defaultUser");

    }
    @Override
    public void keyPressed(KeyEvent e) {
        if (gameover) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                // Oyunu sıfırla
                score = 0;
                speed = 90;
                roadSpeed = 0;
                y1pos = -240;
                y2pos = -480;
                y3pos = -720;
                xpos = 300;
                ypos = 400;
                gameover = false;

                // Mevcut pencereyi kapat
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window != null) {
                    window.dispose();
                }

                // Direkt araç seçim ekranını aç
                SwingUtilities.invokeLater(() -> {
                    AracSecimEkrani aracSecim = new AracSecimEkrani(kullaniciAdi);
                    aracSecim.setVisible(true);
                });
            }
            return;
        }
        if(e.getKeyCode()==KeyEvent.VK_LEFT && !gameover)
        {
            xpos-=100;
            if(xpos<100)
            {
                xpos=100;
            }


        }
        if(e.getKeyCode()==KeyEvent.VK_RIGHT&&!gameover)
        {
            xpos+=100;
            if(xpos>500)
            {
                xpos=500;
            }
        }
        if(e.getKeyCode()==KeyEvent.VK_SPACE && gameover)
        {
            gameover = false;
            cypos1 = random.nextInt(5);
            cypos2 = random.nextInt(5);
            cypos3 = random.nextInt(5);
            speed = 90;
            score = 0;
            delay = 100;
            xpos = 300;
            ypos = 700;
            invulnerableTime = 0;
            healthDecreased = false;

            // Araç tipine göre yeniden oluştur
            switch (carName.toLowerCase()) {
                case "offroad":
                    arac = new OffRoad();
                    break;
                case "sedan":
                    arac = new Sedan();
                    break;
                case "supersport":
                    arac = new SuperSport();
                    break;
                case "pistarac":
                    arac = new PistArac();
                    break;
                default:
                    arac = new Sedan();
                    break;
            }
        }


    }
    @Override
    public void keyReleased(KeyEvent arg0) {


    }
    @Override
    public void keyTyped(KeyEvent e) {

        if(e.getKeyChar()=='a'&&!gameover)
        {
            xpos-=100;

        }
        if(e.getKeyChar()=='s'&&!gameover)
        {
            xpos+=100;
        }

        repaint();

    }
    @Override
    public void actionPerformed(ActionEvent arg0)
    {



    }


}