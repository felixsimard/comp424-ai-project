package student_player;


import pentago_twist.PentagoBoardState;

import java.util.Random;

public class MyTools {

    public static boolean DEBUG_MODE = true;
    public static int FIRST_MOVE_TIME = 12000;
    public static int REGULAR_MOVE_TIME = 1950;

    public static double getSomething() {
        return Math.random();
    }

    public static void print(String msg) {
        if (DEBUG_MODE) {
            System.out.println("- " + msg);
        }
        return;
    }

    public static void error(String error) {
        System.out.println("ERROR: " + error);
        return;
    }

    public static int getOpponent(PentagoBoardState pbs) {
        int playing = pbs.getTurnPlayer();
        if (playing == PentagoBoardState.WHITE) {
            return PentagoBoardState.BLACK;
        } else {
            return PentagoBoardState.WHITE;
        }
    }

    public static int getAgent(PentagoBoardState pbs) {
        return pbs.getTurnPlayer();
    }

    public static int getRandomNumber(int min, int max) {
        Random r = new Random();
        int random_int = r.nextInt(max - min) + min;
        return random_int;
    }

    public static boolean isAgentFirstMove(PentagoBoardState pbs) {
        return pbs.getTurnNumber() == 0;
    }

}