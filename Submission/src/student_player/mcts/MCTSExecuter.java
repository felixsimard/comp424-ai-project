package student_player.mcts;

import boardgame.Board;
import boardgame.Move;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;
import student_player.MyTools;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Reference used during the construction of the MCTS classes:
 * https://www.baeldung.com/java-monte-carlo-tree-search
 * + class lecture notes
 */

public class MCTSExecuter {

    // Constants
    private static int INCR_SCORE = 100;
    private static final double SCALING_CONSTANT = Math.sqrt(1); // UCT scaling constant
    private int OPPONENT;
    private int AGENT;
    private long start_time;
    private int time_allowed;
    private int games_played = 0;

    // Serialization
    private static String TREE_FILE_NAME = "./data/tree.ser";
    public Map<Integer, ArrayList<SerializableNode>> tree;

    /**
     * Constructor for our MCTS agent
     */
    public MCTSExecuter() {
        super();
        MyTools.print("Initializing MCTSExecuter agent.");

    }

    /**
     * Main MCTS method to find the optimal move. Must keep track of time allowed.
     *
     * @param pbs
     * @return
     */
    public Move getOptimalMove(PentagoBoardState pbs) {

        /*
        // Load serialized tree
        MyTools.print("Size of tree: " + MCTSExecuter.loadTree().size());
        setTree(MCTSExecuter.loadTree());
        MCTSExecuter.showTree(tree);
        */

        // Define end time to respect time allocated
        long endtime = start_time + time_allowed;

        // Init root node of our MCTS, with no children
        MCTSNode root = new MCTSNode(pbs, null);
        OPPONENT = MyTools.getOpponent(pbs);
        AGENT = MyTools.getAgent(pbs);
        root.getNodeState().setPlayerno(OPPONENT);

        while (System.currentTimeMillis() < endtime) { // given time allowed at each move

            int playout_result;

            // SELECTION
            MCTSNode selected = select(root);

            // EXPANSION
            int expanded_winner = selected.getNodeState().getPbs().getWinner();
            if (expanded_winner == Board.NOBODY) {
                // If this is not a leaf node (ie: if there is no winner yet for this game), expand search tree.
                // This will expand the tree states from the promising node
                expand(selected);
            }

            // ROLLOUT
            MCTSNode simulate_node = selected;
            ArrayList<MCTSNode> promising_children = selected.getNodeChildren();
            if (promising_children.size() > 0) { // maybe have children by expansion at previous step
                // simplest heuristic, get a random child of the promising expanded node to rollout
                simulate_node = selected.getChildRandom();
            }
            playout_result = rollout(simulate_node); // play a simulation

            // BACKPROPAGATION
            backpropagate(simulate_node, playout_result);

        }

        /* Exploring the use of serialization to persist tree knowledge across moves/games (not used).
        // Save/update tree
        ArrayList<MCTSNode> children_of_root = root.getNodeChildren();
        ArrayList<SerializableNode> sernodes = new ArrayList<>();
        MCTSState temp;
        for(MCTSNode c : children_of_root) {
            temp = c.getNodeState();
            sernodes.add(new SerializableNode(temp.getVisits(), temp.getScore(), temp.getPbs().hashCode(), temp.getPlayerno()));
        }
        tree.put(pbsclonedhash, sernodes);

         Serialize tree
        MCTSExecuter.serialize(tree, TREE_FILE_NAME);
         */


        // Finally, determine node with the highest score (visits).
        // This node will be used as our next move.
        MCTSNode chosen = root.getBestChild();
        PentagoMove next_move = chosen.getNodeState().getPm();

        return next_move;

    }

    /**
     * MCTS methods for performing algorithm.
     * 1. Selection
     * 2. Expansion
     * 3. Simulation
     * 4. Backpropagation
     */

    /**
     * Determine the most promising child (tree leaf) from a given start node to explore based on UCT computations.
     * @param start_node
     * @return
     */
    public MCTSNode select(MCTSNode start_node) {
        MCTSNode n = start_node;
        // get all the way down to a leaf node in our mcts tree
        while (n.getNodeChildren().size() != 0) { // as long as we have children to select from to expand
            n = getBestNodeUCT(n); // will recurse on the most promising node at each iteration!
        }
        // return leaf node from our mcts tree which has the best UCT value
        return n;
    }

    /**
     * Monte Carlo Upper Confidence Tree Computations.
     */
    //-------------------------------------------
    // Compute UCT
    public double computeUCT(int visit_at_node, double node_score, int total_visits) {
        if (visit_at_node == 0) {
            return Integer.MAX_VALUE;
        }
        double exploitation = (node_score) / (double) visit_at_node;
        double exploration = SCALING_CONSTANT * Math.sqrt(Math.log(total_visits) / (double) visit_at_node);
        double uct_value = exploitation + exploration;
        return uct_value;
    }

    // Select best child node to expand
    public MCTSNode getBestNodeUCT(MCTSNode n) {
        // Parent
        int visits_parents = n.getNodeState().getVisits();
        // For comparison
        double best = Integer.MIN_VALUE;
        double curr;
        MCTSNode node = null;
        // Simply get all the children for given node, compute UCT for each, and select best one.
        ArrayList<MCTSNode> children = n.getNodeChildren();
        for (MCTSNode c : children) {
            curr = computeUCT(c.getNodeState().getVisits(), c.getNodeState().getScore(), visits_parents);
            if (curr > best) { // if better than current best, save node
                best = curr;
                node = c;
            }
        }
        return node;
    }
    //-------------------------------------------

