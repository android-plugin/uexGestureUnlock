package org.zywx.wbpalmstar.plugin.uexgestureunlock.vo;

import org.zywx.wbpalmstar.base.BUtility;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;

import java.io.Serializable;

public class ConfigGestureVO implements Serializable{
    private static final long serialVersionUID = 2799945813335599371L;

    private int minimumCodeLength = 4;
    private int maximumAllowTrialTimes = 5;
    private long errorRemainInterval = 1000;
    private long successRemainInterval = 200;
    private String backgroundColor = "#F1F1F1";
    private String normalThemeColor = "#002849";
    private String selectedThemeColor = "#22B2F6";
    private String errorThemeColor = "#FE525C";
    private String creationBeginPrompt = EUExUtil.getString("plugin_uexGestureUnlock_creationBeginPrompt");
    private String codeLengthErrorPrompt = EUExUtil.getString("plugin_uexGestureUnlock_codeLengthErrorPrompt");
    private String codeCheckPrompt = EUExUtil.getString("plugin_uexGestureUnlock_codeCheckPrompt");
    private String checkErrorPrompt = EUExUtil.getString("plugin_uexGestureUnlock_checkErrorPrompt");
    private String creationSucceedPrompt = EUExUtil.getString("plugin_uexGestureUnlock_creationSucceedPrompt");
    private String verificationBeginPrompt = EUExUtil.getString("plugin_uexGestureUnlock_verificationBeginPrompt");
    private String verificationErrorPrompt = EUExUtil.getString("plugin_uexGestureUnlock_verificationErrorPrompt");
    private String verificationSucceedPrompt = EUExUtil.getString("plugin_uexGestureUnlock_verificationSucceedPrompt");
    private String cancelVerificationButtonTitle = EUExUtil.getString("plugin_uexGestureUnlock_cancelVerificationButtonTitle");
    private String cancelCreationButtonTitle = EUExUtil.getString("plugin_uexGestureUnlock_cancelCreationButtonTitle");
    private String restartCreationButtonTitle = EUExUtil.getString("plugin_uexGestureUnlock_restartCreationButtonTitle");
    private String backgroundImage;
    private String iconImage;

    public int getMinimumCodeLength() {
        return minimumCodeLength;
    }

    public void setMinimumCodeLength(int minimumCodeLength) {
        this.minimumCodeLength = minimumCodeLength;
    }

    public int getMaximumAllowTrialTimes() {
        return maximumAllowTrialTimes;
    }

    public void setMaximumAllowTrialTimes(int maximumAllowTrialTimes) {
        this.maximumAllowTrialTimes = maximumAllowTrialTimes;
    }

    public long getErrorRemainInterval() {
        return errorRemainInterval;
    }

    public void setErrorRemainInterval(long errorRemainInterval) {
        this.errorRemainInterval = errorRemainInterval;
    }

    public long getSuccessRemainInterval() {
        return successRemainInterval;
    }

    public void setSuccessRemainInterval(long successRemainInterval) {
        this.successRemainInterval = successRemainInterval;
    }

    public int getBackgroundColor() {
        return BUtility.parseColor(backgroundColor);
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getNormalThemeColor() {
        return BUtility.parseColor(normalThemeColor);
    }

    public void setNormalThemeColor(String normalThemeColor) {
        this.normalThemeColor = normalThemeColor;
    }

    public int getSelectedThemeColor() {
        return BUtility.parseColor(selectedThemeColor);
    }

    public void setSelectedThemeColor(String selectedThemeColor) {
        this.selectedThemeColor = selectedThemeColor;
    }

    public int getErrorThemeColor() {
        return BUtility.parseColor(errorThemeColor);
    }

    public void setErrorThemeColor(String errorThemeColor) {
        this.errorThemeColor = errorThemeColor;
    }

    public String getCreationBeginPrompt() {
        return creationBeginPrompt;
    }

    public void setCreationBeginPrompt(String creationBeginPrompt) {
        this.creationBeginPrompt = creationBeginPrompt;
    }

    public String getCodeLengthErrorPrompt() {
        return codeLengthErrorPrompt;
    }

    public void setCodeLengthErrorPrompt(String codeLengthErrorPrompt) {
        this.codeLengthErrorPrompt = codeLengthErrorPrompt;
    }

    public String getCodeCheckPrompt() {
        return codeCheckPrompt;
    }

    public void setCodeCheckPrompt(String codeCheckPrompt) {
        this.codeCheckPrompt = codeCheckPrompt;
    }

    public String getCheckErrorPrompt() {
        return checkErrorPrompt;
    }

    public void setCheckErrorPrompt(String checkErrorPrompt) {
        this.checkErrorPrompt = checkErrorPrompt;
    }

    public String getCreationSucceedPrompt() {
        return creationSucceedPrompt;
    }

    public void setCreationSucceedPrompt(String creationSucceedPrompt) {
        this.creationSucceedPrompt = creationSucceedPrompt;
    }

    public String getVerificationBeginPrompt() {
        return verificationBeginPrompt;
    }

    public void setVerificationBeginPrompt(String verificationBeginPrompt) {
        this.verificationBeginPrompt = verificationBeginPrompt;
    }

    public String getVerificationErrorPrompt() {
        return verificationErrorPrompt;
    }

    public void setVerificationErrorPrompt(String verificationErrorPrompt) {
        this.verificationErrorPrompt = verificationErrorPrompt;
    }

    public String getVerificationSucceedPrompt() {
        return verificationSucceedPrompt;
    }

    public void setVerificationSucceedPrompt(String verificationSucceedPrompt) {
        this.verificationSucceedPrompt = verificationSucceedPrompt;
    }

    public String getCancelVerificationButtonTitle() {
        return cancelVerificationButtonTitle;
    }

    public void setCancelVerificationButtonTitle(String cancelVerificationButtonTitle) {
        this.cancelVerificationButtonTitle = cancelVerificationButtonTitle;
    }

    public String getCancelCreationButtonTitle() {
        return cancelCreationButtonTitle;
    }

    public void setCancelCreationButtonTitle(String cancelCreationButtonTitle) {
        this.cancelCreationButtonTitle = cancelCreationButtonTitle;
    }

    public String getRestartCreationButtonTitle() {
        return restartCreationButtonTitle;
    }

    public void setRestartCreationButtonTitle(String restartCreationButtonTitle) {
        this.restartCreationButtonTitle = restartCreationButtonTitle;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getIconImage() {
        return iconImage;
    }

    public void setIconImage(String iconImage) {
        this.iconImage = iconImage;
    }
}
