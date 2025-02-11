import java.util.List;

public class CarDataManager {
    private static final CarDataManager instance = new CarDataManager();

    private CarData car1 = new CarData("Car 1", "./assets/gamecar1.png");
    private CarData car2 = new CarData("Car 2", "./assets/gamecar2.png");
    private CarData car3 = new CarData("Car 3", "./assets/gamecar3.png");
    private CarData car4 = new CarData("Car 4", "./assets/gamecar4.png");

    public CarData getCarData(int carName) {
        switch (carName) {
            case 0: return car1;
            case 1: return car2;
            case 2: return car3;
            case 3: return car4;
        }
        return null;
    }



    private CarDataManager() {}


    public static CarDataManager getInstance() {
        return instance;
    }


    public void display() {
        System.out.println("Singleton Car Data Manager çalışıyor!");
    }
}
