package org.zywx.wbpalmstar.plugin.uexgestureunlock.fragment;

import android.widget.FrameLayout;

import org.zywx.wbpalmstar.base.view.BaseFragment;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.util.DrawArrowListener;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.vo.ConfigGestureVO;

public class GestureBaseFragment extends BaseFragment{

    protected DrawArrowListener mDrawArrowListener;
    private ConfigGestureVO mData;

    public void setParentViewFrameLayout(FrameLayout view) {
        this.mDrawArrowListener = new DrawArrowListener(this.getActivity(), view, mData);
    }

    public void setData(ConfigGestureVO data) {
        this.mData = data;
    }
}
