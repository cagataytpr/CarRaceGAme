import java.awt.Color;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class TimeOfDay {
    private DayPhase currentPhase;
    private Timer dayNightTimer;
    private float timeProgress;
    private static final float TIME_STEP = 0.01f; // Increased from 0.001f to make cycle faster

    public enum DayPhase {
        MORNING("Sabah", new Color(255, 200, 150), 0.8f),
        NOON("Öğle", new Color(255, 255, 255), 1.0f),
        EVENING("Akşam", new Color(255, 150, 100), 0.7f),
        NIGHT("Gece", new Color(50, 50, 100), 0.4f);

        private String name;
        private Color lightColor;
        private float brightness;

        DayPhase(String name, Color lightColor, float brightness) {
            this.name = name;
            this.lightColor = lightColor;
            this.brightness = brightness;
        }

        public String getName() { return name; }
        public Color getLightColor() { return lightColor; }
        public float getBrightness() { return brightness; }
    }

    public TimeOfDay() {
        currentPhase = DayPhase.NOON;
        timeProgress = 0.0f;
        startDayNightCycle();
    }

    private void startDayNightCycle() {
        dayNightTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeProgress += TIME_STEP;
                if (timeProgress >= 1.0f) {
                    timeProgress = 0.0f;
                    switchToNextPhase();
                }
            }
        });
        dayNightTimer.start();
    }

    private void switchToNextPhase() {
        DayPhase[] phases = DayPhase.values();
        int nextIndex = (currentPhase.ordinal() + 1) % phases.length;
        currentPhase = phases[nextIndex];
    }

    public Color modifyColor(Color baseColor) {
        float brightness = currentPhase.getBrightness();
        float[] hsb = Color.RGBtoHSB(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), null);
        hsb[2] *= brightness;
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

    public DayPhase getCurrentPhase() { return currentPhase; }
    public void stop() { dayNightTimer.stop(); }
}
