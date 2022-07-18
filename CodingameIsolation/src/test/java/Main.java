
import com.codingame.gameengine.runner.MultiplayerGameRunner;

public class Main {
    public static void main(String[] args) {
        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();

        gameRunner.addAgent(Agent1.class, "wala", "https://static.codingame.com/servlet/fileservlet?id=45161669909418&format=viewer_avatar");
        gameRunner.addAgent(Agent2.class);

        gameRunner.start();
    }
}
