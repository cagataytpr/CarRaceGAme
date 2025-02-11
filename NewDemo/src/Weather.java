import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Weather {
    public enum WeatherType {
        SUNNY,      // Güneşli
        RAINY,      // Yağmurlu
        SNOWY       // Karlı
    }

    private WeatherType currentWeather;
    private Timer weatherTimer;
    private static final double RAIN_SPEED_MULTIPLIER = 0.8;    // Yağmurda hız %20 azalır
    private static final double SNOW_SPEED_MULTIPLIER = 0.7;    // Karda hız %30 azalır
    private static final double SUNNY_SPEED_MULTIPLIER = 1.0;   // Normal hız

    public Weather() {
        currentWeather = WeatherType.SUNNY; // Başlangıçta hava güneşli
        startWeatherCycle();
    }

    private void startWeatherCycle() {
        weatherTimer = new Timer(30000, new ActionListener() { // Change weather every 30 seconds
            @Override
            public void actionPerformed(ActionEvent e) {
                changeWeather();
            }
        });
        weatherTimer.start();
    }

    public void changeWeather() {
        // Rastgele hava durumu değişimi
        WeatherType[] types = WeatherType.values();
        int index = (int) (Math.random() * types.length);
        currentWeather = types[index];
    }

    public WeatherType getCurrentWeather() {
        return currentWeather;
    }

    public double getSpeedMultiplier() {
        switch (currentWeather) {
            case RAINY:
                return RAIN_SPEED_MULTIPLIER;
            case SNOWY:
                return SNOW_SPEED_MULTIPLIER;
            default:
                return SUNNY_SPEED_MULTIPLIER;
        }
    }

    public String getWeatherEffect() {
        switch (currentWeather) {
            case RAINY:
                return "Yağmurlu hava: Yol kaygan, hız %20 azaldı!";
            case SNOWY:
                return "Karlı hava: Yol çok kaygan, hız %30 azaldı!";
            default:
                return "Güneşli hava: Normal sürüş koşulları";
        }
    }

    public void stop() {
        if (weatherTimer != null) {
            weatherTimer.stop();
        }
    }
}
