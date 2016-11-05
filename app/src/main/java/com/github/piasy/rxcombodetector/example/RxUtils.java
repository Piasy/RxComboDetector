package com.github.piasy.rxcombodetector.example;

import android.util.Log;
import io.reactivex.functions.Consumer;

/**
 * Created by Piasy{github.com/Piasy} on 16/1/12.
 */
public class RxUtils {

    public static Consumer<Throwable> IgnoreErrorProcessor = new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) {
            Log.e("IgnoreErrorProcessor", "Rx onError: ", throwable);
        }
    };
}
