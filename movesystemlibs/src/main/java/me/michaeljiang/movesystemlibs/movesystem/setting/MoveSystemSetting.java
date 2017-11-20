package me.michaeljiang.movesystemlibs.movesystem.setting;

/**
 * Created by MichaelJiang on 2017/5/7.
 */

public class MoveSystemSetting {
    /**
     * ProjectSetting
     **/
    public final static String TAG_RECEIVER_FROM_ARM = "NetBot_Arm";//（机械手发送信息）
    public final static String TAG_RECEIVER_FROM_TRANSPORTER = "SingleSCM_To_Android";//（传送带发送信息）
    public final static String TAG_RECEIVER_FROM_CHESS_BELL = "NetBot_Chess_Bell_Transporter";//(棋钟向手机发送数据)
    public final static String TAG_SEND_TO_ARM = "NetBot_Android_Arm";//（手机向机械手发送数据）
    public final static String TAG_SEND_TO_Transporter = "Android_To_SingleSCM";//（手机向传送带发送数据）
}
