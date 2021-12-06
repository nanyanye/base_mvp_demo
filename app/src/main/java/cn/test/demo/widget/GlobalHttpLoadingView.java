package cn.test.demo.widget;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import cn.test.manage.R;

public class GlobalHttpLoadingView {

    private Activity activity;

    public GlobalHttpLoadingView(Activity activity){
       this.activity = activity;
    }

    /**
     *********** 处理网络加载loading ***********
     */
    private View llLoading;

    /**
     * 显示Loading
     */
    public void showHttpLoading() {
        addLoadingView();
        if (llLoading != null) {
            llLoading.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏Loading
     */
    public void hideHttpLoading() {
        if (llLoading != null) {
            llLoading.setVisibility(View.GONE);
        }
    }

    /**
     * 添加Loading布局
     */
    private void addLoadingView() {
        if (llLoading == null) {
            final View loadingView = View.inflate(activity, R.layout.global_http_loading, null);
            final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            activity.addContentView(loadingView, params);
            llLoading = activity.findViewById(R.id.ll_global_center_loading);
        }
    }
}
