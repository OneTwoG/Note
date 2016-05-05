package com.github.hmallet.realparallaxandroid;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by hugo
 * on 08/12/2015.
 */
public class RealViewPager extends ViewPager {

    private RealHorizontalScrollView mRealHorizontalScrollView;
    private float mParallaxVelocity;
    private int mRealHorizontalScrollViewWidth;

    //定义两个RealViewPager的构造函数
    //实例化时使用
    public RealViewPager(Context context) {
        super(context);
    }

    //包含自定义控件参数的构造函数
    public RealViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initRealViewPagerAttributes(context, attrs);
    }

    public void configure(RealHorizontalScrollView realHorizontalScrollView) {
        this.mRealHorizontalScrollView = realHorizontalScrollView;

        mRealHorizontalScrollViewWidth = this.mRealHorizontalScrollView.getWidth();

        overrideScrollListener();
    }

    public void configureWithMyListener(
            RealHorizontalScrollView realHorizontalScrollView) {
        this.mRealHorizontalScrollView = realHorizontalScrollView;
        this.mRealHorizontalScrollViewWidth =
                this.mRealHorizontalScrollView.getWidth();
    }

    public void setRealHorizontalScrollViewPosition(int scrollX, int position) {
        this.mRealHorizontalScrollView.scrollTo(
                Math.round((scrollX * mParallaxVelocity) +
                        (position * mParallaxVelocity *
                                this.mRealHorizontalScrollView.getWidth())), 0);
    }

    /**
     *  Override scroll listener
     *
     *  @param scrollX scroll X
     *  @param position current item position
     */
    public void manageScrollWithMyListeners(int scrollX, int position) {
        setRealHorizontalScrollViewPosition(scrollX, position);
    }

    private void overrideScrollListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //版本号和当前开发代码代号
            this.setOnScrollChangeListener(new OnScrollChangeListener() {
                @Override
                public void onScrollChange(
                        View v, int scrollX, int scrollY,
                        int oldScrollX, int oldScrollY) {
                    setRealHorizontalScrollViewPosition(scrollX, 0);
                }
            });
        } else {
            this.setOnPageChangeListener(new OnPageChangeListener() {
                @Override
                //在滑动过程中不断被调用，positionOffset表示滑动比例，趋于1时突然变为0
                public void onPageScrolled(
                        int position, float positionOffset,
                        int positionOffsetPixels) {
                    setRealHorizontalScrollViewPosition(
                            positionOffsetPixels, position);
                }

                @Override
                public void onPageSelected(int position) {
                    //表示哪个页面被选中，pisition指被选中的页面
                    // nothing to do
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    // nothing to do
                }
            });
        }
    }

    private void initRealViewPagerAttributes(Context context, AttributeSet attrs) {

        TypedArray attributesArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RealViewPager,
                0, 0);  //TypedArray是一个数组容器  保存自定义控件的属性并进行初始化
        //后两个参数是设置风格的参数，初始化为0 表示默认风格

        try {
            mParallaxVelocity = attributesArray.getFloat(
                    R.styleable.RealViewPager_parallaxVelocity, 0.3f);
        } finally {
            //官方解释一般是说回收TypedArray，以便后面重用
            //当recycle被调用后，就说明这个对象现在可以被重用了
            //准确的说，typearray内部持有部分数组，它们缓存在Resource类中的静态字段中
            //这样就不用每次使用前都分配内存
            attributesArray.recycle();
        }
    }
}
