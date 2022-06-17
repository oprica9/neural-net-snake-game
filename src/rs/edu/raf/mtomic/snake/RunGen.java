package rs.edu.raf.mtomic.snake;

import rs.edu.raf.mtomic.snake.agent.player.evolution.Controller;

public class RunGen {
    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.initGen();
    }
}
