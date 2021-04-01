package student_player.mcts;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;
import student_player.MyTools;

import java.util.ArrayList;

public class MCTSNode {

    private MCTSNode parent = null;
    private MCTSState state = null;
    private ArrayList<MCTSNode> children = new ArrayList<>();

    /**
     * Constructors
     */
    public MCTSNode(MCTSNode p, MCTSState s, ArrayList<MCTSNode> c) {
        this.parent = p;
        this.state = s;
        this.children = c;
    }
    public MCTSNode(MCTSState s) {
        this.state = s;
    }
    public MCTSNode(PentagoBoardState pbs, PentagoMove pm) {
        this.state = new MCTSState(pbs, pm);
    }
    public MCTSNode(MCTSNode n) {
        this.state = new MCTSState(n.getNodeState());
        parent = n.getNodeParent();
        if(parent != null) {
            this.setNodeParent(n);
        }
        this.setNodeChildren(n.getNodeChildren());
    }

    /**
     * Methods
     */

    /**
     * Compute the node with the highest sch
     * @return
     */
    public MCTSNode getHighestScoreChild() {
        int highest = 0; // init highest score
        MCTSNode node = null; // to be returned
        int v;
        ArrayList<MCTSNode> children = this.getNodeChildren(); // list current node's children
        for(MCTSNode c : children) {
            // most commonly used strategy is to select the child with the highest number of visits
            v = c.getNodeState().getVisits();
            if(v > highest) { // update if found better score
                highest = v;
                node = c;
            }
        }
        return node;
    }

    /**
     * Simply get a random child (used in simulation step).
     * @return
     */
    public MCTSNode getChildRandom() {
        int num_childs = this.getNumberOfChildren();
        int selected = MyTools.getRandomNumber(0, num_childs);
        ArrayList<MCTSNode> children = this.getNodeChildren();
        return children.get(selected);
    }

    public int getNumberOfChildren() {
        return this.children.size();
    }

    /**
     * Getters and setters
     */
    public MCTSState getNodeState() {
        return this.state;
    }
    public MCTSNode getNodeParent() {
        return this.parent;
    }
    public ArrayList<MCTSNode> getNodeChildren() {
        return this.children;
    }
    public void setNodeState(MCTSState s) {
        this.state = s;
    }
    public void setNodeParent(MCTSNode p) {
        this.parent = p;
    }

    /**
     * Setting the node children requires iterating through a children arraylist and adding
     * each child to the children list of this node.
     * @param children
     */
    public void setNodeChildren(ArrayList<MCTSNode> children) {
        this.children = new ArrayList<>();
        for(MCTSNode c : children) {
            this.children.add(c);
        }
    }


}
