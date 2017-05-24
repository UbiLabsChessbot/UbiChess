package me.michaeljiang.movesystem.movesystem.model;

import me.michaeljiang.movesystem.movesystem.component.arm7bot.model.TransformResult;
import me.michaeljiang.movesystem.movesystem.setting.ProjectSetting;
import me.michaeljiang.movesystem.movesystem.util.MoveSystemTool;

/**
 * Created by MichaelJiang on 2017/5/15.
 */

public class MoveData {
    private TransformResult transformResult = null;
    private String conveyerBandData = null;

    public TransformResult getTransformResult() {
        return transformResult;
    }

    public void setTransformResult(TransformResult transformResult) {
        this.transformResult = transformResult;
    }

    public String getConveyerBandData() {
        return conveyerBandData;
    }

    public void setConveyerBandData(String conveyerBandData) {
        this.conveyerBandData = conveyerBandData;
    }

}
