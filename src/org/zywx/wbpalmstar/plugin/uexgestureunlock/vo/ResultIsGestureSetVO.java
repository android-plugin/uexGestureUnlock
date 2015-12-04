package org.zywx.wbpalmstar.plugin.uexgestureunlock.vo;

import java.io.Serializable;

public class ResultIsGestureSetVO implements Serializable{
    private static final long serialVersionUID = -5294895494087674387L;
    private boolean result;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
