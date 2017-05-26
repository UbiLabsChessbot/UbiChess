package me.michaeljiang.movesystemlibs.movesystem.component.conveyerband.model;

/**
 * Created by MichaelJiang on 2017/5/15.
 */

public class PlatformState {

    private double PlatformPosition = 0;
    private boolean isMove = false;

    public double getPlatformPosition() {
        return PlatformPosition;
    }

    public void setPlatformPosition(double platformPosition) {
        PlatformPosition = platformPosition;
    }

    public boolean isMove() {
        return isMove;
    }

    public void setMove(boolean move) {
        isMove = move;
    }

    public String toString(){
        return "PlatformPosition\t"+this.PlatformPosition+"\nisMove\t"+String.valueOf(this.isMove)+"\n";
    }

}
