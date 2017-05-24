package me.michaeljiang.movesystem.movesystem.component.arm7bot;


import me.michaeljiang.movesystem.movesystem.component.arm7bot.model.ArmParameter;
import me.michaeljiang.movesystem.movesystem.component.arm7bot.model.IK6Point;
import me.michaeljiang.movesystem.movesystem.component.arm7bot.model.MotoPosition;
import me.michaeljiang.movesystem.movesystem.component.arm7bot.model.ReceiverResult;
import me.michaeljiang.movesystem.movesystem.component.arm7bot.model.TransformResult;
import me.michaeljiang.movesystem.movesystem.component.arm7bot.util.ArmCheck;
import me.michaeljiang.movesystem.movesystem.component.arm7bot.util.ArmReceiver;
import me.michaeljiang.movesystem.movesystem.component.arm7bot.util.ArmSend;
import me.michaeljiang.movesystem.movesystem.component.arm7bot.util.ArmTools;

/**
 * Created by MichaelJiang on 2017/2/14.
 * 机械手的main函数，所有接口和数据均在这里，除了接口外请勿将计算与转换过程放在这里
 *
 */

public class Arm7Bot {
    //调试用TAG
    private String TAG = Arm7Bot.class.getSimpleName();
    private ArmCheck armCheck = new ArmCheck();

    //机械手三大模式
    public byte[] DefaultMode() {
        return ArmParameter.DEFAULT_MODE;
    }
    public byte[] ProtectionMode() {
        return ArmParameter.PROTECTION_MODE;
    }
    public byte[] ForcelessMode() {
        return ArmParameter.FORCELESS_MODE;
    }

    //机械手相关参数
    private boolean isMove = false;
    /**
     * 将IK6Point转换成标准的Arm7Bot接受的byte数组
     * @param ik6Point 包含机械手顶点及相关向量
     * @return JSON字符串 包含Code、byteResult、Message
     * @Example
     * @State   Finish
     * @Version 1.0
     */
    public TransformResult toStandIK6Byte(IK6Point ik6Point){
        TransformResult result = new TransformResult();
        //toByte转换
        byte[] ik6Byte = ArmSend.initIK6Byte(ik6Point.getData());
        //check是否能到达该点
        if(armCheck.checkIK6Byte(ik6Byte)){
            result.setCode(1);
            result.setByteResult(ik6Byte);
            result.setMessage("IK6 : "+ ArmTools.bytesToHexString(ik6Byte));
        }
        else{
            result.setCode(0);
            result.setByteResult(null);
            result.setMessage("ERROR : "+ "无法到达的位置");
        }
        return result;
    }

    /**
     *  将传进来的motoPosition转换成可以被机械手接受的byte数组
     * @param       motoPosition  {moto1:}
     * @return
     * @Example
     * @State   Finish
     * @Version 1.0
     */
    public TransformResult toStandMotoByte(MotoPosition motoPosition){
        byte[] BeginningByte = {(byte) 0xFE, (byte) 0xF9};
        TransformResult result = new TransformResult();
        if(armCheck.checkMotoData(motoPosition.getRepresentInts())){
            result.setCode(1);
            result.setByteResult(ArmTools.spliceBytes(BeginningByte,motoPosition.getBytes()));
            result.setMessage("MotoChange Success");
        }
        else{
            result.setCode(0);
            result.setByteResult(null);
            result.setMessage("MotoData Invalid");
        }
        return result;
    }

    /**
     * 接收回传数据，并返回解析结果
     * @param   receiverData
     * @return  JSON字符串 包含Code、byteResult、servoAngle、Message
     * @Example
     * @State   Finish
     * @Version 1.0
     */
    public ReceiverResult standReceiverResult(int[] receiverData){
        ReceiverResult result = ArmReceiver.analysisReceived(receiverData);
        return result;
    }

    public boolean isMove() {
        return isMove;
    }

    public synchronized void setMove(boolean move) {
        isMove = move;
    }

}
