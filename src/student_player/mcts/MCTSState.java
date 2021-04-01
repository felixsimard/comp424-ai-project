package student_player.mcts;

import pentago_twist.PentagoBoard;
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
     * is be stored within a MCTSNode which themselves construct the tree used in the Monte Carlo Tree Search.
     *
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
     * Switch between players.
     */
    public void switchPlayer() {
        if(this.playerno == PentagoBoardState.WHITE) {
            this.playerno = PentagoBoardState.BLACK; // white --> black
        } else {
            this.playerno = PentagoBoardState.WHITE; // black --> white
        }
    }

    /**
     * Simple method to update the visits counts of nodes during backpropagation.
     */
    public void incrVists() {
        this.visits++;
    }

    /**
     * Not put into great use, was trying to figure out how to avoid
     * repeating multiple same states in our search tree...
     * @param states
     * @param s
     * @return
     */
    public boolean stateAlreadySeen(ArrayList<MCTSState> states, MCTSState s) {
        boolean statesAreEqual = false;
        for(MCTSState state : states) {
            statesAreEqual = Arrays.deepEquals(s.getPbs().getBoard(), state.getPbs().getBoard());
            if(statesAreEqual) {
                return true;
            }
        }
        return false;
    }

    /**
     * Expand the states for a node given the list of all possible moves
     * it can compute from this particular state.
     * Then, convert those states to nodes and them to search tree.
     * @return
     */
    public ArrayList<MCTSState> getExpandedNodeStates() {
        ArrayList<MCTSState> expanded_states = new ArrayList<>();
        ArrayList<PentagoMove> moves = this.pbs.getAllLegalMoves();
        for(PentagoMove pm : moves) {

            PentagoBoardState pbs_cloned = (PentagoBoardState) pbs.clone();
            MCTSState new_state = new MCTSState(pbs_cloned, pm);

            new_state.setPlayerno(MyTools.getOpponent(pbs));
            new_state.getPbs().processMove(pm); // play the possible legal move from this state

            // Add state to list of states derived from current node
            expanded_states.add(new_state);

        }

        return expanded_states;
    }

    /**
     * Update the score at a node during backpropagation.
     * @param add_score
     */
    public void updateScore(double add_score) {
        if(this.score != Integer.MAX_VALUE) {
            this.score += add_score;
        }
    }

    /**
     * Select and perform a random move from a current board state.
     * Used during the simulation step.
     */
    public void performRandomMove() {
        Random rand = new Random();
        ArrayList<PentagoMove> legalMoves = this.pbs.getAllLegalMoves();
        PentagoMove randomMove = legalMoves.get(rand.nextInt(legalMoves.size()));
        this.pbs.processMove(randomMove);
        switchPlayer();
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
