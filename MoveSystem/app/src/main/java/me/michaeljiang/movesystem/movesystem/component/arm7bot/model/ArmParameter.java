package me.michaeljiang.movesystem.movesystem.component.arm7bot.model;

/**
 * Created by MichaelJiang on 2017/4/22. V0.1.0
 * 机械手所有的固定数据，暂时应该没什么需要更改的
 */

public class ArmParameter {
    public static final int SERVO_NUM=7;//舵机数量
    public static final byte[] DEFAULT_MODE = {(byte)0xfe,(byte)0xf5,0x01};//可控模式
    public static final byte[] PROTECTION_MODE={(byte)0xfe,(byte)0xf5,0x02};//半无力模式
    public static final byte[] FORCELESS_MODE = {(byte)0xfe,(byte)0xf5,0x00};//无力模式

    public static final double[] thetaMin = { 0,  0, -1.134464,  0.17453292,  0,  0, 0};
    public static final double[] thetaMax = {Math.PI, Math.PI, 2.0071287, 2.9670596, Math.PI, Math.PI, Math.PI/2};
    public static final double a=120.0, b=40.0, c=198.50, d=30.05, e=77.80, f=22.10, g=12.0, h = 29.42;

    public static final int POSITION_MODE_FREE = 0;
    public static final int POSITION_MODE_X = 1;
    public static final int POSITION_MODE_Y = 2;
    public static final int POSITION_MODE_Z = 3;
}
