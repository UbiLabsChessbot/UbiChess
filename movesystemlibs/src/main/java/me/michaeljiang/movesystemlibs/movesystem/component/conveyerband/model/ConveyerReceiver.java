package me.michaeljiang.movesystemlibs.movesystem.component.conveyerband.model;

/**
 * Created by MichaelJiang on 2017/5/15.
 */

public class ConveyerReceiver {

    private int code = 0;
    private String message = "";
    private PlatformState platformState = new PlatformState();

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

    public PlatformState getPlatformState() {
        return platformState;
    }

    public void setPlatformState(PlatformState platformState) {
        this.platformState = platformState;
    }

    public String toString(){
        return "code\t\t"+this.code+"\n"+"message\t\t"+this.message+"\n"+this.platformState.toString();
    }

}
