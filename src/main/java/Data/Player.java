package Data;

import java.net.Socket;

public class Player {
    String name;
    public Socket socket;
    public int score;

    public Player(String name, Socket socket, int score){
        this.name = name;
        this.socket = socket;
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public void setScore(int score) { //adding the score to the player
        this.score += score;
    }

}
