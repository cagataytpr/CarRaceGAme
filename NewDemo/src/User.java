

import java.awt.*;

public class User {

    private static final User instance = new User();

    private String _userName;
    private int _userScore;
    private int _userCarIndex;
    private int _userCarName;


    public void SetUserName(String userName) {
        _userName = userName;
    }


    public void SetUserScore(int userScore) {
        _userScore = userScore;
    }


    public String GetUserName() {
        return _userName;
    }


    public int GetUserScore() {
        return _userScore;
    }

    public void SetUserCarIndex(int carIndex) {
        _userCarIndex = carIndex;
    }

    public int GetUserCarIndex() {
        return _userCarIndex;
    }

    private User() {}


    public static User getInstance() {
        return instance;
    }


    public void display() {
        System.out.println("Singleton User çalışıyor!");
    }
}
