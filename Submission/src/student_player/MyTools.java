package student_player;

import pentago_twist.PentagoBoardState;

import java.util.Random;

public class MyTools {

    public static boolean DEBUG_MODE = false;
    public static int FIRST_MOVE_TIME = 12000;
    public static int REGULAR_MOVE_TIME = 1950;

    /**
     * Simple print function with debug parameter. Can toggle debug ON or OFF.
     *
     * @param msg
     */
    public static void print(String msg) {
        if (DEBUG_MODE) {
            System.out.println("- " + msg);
        }
        return;
    }

    /**
     * For easy error displaying. Used during debugging.
     *
     * @param error
     */
    public static void error(String error) {
        System.out.println("ERROR: " + error);
        return;
    }

    /**
     * Get opponent given a PentagoBoardState.
     * Basically get the turn player value and return the other value.
     *
     * @param pbs
     * @return
     */
    public static int getOpponent(PentagoBoardState pbs) {
        int playing = pbs.getTurnPlayer();
        if (playing == PentagoBoardState.WHITE) {
            return PentagoBoardState.BLACK;
        } else {
            return PentagoBoardState.WHITE;
        }
    }

    /**
     * Get our agent's turn number for a given PentagoBoardState.
     *
     * @param pbs
     * @return
     */
    public static int getAgentTurnNum(PentagoBoardState pbs) {
        return pbs.getTurnPlayer();
    }

    /**
     * Simple wrapper for generating a random number between a 'min' and 'max'.
     *
     * @param min
     * @param max
     * @return
     */
    public static int getRandomNumber(int min, int max) {
        Random r = new Random();
        int random_int = r.nextInt(max - min) + min;
        return random_int;
    }

    /**
     * Determine if this is our agent's first move or not.
     *
     * @param pbs
     * @return
     */
    public static boolean isAgentFirstMove(PentagoBoardState pbs) {
        return pbs.getTurnNumber() == 0;
    }

}