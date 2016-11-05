package com.github.piasy.rxcombodetector.example;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import java.util.concurrent.TimeUnit;

public class YOLOComboView extends FrameLayout {

    private final ComboSpringListener mSpringListener;
    private final Spring mComboSpring;
    private Disposable mComboHideDisposable;

    private final ImageView mYOLOLogo;
    private final TextView mComboText;

    public YOLOComboView(Context context) {
        this(context, null);
    }

    public YOLOComboView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YOLOComboView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        BaseSpringSystem springSystem = SpringSystem.create();
        mComboSpring = springSystem.createSpring();
        mComboSpring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(600, 9));

        mYOLOLogo = new ImageView(context);
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mYOLOLogo.setLayoutParams(params);
        mYOLOLogo.setImageResource(R.drawable.ic_yolo);
        addView(mYOLOLogo);

        mComboText = new TextView(context);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT);
        params.rightMargin = 30;
        mComboText.setLayoutParams(params);
        mComboText.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
        mComboText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        addView(mComboText);

        mSpringListener = new ComboSpringListener(mComboText);
        setAlpha(0);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mComboSpring.addListener(mSpringListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mComboSpring.removeAllListeners();
        if (mComboHideDisposable != null && !mComboHideDisposable.isDisposed()) {
            mComboHideDisposable.dispose();
            mComboHideDisposable = null;
        }
    }

    public void combo(int combo) {
        mComboText.setText("X " + combo);
        mComboSpring.setCurrentValue(0.01);
        mComboSpring.setEndValue(1);
        show();

        if (mComboHideDisposable != null && !mComboHideDisposable.isDisposed()) {
            mComboHideDisposable.dispose();
        }
        mComboHideDisposable = Flowable.timer(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        hide();
                    }
                }, RxUtils.IgnoreErrorProcessor);
    }

    private void show() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            animate().alpha(1).setDuration(100).withLayer().start();
        } else {
            animate().alpha(1).setDuration(100).start();
        }
    }

    private void hide() {
        animate().alpha(0).setDuration(100).start();
    }

    private static class ComboSpringListener extends SimpleSpringListener {

        public static final double TO_HIGH = 1.5;

        private final View mView;

        private ComboSpringListener(View view) {
            mView = view;
        }

        @Override
        public void onSpringUpdate(Spring spring) {
            if (mView != null) {
                float mappedValue =
                        (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1,
                                1, TO_HIGH);
                mView.setScaleX(mappedValue);
                mView.setScaleY(mappedValue);
            }
        }
    }
}
