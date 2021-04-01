package student_player;

import boardgame.Board;
import boardgame.Move;

import pentago_twist.PentagoMove;
import pentago_twist.PentagoPlayer;
import pentago_twist.PentagoBoardState;
import student_player.mcts.MCTSExecuter;

import java.util.ArrayList;

/** A player file submitted by a student. */
public class MonteCarloPlayer extends PentagoPlayer {

    public static boolean DEBUG_MODE = true;

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public MonteCarloPlayer() {
        super("MonteCarlo 260865674");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(PentagoBoardState boardState) {

        // start time
        long start = System.currentTimeMillis();
        int time_allowed = 1950; // allowed ~2 sec for all moves, except first one.

        if(MyTools.isFirstMove(boardState)) {
            MyTools.print("First move. Use 30s.");
            time_allowed = 29000; // allowed ~30 sec for the first move.
        }

        // Determine if there is a simple move to win the game
        WinNextHeuristic wnh = new WinNextHeuristic();
        PentagoMove win_next_move = wnh.getWinNextMove(boardState);
        if(win_next_move != null) {
            MyTools.print("Found a win-on-next move.");
            return win_next_move;
        }
        MyTools.print("No win-on-next move found. Run MCTS.");

        // Find 'best' move according to MCTS strategy
        MCTSExecuter mcts = new MCTSExecuter(start, time_allowed);
        Move myMove = mcts.getOptimalMove(boardState);

        MyTools.print(String.format("Found move in: %f", (System.currentTimeMillis() - start) / 1000f));
        MyTools.print(myMove.toPrettyString());
        boardState.printBoard();

        // Return your move to be processed by the server.
        return myMove;
    }
}