package cn.test.demo.base;import android.content.BroadcastReceiver;import android.content.Context;import android.content.Intent;import android.content.IntentFilter;import android.content.res.Configuration;import android.net.ConnectivityManager;import android.net.NetworkInfo;import android.os.Bundle;import android.util.TypedValue;import android.view.Gravity;import android.view.KeyEvent;import android.view.MotionEvent;import android.view.View;import android.view.ViewGroup;import android.view.animation.AnimationUtils;import android.view.inputmethod.InputMethodManager;import android.widget.FrameLayout;import android.widget.TextView;import androidx.annotation.ColorRes;import androidx.annotation.LayoutRes;import androidx.annotation.NonNull;import androidx.databinding.DataBindingUtil;import androidx.databinding.ViewDataBinding;import com.gyf.immersionbar.ImmersionBar;import com.trello.rxlifecycle2.LifecycleTransformer;import com.trello.rxlifecycle2.android.ActivityEvent;import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;import java.util.List;import cn.test.demo.R;import cn.test.demo.app.ActivityTaskManager;import cn.test.demo.base.mvp.BasePresenter;import cn.test.demo.base.mvp.IView;import cn.test.demo.util.MyPermissionUtil;import cn.test.demo.util.ScreenUtil;import cn.test.demo.widget.GlobalHttpLoadingView;import pub.devrel.easypermissions.EasyPermissions;/** * Created by Sam on  2021-12-06  11:28 * Describe: */public abstract class BaseMVPActivity<P extends BasePresenter, T extends ViewDataBinding> extends RxAppCompatActivity implements EasyPermissions.PermissionCallbacks, IView {    private TextView networkTipsTextView;    private NetworkInfo networkInfo;    // 广播    private BroadcastReceiver mItemViewListClickReceiver;    private ConnectivityManager mConnectivityManager;    private boolean mReceiverTag = false;   //广播接受者标识 避免重复注册，    protected View statusBarView;    protected ImmersionBar immersionBar;    protected T viewBinding;    protected P mPresenter;    protected GlobalHttpLoadingView globalHttpLoadingView;    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        try {            int layoutResId = setLayoutId();            if (layoutResId != 0) {                viewBinding = DataBindingUtil.setContentView(this, setLayoutId());                statusBarView = viewBinding.getRoot().findViewById(R.id.status_bar_view);                mPresenter = getPresenter();                if (mPresenter != null) {                    mPresenter.attachView(this);                }                // 创建activity时加入管理栈中                ActivityTaskManager.getInstance().putActivity(this);                registerBroadcastReceiver();                initImmersionBar();                initView();            }        } catch (Exception e) {            e.printStackTrace();        }    }    @Override    protected void onResume() {        super.onResume();        checkNetworkStatus();    }    @Override    public void finish() {        // 销毁activity时从管理栈中中去除        ActivityTaskManager.getInstance().removeActivity(this);        super.finish();    }    /**     * 监听返回键     */    @Override    public boolean onKeyDown(int keyCode, KeyEvent event) {        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {            goBack(null);            return true;        }        return super.onKeyDown(keyCode, event);    }    /**     * 统一返回     *     * @param v     */    public void goBack(View v) {        finish();    }    /**     * 点击空白位置 隐藏软键盘     */    public boolean onTouchEvent(MotionEvent event) {        if (null != this.getCurrentFocus()) {            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);        }        return super.onTouchEvent(event);    }    //限制系统修改字体    @Override    protected void attachBaseContext(Context newBase) {        super.attachBaseContext(newBase);        String brand = android.os.Build.BRAND;        //google手机不支持        if (brand != null && (brand.toLowerCase().equals("google") || brand.toLowerCase().equals("nexus"))) {            return;        }        final Configuration override = new Configuration(newBase.getResources().getConfiguration());        override.fontScale = 1.0f;        applyOverrideConfiguration(override);    }    private void registerBroadcastReceiver() {        if (!mReceiverTag) {            IntentFilter intentFilter = new IntentFilter();            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);            mItemViewListClickReceiver = new BroadcastReceiver() {                @Override                public void onReceive(Context context, Intent intent) {                    if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {                        checkNetworkStatus();                    }                }            };            registerReceiver(mItemViewListClickReceiver, intentFilter);            mReceiverTag = true;        }    }    @Override    protected void onDestroy() {        super.onDestroy();        if (mItemViewListClickReceiver != null) {            //注销广播            unregisterReceiver(mItemViewListClickReceiver);        }        dismissProgressDialog();        globalHttpLoadingView = null;    }    private void checkNetworkStatus() {        if (mConnectivityManager == null) {            mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);        }        networkInfo = mConnectivityManager.getActiveNetworkInfo();        if (networkInfo != null && networkInfo.isAvailable()) {            // 网络连接            hideNetworkTips();        } else {            // 网络断开            showNetworkTips();        }    }    //显示 断网提示    private void showNetworkTips() {        if (networkTipsTextView != null && networkTipsTextView.getVisibility() == View.VISIBLE) {            return;        }        View decorView = getWindow().getDecorView();        FrameLayout contentView = (FrameLayout) decorView.findViewById(android.R.id.content);        networkTipsTextView = getNetworkStatusView();        contentView.addView(networkTipsTextView);        networkTipsTextView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.network_tips_show));        networkTipsTextView.setVisibility(View.VISIBLE);    }    //隐藏断网提示    private void hideNetworkTips() {        if (networkTipsTextView == null || networkTipsTextView.getVisibility() == View.GONE) {            return;        }        networkTipsTextView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.network_tips_hide));        networkTipsTextView.setVisibility(View.GONE);    }    //创建一个断网提示的TextView    private TextView getNetworkStatusView() {        TextView textView = new TextView(this);        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(this, 44));        marginLayoutParams.setMargins(0, ScreenUtil.dip2px(this, 35), 0, 0);        textView.setLayoutParams(marginLayoutParams);        textView.setBackgroundColor(getColor(R.color.network_tips_bg));        textView.setTextColor(getColor(R.color.white));        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_size_16));        textView.setText(getResources().getString(R.string.network_tips_hint));        textView.setGravity(Gravity.CENTER);        textView.setVisibility(View.GONE);        return textView;    }    /**     * 初始化沉浸式     * Init immersion bar.     */    protected void initImmersionBar() {        //设置共同沉浸式样式        immersionBar = ImmersionBar.with(this);        immersionBar.navigationBarColor(R.color.white).init();        fitsLayoutOverlap();        //默认黑色 状态栏文字        setImmersionStatusBarDark(true);    }    private void fitsLayoutOverlap() {        if (statusBarView != null) {            ImmersionBar.setStatusBarView(this, statusBarView);        }    }    //状态栏    public void fitsLayoutOverlap(@ColorRes int ColorRes) {        if (statusBarView != null) {            statusBarView.setBackgroundColor(getColor(ColorRes));        }    }    public void setImmersionStatusBarDark(boolean isDark) {        if (immersionBar != null) {            immersionBar.statusBarDarkFont(isDark)  //状态栏字体是深色，不写默认为亮色                    .navigationBarDarkIcon(isDark).init(); //导航栏图标是深色，不写默认为亮色        }    }    @Override    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {        super.onRequestPermissionsResult(requestCode, permissions, grantResults);        //将请求结果传递EasyPermission库处理   这一步需要手动处理        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);    }    /**     * EasyPermissions权限请求成功     *     * @param requestCode     * @param perms     */    @Override    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {    }    /**     * EasyPermissions权限请求失败     *     * @param requestCode     * @param perms     */    @Override    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {        switch (requestCode) {            case MyPermissionUtil.ACCESS_COARSE_LOCATION:                MyPermissionUtil.onCallbackPermissionsDenied(this, perms, () -> {                }, "定位");                break;            case MyPermissionUtil.RQ_CAMERA_PERMISSIONS:                MyPermissionUtil.onCallbackPermissionsDenied(this, perms, () -> {                }, "相机");                break;            case MyPermissionUtil.READ_WRITE_EXTERNAL_STORAGE:                MyPermissionUtil.onCallbackPermissionsDenied(this, perms, () -> {                }, "相册文件");                break;            case MyPermissionUtil.CALL_PHONE:                MyPermissionUtil.onCallbackPermissionsDenied(this, perms, () -> {                }, "电话");                break;        }    }    /**     * 子类设置布局Id     *     * @return the layout id     */    @LayoutRes    protected abstract int setLayoutId();    /**     * 初始化视图     */    protected abstract void initView();    /**     * @return     */    protected abstract P getPresenter();    public T getViewBinding() {        return viewBinding;    }    @Override    public void onError(Throwable throwable) {    }    @Override    public void showLoading() {        showProgressDialog();    }    @Override    public void hideLoading() {        dismissProgressDialog();    }    private void showProgressDialog() {        if (globalHttpLoadingView == null) {            globalHttpLoadingView = new GlobalHttpLoadingView(this);        }        globalHttpLoadingView.showHttpLoading();    }    private void dismissProgressDialog() {        if (globalHttpLoadingView != null) {            globalHttpLoadingView.hideHttpLoading();        }    }    @Override    public <T> LifecycleTransformer<T> bindToLife() {        return bindUntilEvent(ActivityEvent.DESTROY);    }}