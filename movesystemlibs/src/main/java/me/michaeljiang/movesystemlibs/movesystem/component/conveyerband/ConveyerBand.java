package me.michaeljiang.movesystemlibs.movesystem.component.conveyerband;

/**
 * Created by MichaelJiang on 2017/5/7.
 */

public class ConveyerBand {
    private boolean isMove = false;
    private double position = 0;
    private boolean isReceiver = false;
    public boolean isMove() {
        return isMove;
    }

    public synchronized void setMove(boolean move) {
        isMove = move;
    }

    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    public String toString(){
        return "ConveyerBand\t"+this.position+"\tisMove\t"+String.valueOf(this.isMove)+"\n";
    }

    public boolean isReceiver() {
        return isReceiver;
    }

    public void setReceiver(boolean receiver) {
        isReceiver = receiver;
    }
}
