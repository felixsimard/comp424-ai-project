package student_player;

import java.io.Serializable;

public class SerializableNode implements Serializable {

    private int visits;
    private double score;
    private int playerno;
    private int pbshash;

    public SerializableNode(int visits, double score, int pbshash, int playerno) {
        this.visits = visits;
        this.score = score;
        this.playerno = playerno;
        this.pbshash = pbshash;
    }

    @Override
    public String toString() {
        return "("+this.visits+", "+this.getScore()+", "+this.getPbshash()+", "+this.getPlayerno()+")";
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getPlayerno() {
        return playerno;
    }

    public void setPlayerno(int playerno) {
        this.playerno = playerno;
    }

    public int getPbshash() {
        return pbshash;
    }

    public void setPbshash(int pbshash) {
        this.pbshash = pbshash;
    }
}
