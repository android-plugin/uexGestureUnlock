package org.zywx.wbpalmstar.plugin.uexgestureunlock.fragment;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.zywx.wbpalmstar.base.BUtility;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.EUExGestureUnlock.GestureVerifyListener;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.JsConst;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.util.GestureUtil;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.vo.ConfigGestureVO;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.vo.ResultFailedVO;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.vo.ResultVerifyVO;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.widget.GestureContentView;
import org.zywx.wbpalmstar.plugin.uexgestureunlock.widget.GestureDrawLine;

public class GestureVerifyFragment extends GestureBaseFragment implements OnClickListener{

    private RelativeLayout mBg;
    private ImageView mImgUserLogo;
    private TextView mTextTip;
    private FrameLayout mGestureContainer;
    private GestureContentView mGestureContentView;
    private TextView mTextForget;
    private GestureVerifyListener mListener;
    private String mGestureCode;
    private ConfigGestureVO mData;
    private int mLeftCount;
    private boolean isHasCreate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(EUExUtil.getResLayoutID("plugin_uexgestureunlock_gesture_verify"),
                    container, false);
            mBg = (RelativeLayout) view.findViewById(EUExUtil.getResIdID("plugin_uexGestureUnlock_bg"));
            mImgUserLogo = (ImageView) view.findViewById(EUExUtil
                    .getResIdID("plugin_uexGestureUnlock_user_logo"));
            mTextTip = (TextView) view.findViewById(EUExUtil
                    .getResIdID("plugin_uexGestureUnlock_text_tip"));
            mGestureContainer = (FrameLayout) view.findViewById(EUExUtil
                    .getResIdID("plugin_uexGestureUnlock_gesture_container"));
            mTextForget = (TextView) view.findViewById(EUExUtil
                    .getResIdID("plugin_uexGestureUnlock_text_forget_gesture"));
            if (mData != null){
                setUpData();
            }
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.topMargin = GestureUtil.getScreenDisplay(this.getActivity())[0] / 8;
            layoutParams.leftMargin = GestureUtil.getScreenDisplay(this.getActivity())[0] / 8;
            layoutParams.addRule(RelativeLayout.BELOW, EUExUtil.getResIdID("plugin_uexGestureUnlock_gesture_tip_layout"));
            mGestureContainer.setLayoutParams(layoutParams);
            setParentViewFrameLayout(mGestureContainer);
            // 初始化一个显示各个点的viewGroup
            mGestureContentView = new GestureContentView(this.getActivity(), true, mGestureCode,
                    new GestureDrawLine.GestureCallBack() {

                        @Override
                        public void onGestureCodeInput(String inputCode) {

                        }

                        @Override
                        public void checkedSuccess() {
                            mGestureContentView.clearDrawlineState(mData.getSuccessRemainInterval(), false);
                            setTextTipNormal(mData.getVerificationSucceedPrompt());
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mListener != null){
                                        ResultVerifyVO resultVerifyVO = new ResultVerifyVO();
                                        resultVerifyVO.setIsFinished(true);
                                        if (isHasCreate){
                                            mListener.onStartCreate();
                                        }else{
                                            mListener.onVerifyResult(resultVerifyVO);
                                        }
                                        mListener.onEventOccur(JsConst.EVENT_VERIFY_SUCCESS);
                                        mListener.closeLayout();
                                    }
                                }
                            }, mData.getSuccessRemainInterval());
                        }

                        @Override
                        public void checkedFail() {
                            if (mListener != null){
                                mListener.onEventOccur(JsConst.EVENT_VERIFY_ERROR);
                            }
                            if (mLeftCount > 1){
                                mLeftCount--;
                                mGestureContentView.clearDrawlineState(mData.getMinimumCodeLength(), true);
                                String tips = String.format(mData.getVerificationErrorPrompt(),
                                        mLeftCount);
                                setTextTipError(tips);
                                // 左右移动动画
                                Animation shakeAnimation = AnimationUtils.loadAnimation(
                                        GestureVerifyFragment.this.getActivity(),
                                        EUExUtil.getResAnimID("plugin_uexgestureunlock_shake"));
                                mTextTip.startAnimation(shakeAnimation);
                            }else {
                                if (mListener != null){
                                    ResultFailedVO result = new ResultFailedVO();
                                    result.setIsFinished(false);
                                    result.setErrorCode(JsConst.ERROR_CODE_TOO_MANY_TRY);
                                    result.setErrorString(EUExUtil
                                            .getString("plugin_uexGestureUnlock_errorCodeTooManyTry"));
                                    mListener.onVerifyResult(result);
                                    mListener.closeLayout();
                                }
                            }
                        }
                    },mDrawArrowListener, mData);
            // 设置手势解锁显示到哪个布局里面
            mGestureContentView.setParentView(mGestureContainer);
            setUpListeners();
            if (mListener != null){
                mListener.onEventOccur(JsConst.EVENT_PLUGIN_INIT);
                mListener.onEventOccur(JsConst.EVENT_START_VERIFY);
            }
        } catch (Exception e) {
            if (mListener != null){
                ResultFailedVO result = new ResultFailedVO();
                result.setIsFinished(false);
                result.setErrorCode(JsConst.ERROR_CODE_UNKNOWN);
                result.setErrorString(EUExUtil
                        .getString("plugin_uexGestureUnlock_errorCodeUnknown"));
                mListener.onVerifyResult(result);
            }
        }
        return view;
    }

    private void setTextTipError(String tips) {
        mTextTip.setTextColor(mData.getErrorThemeColor());
        mTextTip.setText(tips);
    }

    private void setTextTipNormal(String tips) {
        mTextTip.setTextColor(mData.getNormalThemeColor());
        mTextTip.setText(tips);
    }

    private void setUpData() {
        mLeftCount = mData.getMaximumAllowTrialTimes();
        if (!TextUtils.isEmpty(mData.getBackgroundImage())){
            mBg.setBackgroundDrawable(new BitmapDrawable(
                    BUtility.getLocalImg(this.getActivity(),
                            mData.getBackgroundImage())));
        }else{
            mBg.setBackgroundColor(mData.getBackgroundColor());
        }
        if (!TextUtils.isEmpty(mData.getIconImage())){
            mImgUserLogo.setVisibility(View.VISIBLE);
            mImgUserLogo.setImageBitmap(BUtility.getLocalImg(this.getActivity(),
                    mData.getIconImage()));
        }else{
            mImgUserLogo.setVisibility(View.INVISIBLE);
        }
        mTextForget.setTextColor(mData.getNormalThemeColor());
        mTextForget.setText(mData.getCancelVerificationButtonTitle());
        setTextTipNormal(mData.getVerificationBeginPrompt());
    }

    private void setUpListeners() {
        mTextForget.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == EUExUtil
                .getResIdID("plugin_uexGestureUnlock_text_forget_gesture")){
            if (mListener != null){
                mListener.onCancel();
            }
        }
    }

    public void setGestureVerifyListener(GestureVerifyListener listener) {
        this.mListener = listener;
    }

    public void setGestureCodeData(String code) {
        this.mGestureCode = code;
    }

    public void setData(ConfigGestureVO data) {
        if (data == null){
            mData = new ConfigGestureVO();
        }else{
            this.mData = data;
        }
        super.setData(mData);
    }

    public void setIsHasCreate(boolean flag) {
        this.isHasCreate = flag;
    }
}
