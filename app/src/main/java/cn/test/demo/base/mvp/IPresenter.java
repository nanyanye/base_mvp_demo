package cn.test.demo.base.mvp;

/**
 * Created by Sam on  2021-12-06  15:28
 * Describe:
 */

public interface IPresenter {

    /**
     * 做一些初始化操作
     */
    void onStart();

    /**
     * 在框架中会默认调用
     */
    void onDestroy();

}
