package cn.test.demo.ui.activity;

import android.widget.Toast;

import cn.test.demo.R;
import cn.test.demo.base.BaseMVPActivity;
import cn.test.demo.contract.WelcomeContract;
import cn.test.demo.databinding.ActivityWelcomeBinding;
import cn.test.demo.presenter.WelcomePresenter;

/**
 * 欢迎页
 */
public class WelcomeActivity extends BaseMVPActivity<WelcomePresenter, ActivityWelcomeBinding> implements WelcomeContract.View {

    @Override
    protected int setLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initView() {
        viewBinding.setTitle("欢迎页");
        viewBinding.button1.setOnClickListener(v -> {
            mPresenter.loadTestData("1");
        });
        viewBinding.button2.setOnClickListener(v -> {
            hideLoading();
        });
    }

    @Override
    protected WelcomePresenter getPresenter() {
        return new WelcomePresenter();
    }

    @Override
    public void loadTestDataSuccess(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }
}