    /**
     * Expand a promising node which is not a leaf (no winner yet).
     * @param node
     */
    //-------------------------------------------
    public void expand(MCTSNode node) {
        // Produce list of expanded states from the given node (performing all moves, adding to search tree the resulting states)
        ArrayList<MCTSState> expanded_states = node.getNodeState().getExpandedNodeStates();
        for (MCTSState s : expanded_states) {
            // For each expanded node states, add them to the monte carlo search tree
            MCTSNode n = new MCTSNode(s);
            n.setNodeParent(node);
            // set player no to opponent for child
            n.getNodeState().setPlayerno(MyTools.getOpponent(node.getNodeState().getPbs()));

            // Add this newly created expanded state to list of children of parent node
            ArrayList<MCTSNode> children = node.getNodeChildren();
            children.add(n);
            node.setNodeChildren(children);
        }
        // now, our 'node' has the expanded nodes as its children
    }
    //-------------------------------------------

    /**
     * Run a simulation to determine outcome of a rollout.
     * @param node
     * @return
     */
    //-------------------------------------------
    public int rollout(MCTSNode node) {

        // Temp node and state
        MCTSNode temp_node = new MCTSNode(node);
        MCTSState temp_state = temp_node.getNodeState();

        // Determine a winner from a rollout of random moves
        int winner = temp_state.getPbs().getWinner();
        while (winner == Board.NOBODY) {
            temp_state.switchPlayer();
            // play random moves
            temp_state.playRandomMove();
            // update winner
            winner = temp_state.getPbs().getWinner();
        }

        // Determine winner and set scores to respective node states.
        // Set Integer.MAX_VALUE to node state if our agent won, Integer.MIN_VALUE
        if (winner == OPPONENT) {
            temp_node.getNodeParent().getNodeState().setScore(Integer.MIN_VALUE);
        } else {
            temp_node.getNodeParent().getNodeState().setScore(Integer.MAX_VALUE);
        }

        return winner;
    }
    //-------------------------------------------

    /**
     * Perform backprogation, updating visits and score values of nodes involved in a certain playout.
     * @param node
     * @param winner
     */
    //-------------------------------------------
    public void backpropagate(MCTSNode node, int winner) { // playerNo represents the winner from the playout
        MCTSNode temp = node; // use temp as a pointer for current visiting node
        while (temp != null) { // all the way back up to root of our MCTS
            temp.getNodeState().updateVisits(); // update visit scores for nodes used in playout
            if (temp.getNodeState().getPlayerno() == winner) {
                // increment score for winning player at each node along path
                temp.getNodeState().updateScore(INCR_SCORE);
            }
            // move up one node in our search tree
            temp = temp.getNodeParent();
        }
    }
    //-------------------------------------------

    /**
     * Getters and setters for this class.
     */

    public int getTimeAllowed() {
        return time_allowed;
    }

    public long getStartTime() {
        return start_time;
    }

    public void setStartTime(long start_time) {
        this.start_time = start_time;
    }

    public void setTimeAllowed(int time_allowed) {
        this.time_allowed = time_allowed;
    }


    //-------------------------------------------

    /**
     * Serialization and deserialization exploration.
     * Not used in the final implementation, but left in the code to show exploration work done.
     */

    /**
     * Display contents of our serialized tree.
     */
    public static void showTree(Map<Integer, ArrayList<SerializableNode>> tree) {
        for (Integer k : tree.keySet()) {
            String key = k.toString();
            String values = tree.get(k).toString();
            MyTools.print("Key: " + key + ", Values: " + values);
        }
    }

    /**
     * Load the serialized tree.
     */
    public static Map<Integer, ArrayList<SerializableNode>> loadTree() {
        Map<Integer, ArrayList<SerializableNode>> tree = new HashMap<>();
        tree = MCTSExecuter.deserialize(TREE_FILE_NAME, tree.getClass());
        if (tree == null) {
            return new HashMap<>();
        }
        return tree;

    }

    public Map<Integer, ArrayList<SerializableNode>> getTree() {
        return this.tree;
    }

    public void setTree(Map<Integer, ArrayList<SerializableNode>> t) {
        this.tree = t;
    }

    /**
     * Deserialize method. Handle any return type.
     */
    public static <T> T deserialize(String filename, Class<T> type) {
        T agent = null;
        try {
            FileInputStream file = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(file);

            // deserialize object and cast
            agent = (T) ois.readObject();

            MyTools.print("Deserialized.");

            // close file and input streams
            ois.close();
            file.close();

        } catch (Exception e) {
            MyTools.error(e.getMessage());
        }

        return agent;

    }


    /**
     * Serialize method.
     *
     * @return
     */
    public static void serialize(Object o, String filename) {
        try {
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(file);

            // write our agent to the file
            oos.writeObject(o);

            // close file and output streams
            oos.close();
            file.close();

            MyTools.print("Serialized.");

        } catch (Exception e) {
            MyTools.error(e.getMessage());
        }
    }

    /**
     * Finding and returning serialized children for a given board state hashcode.
     */
    public SerializableNode getSerializedChild(int parent_hashcode, int child_hashcode) {
        ArrayList<SerializableNode> children = tree.get(parent_hashcode);
        if (children != null) {
            for (SerializableNode sn : children) {
                if (sn.getPbshash() == child_hashcode) {
                    MyTools.print("Found serialized child.");
                    return sn;
                }
            }
        }
        return null;
    }


}
