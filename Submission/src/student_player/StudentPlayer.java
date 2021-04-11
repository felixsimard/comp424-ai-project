package student_player;

import boardgame.Move;

import pentago_twist.PentagoMove;
import pentago_twist.PentagoPlayer;
import pentago_twist.PentagoBoardState;

/**
 * A player file submitted by a student.
 */
public class StudentPlayer extends PentagoPlayer {

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

        // Timing setup
        long start_time = System.currentTimeMillis();
        int time_allowed = MyTools.REGULAR_MOVE_TIME;

        // First Move (allowed ~30s)
        if (MyTools.isAgentFirstMove(boardState)) {
            MyTools.print("Agent's first move.");
            time_allowed = MyTools.FIRST_MOVE_TIME;
        }

        // Determine if there is a simple move to win the game.
        WinNextHeuristic wnh = new WinNextHeuristic();
        PentagoMove win_next_move = wnh.getWinNextMove(boardState);
        if (win_next_move != null) {
            MyTools.print("Found a win-on-next move.");
            return win_next_move;
        }

        // Determine if our opponent could win on the next move. If could, block opponent.
        LoseNextHeuristic lnh = new LoseNextHeuristic();
        PentagoMove lose_next_move = lnh.getLoseNextMove(boardState);
        if (lose_next_move != null) {
            MyTools.print("Trying to avoid a loss by blocking opponent.");
            return lose_next_move;
        }

        // Find optimal move using Monte Carlo Tree Search (MCTS)
        MyTools.print("Run MCTS agent.");
        MCTSExecuter agent = new MCTSExecuter();
        agent.setStartTime(start_time);
        agent.setTimeAllowed(time_allowed);

        // Get optimal move using MCTS
        Move myMove = agent.getOptimalMove(boardState);

        MyTools.print(String.format("Found move in %f", (System.currentTimeMillis() - start_time) / 1000f));

        return myMove;

    }
}