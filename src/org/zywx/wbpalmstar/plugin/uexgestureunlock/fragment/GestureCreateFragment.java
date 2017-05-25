package org.zywx.wbpalmstar.plugin.uexgestureunlock.fragment;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.zywx.wbpalmstar.base.BUtility;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.EUExGestureUnlock.GestureCreateListener;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.JsConst;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.util.GestureUtil;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.vo.ConfigGestureVO;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.vo.ResultEventVO;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.vo.ResultFailedVO;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.widget.GestureContentView;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.widget.GestureDrawLine;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.widget.LockIndicator;

public class GestureCreateFragment extends GestureBaseFragment implements OnClickListener{

    private RelativeLayout mBg;
    private GestureCreateListener mGestureCreateListener;
    private TextView mTextCancel;
    private LockIndicator mLockIndicator;
    private TextView mTextTip;
    private FrameLayout mGestureContainer;
    private GestureContentView mGestureContentView;
    private TextView mTextReset;
    private boolean mIsFirstInput = true;
    private String mFirstPassword = null;
    private ConfigGestureVO mData;
    private LinearLayout mGestureTipLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(
                    EUExUtil.getResLayoutID("plugin_uexgestureunlock_gesture_edit"),
                    container, false);
            mBg = (RelativeLayout) view.findViewById(EUExUtil.getResIdID("plugin_uexGestureUnlock_bg"));
            mGestureTipLayout = (LinearLayout) view.findViewById(
                    EUExUtil.getResIdID("plugin_uexGestureUnlock_gesture_tip_layout"));
            mTextCancel = (TextView) view.findViewById(EUExUtil
                    .getResIdID("plugin_uexGestureUnlock_text_cancel"));
            mTextReset = (TextView) view.findViewById(EUExUtil
                    .getResIdID("plugin_uexGestureUnlock_text_reset"));
            mTextReset.setClickable(false);
            mGestureContainer = (FrameLayout) view.findViewById(EUExUtil
                    .getResIdID("plugin_uexGestureUnlock_gesture_container"));
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = GestureUtil.getScreenDisplay(this.getActivity())[0] / 8;
            layoutParams.leftMargin = GestureUtil.getScreenDisplay(this.getActivity())[0] / 8;
            layoutParams.addRule(RelativeLayout.BELOW, EUExUtil.getResIdID("plugin_uexGestureUnlock_gesture_tip_layout"));
            mGestureContainer.setLayoutParams(layoutParams);
            setParentViewFrameLayout(mGestureContainer);
            setTipLayout();
            setUpData();
            // 初始化一个显示各个点的viewGroup
            mGestureContentView = new GestureContentView(this.getActivity(), false, "",
                    new GestureDrawLine.GestureCallBack() {
                @Override
                public void onGestureCodeInput(final String inputCode) {
                    if (!isInputPassValidate(inputCode)) {
                        setTextTipError(String.format(mData.getCodeLengthErrorPrompt(),
                                mData.getMinimumCodeLength()));
                        mGestureContentView.clearDrawlineState(mData.getErrorRemainInterval(), true);
                        mGestureCreateListener.onEventOccur(JsConst.EVENT_LENGTH_ERROR);
                        return;
                    }
                    if (mIsFirstInput) {
                        mFirstPassword = inputCode;
                        updateCodeList(inputCode);
                        mGestureContentView.clearDrawlineState(mData.getSuccessRemainInterval(), false);
                        setTextTipNormal(mData.getCodeCheckPrompt());
                        mTextReset.setClickable(true);
                        mTextReset.setText(mData.getRestartCreationButtonTitle());
                        mGestureCreateListener.onEventOccur(JsConst.EVENT_SECOND_INPUT);
                    } else {
                        if (inputCode.equals(mFirstPassword)) {
                            mGestureContentView.clearDrawlineState(mData.getSuccessRemainInterval(), false);
                            setTextTipNormal(mData.getCreationSucceedPrompt());
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mGestureCreateListener != null){
                                        mGestureCreateListener.onCreateFinished(inputCode);
                                        mGestureCreateListener.onEventOccur(JsConst.EVENT_CREATE_SUCCESS);
                                    }
                                }
                            }, mData.getSuccessRemainInterval());
                        } else {
                            mGestureCreateListener.onEventOccur(JsConst.EVENT_NOT_SAME);
                            setTextTipError(mData.getCheckErrorPrompt());
                            // 左右移动动画
                            Animation shakeAnimation = AnimationUtils.loadAnimation(
                                    GestureCreateFragment.this.getActivity(),
                                    EUExUtil.getResAnimID("plugin_uexgestureunlock_shake"));
                            mTextTip.startAnimation(shakeAnimation);
                            // 保持绘制的线，1.0秒后清除
                            mGestureContentView.clearDrawlineState(mData.getErrorRemainInterval(), true);
                            mLockIndicator.setErrorState();
                        }
                    }
                    mIsFirstInput = false;
                }

                @Override
                public void checkedSuccess() {

                }

                @Override
                public void checkedFail() {

                }
            }, mDrawArrowListener, mData);
            // 设置手势解锁显示到哪个布局里面
            mGestureContentView.setParentView(mGestureContainer);
            updateCodeList("");
            setUpListeners();
            if (mGestureCreateListener != null){
                mGestureCreateListener.onEventOccur(JsConst.EVENT_PLUGIN_INIT);
                mGestureCreateListener.onEventOccur(JsConst.EVENT_START_CREATE);
            }
        } catch (Exception e) {
            if (mGestureCreateListener != null){
                ResultFailedVO result = new ResultFailedVO();
                result.setIsFinished(false);
                result.setErrorCode(JsConst.ERROR_CODE_UNKNOWN);
                result.setErrorString(EUExUtil
                        .getString("plugin_uexGestureUnlock_errorCodeUnknown"));
                mGestureCreateListener.onCreateFailed(result);
            }
        }
        return view;
    }

    private void setTipLayout() {
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLockIndicator = new LockIndicator(this.getActivity(), mData);
        mLockIndicator.setLayoutParams(lp1);
        mGestureTipLayout.addView(mLockIndicator);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTextTip = new TextView(this.getActivity());
        lp2.gravity = Gravity.CENTER_HORIZONTAL;
        lp2.setMargins(0, 20, 0, 0);
        setTextTipNormal(mData.getCreationBeginPrompt());
        mTextTip.setLayoutParams(lp2);
        mGestureTipLayout.addView(mTextTip);
    }

    private void setUpData() {
        if (!TextUtils.isEmpty(mData.getBackgroundImage())){
            mBg.setBackgroundDrawable(new BitmapDrawable(
                    BUtility.getLocalImg(this.getActivity(),
                            mData.getBackgroundImage())));
        }else{
            mBg.setBackgroundColor(mData.getBackgroundColor());
        }
        mTextCancel.setTextColor(mData.getNormalThemeColor());
        mTextCancel.setText(mData.getCancelCreationButtonTitle());
        mTextReset.setTextColor(mData.getNormalThemeColor());
        mTextReset.setText(mData.getRestartCreationButtonTitle());
    }

    private void setTextTipError(String tips) {
        mTextTip.setTextColor(mData.getErrorThemeColor());
        mTextTip.setText(tips);
    }

    private void setTextTipNormal(String tips) {
        mTextTip.setTextColor(mData.getNormalThemeColor());
        mTextTip.setText(tips);
    }

    private void setUpListeners() {
        mTextCancel.setOnClickListener(this);
        mTextReset.setOnClickListener(this);
    }

    public void setGestureCreateListener(GestureCreateListener listener) {
        this.mGestureCreateListener = listener;
    }

    private void updateCodeList(String inputCode) {
        // 更新选择的图案
        mLockIndicator.setPath(inputCode);
    }

    private boolean isInputPassValidate(String inputPassword) {
        return (!TextUtils.isEmpty(inputPassword) && inputPassword.length() >= mData.getMinimumCodeLength());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == EUExUtil.getResIdID("plugin_uexGestureUnlock_text_cancel")){
            if (mGestureCreateListener != null){
                mGestureCreateListener.onCancel();
            }
        }else if (v.getId() == EUExUtil.getResIdID("plugin_uexGestureUnlock_text_reset")){
            mIsFirstInput = true;
            updateCodeList("");
            setTextTipNormal(mData.getCreationBeginPrompt());
        }
    }

    public void setData(ConfigGestureVO data) {
        if (data == null){
            mData = new ConfigGestureVO();
        }else{
            this.mData = data;
        }
        super.setData(mData);
    }

}
