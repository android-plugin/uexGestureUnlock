package org.zywx.wbpalmstar.plugin.uexgestureunlock.vo;

import java.io.Serializable;

public class ResultFailedVO extends ResultVerifyVO implements Serializable{
    private static final long serialVersionUID = -8755161979289825084L;
    private int errorCode;
    private String errorString;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorString() {
        return errorString;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }
}
