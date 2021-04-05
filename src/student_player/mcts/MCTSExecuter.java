package student_player.mcts;

import boardgame.Board;
import boardgame.Move;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;
import student_player.MyTools;

import java.io.*;
import java.util.ArrayList;
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

//         Load serialized tree
//        MyTools.print("Size of tree: "+MCTSExecuter.loadTree().size());
//        setTree(MCTSExecuter.loadTree());
//         MCTSExecuter.showTree(tree);


        MCTSNode root = new MCTSNode(pbs, null); // init root node of our MCTS, with no children
        OPPONENT = MyTools.getOpponent(pbs);
        root.getNodeState().setPlayerno(OPPONENT);

        // Define end time to respect time allocated
        long setup_time = (System.currentTimeMillis() - start_time);
        long endtime = System.currentTimeMillis() + (time_allowed - setup_time);

//        int depth = 0;
//        int max_depth = getMaxDepthAllowed(getTimeAllowed());
        // as long as we have time left to explore moves, and not reached a depth to far for computations
        while (System.currentTimeMillis() < endtime) {

            // SELECTION
            MCTSNode promising = getMostPromisingNode(root);

            // EXPANSION
            int expanded_winner = promising.getNodeState().getPbs().getWinner();
            if (expanded_winner == Board.NOBODY) { // check if this is not a leaf node (ie: if there is no winner yet)
                expandPromisingNode(promising);
            }

            // SIMULATION
            MCTSNode to_explore = promising;
            // Might have some child nodes if we expanded this node's states in the previous step
            if (promising.getNodeChildren().size() > 0) {
                to_explore = promising.getChildRandom(); // CAN MODIFY THIS SELECTION
            }
            int playout_result = simulatePlayout(to_explore);

            // BACKPROPAGATION
            backPropagate(to_explore, playout_result);

        }

//        // Save/update tree
//        ArrayList<MCTSNode> children_of_root = root.getNodeChildren();
//        ArrayList<SerializableNode> sernodes = new ArrayList<>();
//        MCTSState temp;
//        for(MCTSNode c : children_of_root) {
//            temp = c.getNodeState();
//            sernodes.add(new SerializableNode(temp.getVisits(), temp.getScore(), temp.getPbs().hashCode(), temp.getPlayerno()));
//        }
//        tree.put(pbsclonedhash, sernodes);

        // Serialize tree
//        MCTSExecuter.serialize(tree, TREE_FILE_NAME);

        // Finally, determine node with the highest score.
        // This node will be used as our next move.
        MCTSNode chosen = root.getHighestScoreChild();
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
     * Simply determine the "best" children of a give node to explore based on UCT computations.
     *
     * @param start_node
     * @return
     */
    public MCTSNode getMostPromisingNode(MCTSNode start_node) {
        MCTSNode n = start_node;
        // get all the way down to a leaf node...
        while (n.getNodeChildren().size() != 0) { // as long as we have children to select from to expand
            n = getBestNodeUCT(n); // will recurse on the most promising node at each iteration!
        }
        return n;
    }

    /**
     * Expand a promising node which is not a leaf (no winner yet).
     *
     * @param node
     */
    public void expandPromisingNode(MCTSNode node) {
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
    }

    /**
     * Run a simulation/rollout to determine outcome of a playout.
     *
     * @param node
     * @return
     */
    public int simulatePlayout(MCTSNode node) {
        MCTSNode temp_node = new MCTSNode(node);
        MCTSState temp_state = temp_node.getNodeState();

        // Determine a winner from a playout of random moves
        int winner = temp_state.getPbs().getWinner();
        while (winner == Board.NOBODY) {
            temp_state.switchPlayer();
            temp_state.performRandomMove(); // use random moves
            winner = temp_state.getPbs().getWinner(); // update winner
        }

        // Determine winner and set scores to respective node states.
        if (winner == OPPONENT) {
            temp_node.getNodeParent().getNodeState().setScore(Integer.MIN_VALUE);
        } else {
            temp_node.getNodeParent().getNodeState().setScore(Integer.MAX_VALUE);
        }

        return winner;
    }

    /**
     * Perform backprogation, updating visits and score values of nodes involved in a certain playout.
     *
     * @param node
     * @param playerNo
     */
    public void backPropagate(MCTSNode node, int playerNo) { // playerNo represents the winner from the playout
        MCTSNode temp = node; // use temp as a pointer for current visiting node
        while (temp != null) { // all the way back up to root of our MCTS
            temp.getNodeState().incrVists(); // update visit scores for nodes used in playout
            if (temp.getNodeState().getPlayerno() == playerNo) {
                // increment score for winning player at each node along path
                temp.getNodeState().updateScore(INCR_SCORE);
            }
            // move up one node in our search tree
            temp = temp.getNodeParent();
        }
    }

    /**
     * Display contents of our serialized tree.
     */
    public static void showTree(Map<Integer, ArrayList<SerializableNode>> tree) {
        for(Integer k : tree.keySet()) {
            String key = k.toString();
            String values = tree.get(k).toString();
            MyTools.print("Key: "+key+", Values: " + values);
        }
    }

    /**
     * Load the serialized tree.
     */
    public static Map<Integer, ArrayList<SerializableNode>> loadTree() {
        Map<Integer, ArrayList<SerializableNode>> tree = new HashMap<>();
        tree = MCTSExecuter.deserialize(TREE_FILE_NAME, tree.getClass());
        if(tree == null) {
            return new HashMap<>();
        }
        return tree;

    }

    /**
     * Serialization and deserialization
     */

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
        if(children != null) {
            for(SerializableNode sn : children) {
                if(sn.getPbshash() == child_hashcode) {
                    MyTools.print("Found serialized child.");
                    return sn;
                }
            }
        }
        return null;
    }

    /**
     * Monte Carlo Upper Confidence Tree Computations
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
            if (curr > best) {
                best = curr;
                node = c;
            }
        }
        return node;
    }
    //-------------------------------------------

    /**
     * Getters and setters for this class.
     * @return
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

    public int getMaxDepthAllowed(int time_allowed) {
        if (time_allowed > 10) {
            return 1000;
        }
        return Integer.MAX_VALUE;
    }

    public Map<Integer, ArrayList<SerializableNode>> getTree() {
        return this.tree;
    }
    public void setTree(Map<Integer, ArrayList<SerializableNode>> t) {
        this.tree = t;
    }

    public int getNumGamesPlayed() {
        return this.games_played;
    }

    /**
     * Helper functions to keep track of our MCTS learning.
     * Used for debugging and optimization.
     */



}
