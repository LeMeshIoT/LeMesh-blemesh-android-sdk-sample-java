package com.example.leshidemo1.ui.utils;

import android.graphics.Rect;
import android.view.TouchDelegate;
import android.view.View;

/**
 * 扩大View点击事件
 */
public class ExpandViewClick {

    private volatile static ExpandViewClick expandViewClick;
    /**
     * 获取实例
     *
     * @return MyApplication
     */
    public static ExpandViewClick getInstance() {
        if(expandViewClick == null){
            synchronized (ExpandViewClick.class){
                if(expandViewClick == null){
                    expandViewClick = new ExpandViewClick();
                }
            }
        }
        return expandViewClick;
    }

    /**
     * 扩展点击区域的范围
     *
     * @param view       需要扩展的元素，此元素必需要有父级元素
     * @param expendSize 需要扩展的尺寸（以sp为单位的）
     */
    public static void expendTouchArea(final View view, final int expendSize) {
        if (view != null) {
            final View parentView = (View) view.getParent();

            parentView.post(new Runnable() {
                @Override
                public void run() {
                    Rect rect = new Rect();
                    view.getHitRect(rect); //如果太早执行本函数，会获取rect失败，因为此时UI界面尚未开始绘制，无法获得正确的坐标
                    rect.left -= expendSize;
                    rect.top -= expendSize;
                    rect.right += expendSize;
                    rect.bottom += expendSize;
                    parentView.setTouchDelegate(new TouchDelegate(rect, view));
                }
            });
        }
    }
}
