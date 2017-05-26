package me.michaeljiang.movesystemlibs.movesystem.component.arm7bot.model;

/**
 * Created by MichaelJiang on 2017/4/22.
 * 用于保存转换后的结果
 */

public class TransformResult {
    private int code; //{0:Error,1:Success}
    private String message = null;
    private byte[] byteResult = null;

    public TransformResult(){

    }

    public TransformResult(boolean isAuto){
        if(isAuto == true){
            code = 1;
            message = "自动生成";
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public byte[] getByteResult() {
        return byteResult;
    }

    public void setByteResult(byte[] byteResult) {
        this.byteResult = byteResult;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
