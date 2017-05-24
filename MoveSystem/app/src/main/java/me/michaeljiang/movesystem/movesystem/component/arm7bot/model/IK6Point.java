package me.michaeljiang.movesystem.movesystem.component.arm7bot.model;
/**
 * Created by MichaelJiang on 2017/2/15 V0.1.0
 * -IK6移动方法的Model，方法中保存着初始变量
 * Modify  by MichaelJiang on 2017/4/22 V0.1.1
 * -修正初始参数，重新整合
 */

public class IK6Point {
    private int[] data;         //发送前的整体数组position+vec56+vec67+moto6
    private int[] position;     //现在point6点的坐标
    private int[] vec56;        //现在Point6点的向量朝向
    private int[] vec67;        //现在Point6点的向量朝向
    private int moto6;          //此时是否吸取或者机械手抓取
    private int mode = ArmParameter.POSITION_MODE_FREE;

    public IK6Point(){
        data = new int[10];
        position=new int[]{0,175,100};    //现在point6点的坐标
        vec56=new int[]{0,300,0};  //现在Point6点的向量朝向
        vec67=new int[]{0,0 ,100};  //现在Point6点的向量朝向
        moto6 = 500;//此时是否吸取或者机械手抓取
    }

    public IK6Point(int[] data,int[] position,int[] vec56,int[] vec67,int moto6){
        this.data = data;
        this.position = position;
        this.vec56 = vec56;
        this.vec67 = vec67;
        this.moto6 = moto6;
    }

    public void changeData(){
        for(int i = 0 ;i < 3 ;i++){
            data[i]   = position[i];//0 1 2
            data[i+3] = vec56[i];   //3 4 5
            data[i+6] = vec67[i];   //6 7 8
        }
        data[9] = moto6;
    }

    public int[] getData(){
        //toByte前将最新的数据放进data
        changeData();
        return data;
    }

    public int[] getPosition() {
        return position;
    }

    public void setPosition(int[] position) {
        this.position = position;
    }

    public int[] getVec56() {
        return vec56;
    }

    public void setVec56(int[] vec56) {
        this.vec56 = vec56;
    }

    public int[] getVec67() {
        return vec67;
    }

    public void setVec67(int[] vec67) {
        this.vec67 = vec67;
    }

    public int getMoto6() {
        return moto6;
    }

    public void setMoto6(int moto6) {
        this.moto6 = moto6;
    }

}
