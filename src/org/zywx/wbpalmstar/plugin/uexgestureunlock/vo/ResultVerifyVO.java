package org.zywx.wbpalmstar.plugin.uexgestureunlock.vo;

import java.io.Serializable;

public class ResultVerifyVO implements Serializable{
    private static final long serialVersionUID = 3320170667879570173L;
    private boolean isFinished;

    public boolean isFinished() {
        return isFinished;
    }

    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

}
