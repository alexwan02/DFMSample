package org.alexwan.dfmsample;

/**
 * DanmakuData
 * Created by alexwan on 16/9/23.
 */
public class DanmakuData {

    private String content;
    private double time;
    private int type;
    private float fontSize;
    private int fontColor;
    private int userId;
    private String userName;
    private String avatar;
    private int danmakuId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public int getFontColor() {
        return fontColor;
    }

    public void setFontColor(int fontColor) {
        this.fontColor = fontColor;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getDanmakuId() {
        return danmakuId;
    }

    public void setDanmakuId(int danmakuId) {
        this.danmakuId = danmakuId;
    }
}
