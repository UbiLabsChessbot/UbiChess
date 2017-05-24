package me.michaeljiang.movesystem.movesystem.component.arm7bot.util;


import me.michaeljiang.movesystem.movesystem.component.arm7bot.model.ReceiverResult;

/**
 * Created by MichaelJiang on 2017/4/22.
 * 机械手接受数据时所调用类 目前仅实现了IK6转换
 */

public class ArmReceiver {
    private static final String TAG = ArmReceiver.class.getSimpleName();

    /**
     * 接收到arduinoDue发回的数据并解析现在各舵机的角度
     * @param   receiverData
     * @return  接收结果
     * @Example
     * @State   Finish
     * @Version 1.0
     */
    public static ReceiverResult analysisReceived(int[] receiverData){
        ReceiverResult result = new ReceiverResult();
        int[] motor=new int[7];
        int[] force=new int[7];
        int mul;
        byte[] data = new byte[14];
        int flag=receiverData[16];
        String message="";
        //前14为的数据转换，moto是各舵机的角度，data是原本的byte内容，用于校对,flag为是否移动，force为活动力度
        for(int i=1;i<8;i++){
            motor[i-1]=(receiverData[i*2]&0x07)*128+(receiverData[i*2+1]);
            data[2*(i-1)]= (byte) ((byte)receiverData[i*2]&0x07);
            data[2*(i-1)+1]= (byte) receiverData[i*2+1];
            force[i-1]=receiverData[i*2]>>3;
            mul=1;if(force[i-1]>7)mul=-1;force[i-1]=(force[i-1]&0x07)*mul;
        }

        if(flag==1){
            result.setMoveing(false);
            message="机械手停止移动";
        }
        else {
            result.setMoveing(true);
            message="机械手正在移动移动";
        }
        result.setCode(1);
        result.setByteResult(data);
        result.setServoAngle(motor);
        result.setMessage(message);
        return result;
    }

}
