package student_player.mcts;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;
import student_player.MyTools;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MCTSState {

    /**
     * This MCTSState.java class aims to represent a particular PentagoBoardState along with a PentagoMove
     * to keep track of the visit count and win score for this state configuration. A MCTSState instance
     * is to be stored within a MCTSNode which themselves construct the tree used in the Monte Carlo Tree Search.
     */

    private double score;
    private int visits;
    private PentagoMove pm;
    private PentagoBoardState pbs;
    private int playerno;

    /**
     * Constructors
     */
    public MCTSState(PentagoBoardState pbs, PentagoMove pm) {
        this.pbs = pbs;
        this.pm = pm;
    }

    public MCTSState(MCTSState s) {
        this.score = s.getScore();
        this.visits = s.getVisits();
        this.pbs = s.getPbs();
        this.pm = s.getPm();
    }

    /**
     * Trim moves from list which lead to same board state.
     */
    public static ArrayList<PentagoMove> trimLegalMoves(PentagoBoardState pbs, ArrayList<PentagoMove> all_moves) {
        ArrayList<PentagoMove> trimmed_moves = new ArrayList<>();
        ArrayList<String> seen_states = new ArrayList<>();
        for(PentagoMove pm : all_moves) {
            PentagoBoardState pbscloned = (PentagoBoardState) pbs.clone();
            pbscloned.processMove(pm);
            // If we have never seen this state, then add it to our temp states list
            // and save the move as a possible move in our trimmed list
            if(!seen_states.contains(pbscloned.toString())) {
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
            PentagoBoardState pbs_cloned = (PentagoBoardState) pbs.clone();
            MCTSState resulting_state = new MCTSState(pbs_cloned, pm);

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
    public void playRandomMove() {
        Random rand = new Random();
        ArrayList<PentagoMove> legalMoves = this.pbs.getAllLegalMoves();
        // We can trim the set of all legal moves
        legalMoves = MCTSState.trimLegalMoves(pbs, legalMoves);
        PentagoMove randomMove = legalMoves.get(rand.nextInt(legalMoves.size()));
        this.pbs.processMove(randomMove);
        switchPlayer();
    }

    /**
     * Switch between players.
     */
    public void switchPlayer() {
        if (this.playerno == PentagoBoardState.WHITE) {
            this.playerno = PentagoBoardState.BLACK; // white --> black
        } else {
            this.playerno = PentagoBoardState.WHITE; // black --> white
        }
    }

    /**
     * Simple method to update the visits counts of nodes during backpropagation.
     */
    public void updateVisits() {
        this.visits++;
    }

    /**
     * Update the score at a node during backpropagation.
     * @param add_score
     */
    public void updateScore(double add_score) {
        if (this.score != Integer.MAX_VALUE) { // avoid overflow
            this.score += add_score;
        }
    }


    /**
     * Getters and setters (some are unused, generated using IntelliJ)
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

    public void setVisits(int v) {
        this.visits = v;
    }

    public void setPm(PentagoMove pm) {
        this.pm = pm;
    }

    public void setPbs(PentagoBoardState pbs) {
        this.pbs = pbs;
    }
}
