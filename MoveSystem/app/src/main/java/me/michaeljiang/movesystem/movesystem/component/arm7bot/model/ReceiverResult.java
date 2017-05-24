package me.michaeljiang.movesystem.movesystem.component.arm7bot.model;

/**
 * Created by MichaelJiang on 2017/4/22.
 * 用于接收机械手回传结果
 */

public class ReceiverResult {
    private int code;
    private boolean isMoveing;
    private String message = null;
    private byte[] byteResult = null;
    private int[]  ServoAngle = null;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public byte[] getByteResult() {
        return byteResult;
    }

    public void setByteResult(byte[] byteResult) {
        this.byteResult = byteResult;
    }

    public int[] getServoAngle() {
        return ServoAngle;
    }

    public void setServoAngle(int[] servoAngle) {
        ServoAngle = servoAngle;
    }

    public boolean isMoveing() {
        return isMoveing;
    }

    public void setMoveing(boolean moveing) {
        isMoveing = moveing;
    }

}
