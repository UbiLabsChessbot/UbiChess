package me.michaeljiang.movesystem.movesystem.component.arm7bot.model;


import me.michaeljiang.movesystem.movesystem.component.arm7bot.util.ArmTools;

/**
 * Created by MichaelJiang on 2017/4/23.
 */

public class MotoPosition {
    private int moto1 = 0;
    private int moto2 = 0;
    private int moto3 = 0;
    private int moto4 = 0;
    private int moto5 = 0;
    private int moto6 = 0;
    private int moto7 = 0;

    public MotoPosition(){

    }

    public MotoPosition(int[] data) {
        moto1 = data[0];
        moto2 = data[1];
        moto3 = data[2];
        moto4 = data[3];
        moto5 = data[4];
        moto6 = data[5];
        moto7 = data[6];
    }

    public void setMotoData(int[] data){
        moto1 = data[0];
        moto2 = data[1];
        moto3 = data[2];
        moto4 = data[3];
        moto5 = data[4];
        moto6 = data[5];
        moto7 = data[6];
    }

    public int getMoto1() {
        return moto1;
    }

    public void setMoto1(int moto1) {
        this.moto1 = moto1;
    }

    public int getMoto2() {
        return moto2;
    }

    public void setMoto2(int moto2) {
        this.moto2 = moto2;
    }

    public int getMoto3() {
        return moto3;
    }

    public void setMoto3(int moto3) {
        this.moto3 = moto3;
    }

    public int getMoto4() {
        return moto4;
    }

    public void setMoto4(int moto4) {
        this.moto4 = moto4;
    }

    public int getMoto5() {
        return moto5;
    }

    public void setMoto5(int moto5) {
        this.moto5 = moto5;
    }

    public int getMoto6() {
        return moto6;
    }

    public void setMoto6(int moto6) {
        this.moto6 = moto6;
    }

    public int getMoto7() {
        return moto7;
    }

    public void setMoto7(int moto7) {
        this.moto7 = moto7;
    }


    public void setAngleInts(int[] represent){
        moto1 = represent[0];
        moto2 = represent[1];
        moto3 = represent[2];
        moto4 = represent[3];
        moto5 = represent[4];
        moto6 = represent[5];
        moto7 = represent[6];
    }

    public int[] getAngleInts(){
        return new int[]{moto1,moto2,moto3,moto4,moto5,moto6,moto7};
    }

    public int[] getRepresentInts(){
        int[] temp = getAngleInts();
        for(int i = 0; i < temp.length; i++){
            temp[i] = temp[i]*1000/180;
        }
        return temp;
    }

    public byte[] getBytes(){
        return ArmTools.motoToBytes(getRepresentInts());
    }

}
