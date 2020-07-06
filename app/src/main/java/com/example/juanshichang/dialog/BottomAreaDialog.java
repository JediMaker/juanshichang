package com.example.juanshichang.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.juanshichang.R;

import java.util.List;

import chihane.jdaddressselector.AddressProvider;
import chihane.jdaddressselector.AddressSelector;
import chihane.jdaddressselector.OnAddressSelectedListener;
import chihane.jdaddressselector.model.City;
import chihane.jdaddressselector.model.County;
import chihane.jdaddressselector.model.Province;
import chihane.jdaddressselector.model.Street;
import mlxy.utils.Dev;

public class BottomAreaDialog extends Dialog {
    private AddressSelector selector;

    public BottomAreaDialog(Context context ) {
        super(context, R.style.bottom_dialog);
        init(context);
    }
    public BottomAreaDialog(Context context, View view) {
        super(context, R.style.bottom_dialog);
        init(context,view);
    }

    public BottomAreaDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    public BottomAreaDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context) {
        selector = new AddressSelector(context);
        setContentView(selector.getView());
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = Dev.dp2px(context, 256);
        window.setAttributes(params);
        window.setGravity(Gravity.BOTTOM);
    }
    private void init(Context context,View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeAllViews();
        }
        setContentView(view);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = Dev.dp2px(context, 256);
        window.setAttributes(params);
        window.setGravity(Gravity.BOTTOM);
    }

    public void setOnAddressSelectedListener(OnAddressSelectedListener listener) {
        this.selector.setOnAddressSelectedListener(listener);
    }

    public static BottomAreaDialog show(Context context) {
        return show(context, null);
    }

    public static BottomAreaDialog show(Context context, OnAddressSelectedListener listener) {
        BottomAreaDialog dialog = new BottomAreaDialog(context, R.style.bottom_dialog);
        dialog.selector.setOnAddressSelectedListener(listener);
        dialog.show();

        return dialog;
    }
}
