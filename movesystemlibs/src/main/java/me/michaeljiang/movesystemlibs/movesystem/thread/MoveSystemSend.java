package me.michaeljiang.movesystemlibs.movesystem.thread;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.michaeljiang.movesystemlibs.movesystem.component.arm7bot.Arm7Bot;
import me.michaeljiang.movesystemlibs.movesystem.component.bluetooth.MyBluetooth;
import me.michaeljiang.movesystemlibs.movesystem.component.conveyerband.ConveyerBand;
import me.michaeljiang.movesystemlibs.movesystem.component.mqtt.MyMqtt;
import me.michaeljiang.movesystemlibs.movesystem.model.MoveData;
import me.michaeljiang.movesystemlibs.movesystem.setting.MoveSystemSetting;


/**
 * Created by MichaelJiang on 2017/05/15.
 * Modify  by MichaelJiang on 2017/05/23
 * -新增waitTime 防止上位机没能收到下位机的回复而卡机
 * -新增canArmMove 放置舵机在滑轨为到指定位置就开始移动
 */

public class MoveSystemSend extends Thread{
    /**MOveSystem类参数**/
    private boolean isDebug = false;
    private String TAG = "MoveSystemSend";
    private boolean canArmMove = true;
    private int waitTime = 0;
    /**MoveSystemSend数据类**/
    private List<MoveData> moveDataList = new ArrayList<>();
    private MoveData moveData;

    /**发送依赖的类**/
    private MyMqtt myMqtt;
    private MyBluetooth myBluetooth;

    /**被控物体状态类**/
    private Arm7Bot myArm7Bot;
    private ConveyerBand mConveterBand;

    public void clearWaiting(){
        waitTime = 0;
    }

    /**
     * Send线程的初始化
     * @param conveyerBand  滑轨信息
     * @param arm7Bot       机械手信息
     * @param mqtt          Mqtt连接
     * @param bluetooth     蓝牙连接
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public MoveSystemSend(ConveyerBand conveyerBand,Arm7Bot arm7Bot, MyMqtt mqtt, MyBluetooth bluetooth){
        this.mConveterBand = conveyerBand;
        this.myBluetooth = bluetooth;
        this.myMqtt = mqtt;
        this.myArm7Bot = arm7Bot;
    }

    @Override
    public void run() {
        super.run();
        try {
            while(true){
                sleep(10);

                if(moveDataList.size()==0){
                    if(isDebug){
                        Log.d(TAG,"NoData");
                    }
                    continue;
                }

                if(!mConveterBand.isMove()&&!myArm7Bot.isMove()){
                    //两个都不动的情况下
                    moveData = moveDataList.get(0);
                    if(moveData.getConveyerBandData()!=null){
                        myMqtt.pubMsg(MoveSystemSetting.TAG_SEND_TO_Transporter,moveData.getConveyerBandData());
                        moveData.setConveyerBandData(null);
                        //为了确保对方收到了信息准备进行移动，可以由对方发送一个信息确保收到
                        mConveterBand.setMove(true);
                        setCanArmMove(false);
                        if(isDebug){
                            Log.d(TAG,"SendDataToConVeyerBand");
                        }
                        continue;
                    }

                    if(moveData.getTransformResult()!=null&&canArmMove){
                        waitTime = 0;
                        myBluetooth.sendMessage(moveData.getTransformResult().getByteResult());
                        moveData.setTransformResult(null);
                        sleep(300);
                        myArm7Bot.setMove(true);
                        if(isDebug){
                            Log.d(TAG,"SendDataToArm");
                        }
                        continue;
                    }
                    else{
                        waitTime++;
                        if(waitTime >= 30000){
                            canArmMove =true;
                            waitTime = 0;
                        }

                    }
                    if(moveData.getConveyerBandData() == null&moveData.getTransformResult() == null){
                        moveDataList.remove(0);
                        Log.d(TAG,"SendDataSuccess");
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 向移动线程中添加移动信息
     * @param moveData  移动信息
     * @return  添加是否成功
     * @State   Finish
     * @Version 1.0
     * @User    MichaelJiang
     */
    public synchronized boolean addMoveData(MoveData moveData){
        moveDataList.add(moveData);
        return true;
    }

    public boolean isCanArmMove() {
        return canArmMove;
    }

    public void setCanArmMove(boolean canArmMove) {
        this.canArmMove = canArmMove;
    }

}
