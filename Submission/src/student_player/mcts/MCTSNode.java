package student_player.mcts;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;
import student_player.MyTools;
import java.util.ArrayList;

public class MCTSNode {

    // Basic node tree attributes (parent, children, state)
    private MCTSNode parent = null;
    private MCTSState state;
    private ArrayList<MCTSNode> children = new ArrayList<>();

    /**
     * Constructors needed based on the different ways we will initialize our MCTS nodes.
     */
    public MCTSNode(MCTSState s) {
        this.state = s;
    }

    public MCTSNode(PentagoBoardState pbs, PentagoMove pm) {
        this.state = new MCTSState(pbs, pm);
    }

    public MCTSNode(MCTSNode n) {
        this.state = new MCTSState(n.getNodeState());
        parent = n.getNodeParent();
        if (parent != null) {
            this.setNodeParent(n);
        }
        this.setNodeChildren(n.getNodeChildren());
    }

    /**
     * Retrieve the 'best' node to play after finishing rollout and backpropagation.
     * Choose child with the highest visits score.
     * @return
     */
    public MCTSNode getBestChild() {
        int highest = 0; // init highest visits count
        MCTSNode node = null; // to be returned
        int v;
        ArrayList<MCTSNode> children = this.getNodeChildren(); // list current node's children
        for (MCTSNode c : children) {
            // most commonly used strategy is to select the child with the highest number of visits
            v = c.getNodeState().getVisits();
            if (v > highest) { // update if found better score
                highest = v;
                node = c;
            }
        }
        return node;
    }

    /**
     * Getters and setters
     */

    // State
    public MCTSState getNodeState() {
        return this.state;
    }

    // Parent
    public MCTSNode getNodeParent() {
        return this.parent;
    }
    public void setNodeParent(MCTSNode p) {
        this.parent = p;
    }

    /**
     * Setting the node children requires iterating through a children arraylist and adding
     * each child to the children list of this node.
     *
     * @param children
     */
    public void setNodeChildren(ArrayList<MCTSNode> children) {
        this.children = new ArrayList<>();
        for (MCTSNode c : children) {
            this.children.add(c);
        }
    }
    public ArrayList<MCTSNode> getNodeChildren() {
        return this.children;
    }


    /**
     * Simply get a random child (used in rollout step).
     * @return
     */
    public MCTSNode getChildRandom() {
        int num_childs = getNumberOfChildren();
        // fetch a random int to select random child from array list
        int selected = MyTools.getRandomNumber(0, num_childs);
        ArrayList<MCTSNode> children = getNodeChildren();
        return children.get(selected);
    }

    public int getNumberOfChildren() {
        return this.children.size();
    }


}
