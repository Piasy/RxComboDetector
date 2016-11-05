package com.github.piasy.rxcombodetector.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.github.piasy.rxcombodetector.RxComboDetector;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.mBtnCombo)
    Button mBtnCombo;
    @Bind(R.id.mTextView)
    TextView mTextView;
    @Bind(R.id.mYOLOComboView)
    YOLOComboView mYOLOComboView;
    @Bind(R.id.mScrollView)
    ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        new RxComboDetector.Builder()
                .detectOn(mBtnCombo)
                .start()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer combo) throws Exception {
                        mTextView.setText(mTextView.getText() + "Combo x " + combo + "\n");
                        mScrollView.fullScroll(View.FOCUS_DOWN);
                        mYOLOComboView.combo(combo);
                    }
                });
    }
}
