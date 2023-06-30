package Data;

import java.net.Socket;

public class Player {
    String name;
    private Socket socket;
    private int score;

    public Player(String name, Socket socket, int score) {
        this.name = name;
        this.socket = socket;
        this.score = score;
    }

    public void addScore(int scoreAdd) {
        this.score += scoreAdd;
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

    public Socket getSocket() {
        if (this.socket != null) {
            if (!this.socket.isClosed()) {
                return socket;
            } else return null;

        } else {
            return null;
        }
    }

}
