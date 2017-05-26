package me.michaeljiang.movesystemlibs.movesystem.model;


import me.michaeljiang.movesystemlibs.movesystem.component.arm7bot.model.MotoPosition;

/**
 * Created by MichaelJiang on 2017/5/7.
 */

public class Chess {
    private MotoPosition motoPosition;
    private double position = 0;//cm

    public Chess(){

    }

    public Chess(MotoPosition motoPosition,double position){
        this.motoPosition = motoPosition;
        this.position = position;
    }

    public MotoPosition getMotoPosition() {
        return motoPosition;
    }

    public void setMotoPosition(MotoPosition motoPosition) {
        this.motoPosition = motoPosition;
    }

    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position;
    }

}
