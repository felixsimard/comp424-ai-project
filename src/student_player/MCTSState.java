package student_player;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;

import java.util.ArrayList;

public class MCTSState {

    /**
     * This MCTSState.java class aims to represent a particular PentagoBoardState along with a PentagoMove
     * to keep track of the visit count and win score for a particular state configuration. A MCTSState instance
     * is to be stored within a MCTSNode which themselves construct the tree used in the Monte Carlo Tree Search.
     */

    private double score;
    private int visits;
    private PentagoMove pm;
    private PentagoBoardState pbs;
    private int playerno;

    /**
     * Constructors, based on the various forms we could construct a MCTSState
     */
    public MCTSState(PentagoBoardState pbs, PentagoMove pm) {
        this.pbs = pbs;
        this.pm = pm;
    }

    public MCTSState(MCTSState s) {
        // If we construct a MCTSState from a MCTSState, just copy over the s attributes
        // to the newly constructed MCTSState
        this.score = s.getScore();
        this.visits = s.getVisits();
        this.pbs = s.getPbs();
        this.pm = s.getPm();
    }

    /**
     * Trim moves from list which lead to same board state.
     * This effectively reduces the branching factor at each step.
     */
    public static ArrayList<PentagoMove> trimLegalMoves(PentagoBoardState pbs, ArrayList<PentagoMove> all_moves) {
        // Keep track of trimmed moves and seen states
        ArrayList<PentagoMove> trimmed_moves = new ArrayList<>();
        ArrayList<String> seen_states = new ArrayList<>();
        // For each possible move, check if playing that move would lead to a board configuration which we can already
        // reach from another move. If so, discard that move.
        for (PentagoMove pm : all_moves) {
            PentagoBoardState pbscloned = (PentagoBoardState) pbs.clone();
            pbscloned.processMove(pm);
            // If we have never seen this state, then add it to our temp states list
            // and save the move as a possible move in our trimmed list.
            // Use the .toString() method of a PentagoBoardState to compare board configurations.
            if (!seen_states.contains(pbscloned.toString())) {
                seen_states.add(pbscloned.toString());
                trimmed_moves.add(pm);
            }
        }
        return trimmed_moves;
    }

    /**
     * Expand the states for a node given the list of all possible moves
     * it can compute from this particular state.
     * Then, convert those states to nodes and them to search tree.
     *
     * @return
     */
    public ArrayList<MCTSState> getExpandedNodeStates() {
        // To hold all the expanded states to be returned
        ArrayList<MCTSState> expanded_states = new ArrayList<>();
        // Get all legal moves (some moves lead to the same board state)
        ArrayList<PentagoMove> all_moves = pbs.getAllLegalMoves();
        // Trim moves to reduce branching factor
        ArrayList<PentagoMove> trim_moves = MCTSState.trimLegalMoves(pbs, all_moves);
        for (PentagoMove pm : trim_moves) { // for each possible move, clone pbs, process move, add state to list
            // Clone
            PentagoBoardState pbscloned = (PentagoBoardState) pbs.clone();
            MCTSState resulting_state = new MCTSState(pbscloned, pm);
            //Play the possible legal move from this state
            resulting_state.setPlayerno(MyTools.getOpponent(pbs));
            resulting_state.getPbs().processMove(pm);
            // Add state to list of states derived from current node
            expanded_states.add(resulting_state);
        }
        return expanded_states;
    }

    /**
     * Select and play a random move from a current board state.
     * Used during the rollout step.
     */
    public void randomMove() {
        // Get and trim the set of all legal moves
        ArrayList<PentagoMove> legalMoves = getPbs().getAllLegalMoves();
        legalMoves = MCTSState.trimLegalMoves(pbs, legalMoves);
        // Get our random move, choosing an int from 0 to legalMoves.size()
        int randint = MyTools.getRandomNumber(0, legalMoves.size());
        PentagoMove randomMove = legalMoves.get(randint);
        getPbs().processMove(randomMove);
        // Switch player for next random move to be played (recall this is used in the rollout phase)
        switchPlayer();
    }

    /**
     * Simple method to update the visits counts of nodes during backpropagation.
     */
    public void updateVisits() {
        this.visits++;
    }

    /**
     * Update the score at a node during backpropagation.
     *
     * @param add_score
     */
    public void updateScore(double add_score) {
        if (this.score != Integer.MAX_VALUE) { // avoid overflow
            this.score += add_score;
        }
    }

    /**
     * Switch between players.
     * Opponent -> Agent
     * Agent    -> Opponent
     */
    public void switchPlayer() {
        int pno = getPlayerno();
        if (pno == PentagoBoardState.WHITE) {
            setPlayerno(PentagoBoardState.BLACK); // white --> black
        } else {
            setPlayerno(PentagoBoardState.WHITE); // white --> black
        }
    }

    /**
     * Getters and setters (generated using IntelliJ).
     */
    public double getScore() {
        return this.score;
    }

    public int getVisits() {
        return visits;
    }

    public PentagoMove getPm() {
        return pm;
    }

    public PentagoBoardState getPbs() {
        return pbs;
    }

    public int getPlayerno() {
        return playerno;
    }

    public void setPlayerno(int no) {
        this.playerno = no;
    }

    public void setScore(double s) {
        this.score = s;
    }

    public void setPbs(PentagoBoardState pbs) {
        this.pbs = pbs;
    }
}
