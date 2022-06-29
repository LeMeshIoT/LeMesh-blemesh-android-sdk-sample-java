package com.example.leshidemo1.ui.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.leshidemo1.R;


/**
 * 输入框 提示框
 */

public class InputPopWindow {
    private View rootView;
    private PopupWindow popupWindow;
    private EditText etName;//输入框
    private TextView tvTitle;//标题
    private TextView cancel, confirm;//取消-
    private String title;
    private String rightTitle;//右下角确认按钮的文字
    private Context context;
    private boolean isInput = true;//是否带有输入框
    public InputPopWindow(Context context, String tile, String rightTitle,boolean isInput) {
        this.title = tile;
        this.rightTitle = rightTitle;
        this.context = context;
        this.isInput = isInput;
        initView();
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setInput(boolean input) {
        isInput = input;
    }

    public void inputTitle(String title) {
        this.title = title;
        tvTitle.setText(title);
    }

    public void inputText(String data){
        etName.setText(data);
    }

    private void initView() {
        rootView = LayoutInflater.from(context).inflate(R.layout.input_popupwindow, null);
        cancel = rootView.findViewById(R.id.cancel);
        confirm = rootView.findViewById(R.id.confirm);
        etName = rootView.findViewById(R.id.etName);
        tvTitle = rootView.findViewById(R.id.tvTitle);
        etName.setVisibility(isInput ? View.VISIBLE : View.GONE);
        tvTitle.setText(this.title);
        confirm.setText(TextUtils.isEmpty(this.rightTitle) ? "保存" : this.rightTitle);
        setListener();
    }

    public void showPopWindow() {
        tvTitle.setText(this.title);
        etName.setVisibility(isInput ? View.VISIBLE : View.GONE);
        popupWindow = new PopupWindow(rootView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        //popupWindow.setAnimationStyle(R.style.popwindow_anim_style);
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);

    }

    public void closePopWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            etName.setText("");
            popupWindow.dismiss();
            popupWindow = null;
        } else {
            return;
        }
    }

    private OnPopWindowClick setPopWindowClick;

    public void setPopWindowClick(OnPopWindowClick setPopWindowClick) {
        this.setPopWindowClick = setPopWindowClick;
    }

    public interface OnPopWindowClick {
        void onClickPopWindow(String data);
    }

    private void setListener() {
        cancel.setOnClickListener(clickListener);
        confirm.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.cancel:
                    //取消
                    closePopWindow();
                    break;
                case R.id.confirm:
                    //确认
                    setPopWindowClick.onClickPopWindow(etName.getText().toString());
                    break;
            }
        }
    };

}
