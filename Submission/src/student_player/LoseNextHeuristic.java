package student_player;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;
import student_player.mcts.MCTSState;

import java.util.ArrayList;

public class LoseNextHeuristic {

    public LoseNextHeuristic() {
        super();
    }

    /**
     * Determine if our opponent could win on its next move (ie: after we play this move).
     * If our opponent could, then block that move!
     */


    public PentagoMove getLoseNextMove(PentagoBoardState pbs) {

        // Fetch all legal moves for the current board state
        ArrayList<PentagoMove> moves = pbs.getAllLegalMoves();

        // Trim the moves which produce the same outcome
        moves = MCTSState.trimLegalMoves(pbs, moves);

        // Opponent
        int opponent = MyTools.getOpponent(pbs);
        ArrayList<PentagoMove> losing_moves = new ArrayList<>();

        // for each potential moves, identify if there is one which would make our opponent win
        // if so, we should play that move to block our opponent from winning.
        for(PentagoMove pm : moves) {
            PentagoBoardState pbs_clone = (PentagoBoardState) pbs.clone();
            pbs_clone.processMove(pm);

            // Check if our opponent could win
            if(pbs_clone.getWinner() == opponent) {
                losing_moves.add(pm);
            }
        }

        // if dangerous moves in sight, try to block opponent.
        if(losing_moves.size() > 0) {
            MyTools.print("Opponent has "+losing_moves.size()+" moves to potentially beat us.");
            return losing_moves.get(0);
        }

        // if no danger in sight, dismiss the heuristic
        return null;

    }


}

