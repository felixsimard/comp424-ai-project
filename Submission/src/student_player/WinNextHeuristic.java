package student_player;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;

import java.util.ArrayList;

public class WinNextHeuristic {

    public WinNextHeuristic() {
        super();
    }

    /**
     * Determine if a straightforward winning move exists to be played next (dumb/simple heuristic).
     * @param pbs
     * @return
     */
    public PentagoMove getWinNextMove(PentagoBoardState pbs) {
        // Fetch all legal moves
        ArrayList<PentagoMove> all_moves = pbs.getAllLegalMoves();
        for(PentagoMove pm : all_moves) {
            // Perform possible move on clone
            PentagoBoardState pbs_clone = (PentagoBoardState) pbs.clone();
            pbs_clone.processMove(pm);
            // Check if moves makes you win
            if(pbs_clone.getWinner() == pbs.getTurnPlayer()) {
                // if you can win with this move, use it!
                return pm;
            }
        }

        // return a null move if no win next move found
        return null;

    }

}
