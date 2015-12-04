package org.zywx.wbpalmstar.plugin.uexgestureunlock.vo;

import java.io.Serializable;

public class CreateGestureVO implements Serializable{
    private static final long serialVersionUID = 2960775873944652949L;
    private boolean isNeedVerifyBeforeCreate;

    public boolean isNeedVerifyBeforeCreate() {
        return isNeedVerifyBeforeCreate;
    }

    public void setIsNeedVerifyBeforeCreate(boolean isNeedVerifyBeforeCreate) {
        this.isNeedVerifyBeforeCreate = isNeedVerifyBeforeCreate;
    }
}
