package me.michaeljiang.movesystem.movesystem.component.arm7bot.util;

import android.util.Log;

/**
 * Created by MichaelJiang on 2017/4/22.
 * 机械手发送数据时所调用类 目前仅实现了IK6转换
 */

public class ArmSend {
    private static final String TAG = ArmSend.class.getSimpleName();

    /**
     * 将data数据转变为ik6发送数据
     * @param data
     * @return
     * @Example
     * @State   Finish
     * @Version 1.0
     */
    public static byte[] initIK6Byte(int[] data){
        byte[] IK6={(byte)0xfe,(byte)0xFA,0x08,0x00,0x01,0x2F,0x00,0x64,0x08,0x00,0x08,0x00, 0x09,0x44,0x01,0x48,0x01,0x48,0x01,0x48,0x01,0x48};//IK6 发送数组
        if (data.length!=10){
            Log.d("Arm7Bot","Arm7Bot_Send:changeIK6(int[] data) data is invalid");
            return null;
        }
        int j = 0;
        for(int i=2;i<19;i=i+2){//Point6 的xyz变化
            if(data[j]>0){
                IK6[i]=(byte)((data[j]/128)&0x7F);
                IK6[i+1]=(byte)(data[j++]&0x7F);
            }
            else if(data[j]==0){
                IK6[i]=0x08;
                IK6[i+1]=0x00;
                j++;
            }
            else{
                IK6[i]=(byte) (((byte)((-data[j]/128)&0x7F))|0x08);
                IK6[i+1]=(byte)(-data[j++]&0x7F);
            }
        }
        //moto6转换
        IK6[20] = (byte)((data[9]/128)&0x7F);
        IK6[21] = (byte)(data[9]&0x7F);
        return IK6;
    }

}
