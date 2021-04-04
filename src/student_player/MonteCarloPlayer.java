package student_player;

import boardgame.Move;

import pentago_twist.PentagoMove;
import pentago_twist.PentagoPlayer;
import pentago_twist.PentagoBoardState;
import student_player.mcts.MCTSExecuter;

/**
 * A player file submitted by a student.
 */
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

        //------------------------
        // Initialize agent
        MCTSExecuter agent = new MCTSExecuter();
        //------------------------

        // Timing setup
        long start_time = System.currentTimeMillis();
        int time_allowed = 1925; // allowed ~2 sec for all moves, except first one

        // First Move
        if (MyTools.isAgentFirstMove(boardState)) {
            MyTools.print("Agent's first move. Use 30s.");
            time_allowed = 15000; // allowed ~30 sec for the first move
        }

        //------------------------
        // Determine if there is a simple move to win the game
        WinNextHeuristic wnh = new WinNextHeuristic();
        PentagoMove win_next_move = wnh.getWinNextMove(boardState);
        if (win_next_move != null) {
            MyTools.print("Found a win-on-next move.");
            return win_next_move;
        }
        MyTools.print("No win-on-next move found. Run MCTS.");
        //------------------------

        //------------------------
        // MCTS - Find optimal move
        agent.setStartTime(start_time);
        agent.setTimeAllowed(time_allowed);
        Move myMove = agent.getOptimalMove(boardState);
        MyTools.print(String.format("Found move in: %f", (System.currentTimeMillis() - start_time) / 1000f));
        //------------------------

        // Return your move to be processed by the server.
        return myMove;
    }
}