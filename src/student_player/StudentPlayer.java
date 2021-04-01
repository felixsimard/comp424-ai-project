package student_player;

import boardgame.Move;

import pentago_twist.PentagoMove;
import pentago_twist.PentagoPlayer;
import pentago_twist.PentagoBoardState;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {

    public static boolean DEBUG_MODE = true;

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260865674");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(PentagoBoardState boardState) {

        // start time
        long start = System.currentTimeMillis();

        // Determine if there is a simple move to win the game
        WinNextHeuristic wnh = new WinNextHeuristic();
        PentagoMove win_next_move = wnh.getWinNextMove(boardState);
        if(win_next_move != null) {
            MyTools.print("FOUND a winning move.");
            return win_next_move;
        }

        // Find 'best' move
        Move myMove = null;

        // end time
        long end = System.currentTimeMillis();

        // Return your move to be processed by the server.
        return myMove;
    }
}