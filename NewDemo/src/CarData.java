public class CarData {
    private String _carName;
    private String _carImagePath;

    public String CarName(){
        return _carName;
    }


    public String CarImagePath(){
        return _carImagePath;
    }

    public CarData(String carName, String carImagePath) {
        _carName = carName;
        _carImagePath = carImagePath;
    }
}