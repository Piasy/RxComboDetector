/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Piasy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.piasy.rxcombodetector;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.view.View;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Timestamped;

import static com.github.piasy.rxcombodetector.Preconditions.checkArgument;
import static com.github.piasy.rxcombodetector.Preconditions.checkNotNull;

/**
 * Created by Piasy{github.com/Piasy} on 16/1/18.
 *
 * Android view click combo detector with Rx.
 */
public final class RxComboDetector {

    private static final long DEFAULT_MAX_INTERVAL_MILLIS = 300;
    private static final int DEFAULT_MIN_COMBO_TIMES_CARED = 2;

    private final View mTarget;
    private final long mMaxIntervalMillis;
    private final int mMinComboTimesCared;

    private RxComboDetector(View target, long maxIntervalMillis, int minComboTimesCared) {
        checkNotNull(target, "target must be non-null");
        checkArgument(maxIntervalMillis >= 1, "maxIntervalMillis must >= 1");
        checkArgument(minComboTimesCared >= DEFAULT_MIN_COMBO_TIMES_CARED,
                "maxIntervalMillis must >= " + DEFAULT_MIN_COMBO_TIMES_CARED);
        mTarget = target;
        mMaxIntervalMillis = maxIntervalMillis;
        mMinComboTimesCared = minComboTimesCared;
    }

    /**
     * start combo detect
     *
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Warning:</em> The created observable uses {@link View#setOnClickListener} to observe
     * clicks. Only one observable can be used for a view at a time.
     *
     * @return {@link Observable} that emits combo times.
     */
    public Observable<Integer> start() {
        return detect(Observable.create(new ViewClickOnSubscribe(mTarget)), mMaxIntervalMillis,
                mMinComboTimesCared);
    }

    static Observable<Integer> detect(Observable<Void> clicks, final long maxIntervalMillis,
            final int minComboTimesCared) {
        return clicks.map(new Func1<Void, Integer>() {
                @Override
                public Integer call(Void aVoid) {
                    return 1;
                }
            }).timestamp()
            .scan(new Func2<Timestamped<Integer>, Timestamped<Integer>, Timestamped<Integer>>() {
                @Override
                public Timestamped<Integer> call(Timestamped<Integer> lastOne,
                        Timestamped<Integer> thisOne) {
                    if (thisOne.getTimestampMillis() - lastOne.getTimestampMillis() <=
                            maxIntervalMillis) {
                        return new Timestamped<>(thisOne.getTimestampMillis(),
                                lastOne.getValue() + 1);
                    } else {
                        return new Timestamped<>(thisOne.getTimestampMillis(), 1);
                    }
                }
            }).map(new Func1<Timestamped<Integer>, Integer>() {
                @Override
                public Integer call(Timestamped<Integer> timestamped) {
                    return timestamped.getValue();
                }
            }).filter(new Func1<Integer, Boolean>() {
                @Override
                public Boolean call(Integer combo) {
                    return combo >= minComboTimesCared;
                }
            });
    }

    /**
     * Builder class to build {@link RxComboDetector}
     */
    public static final class Builder {
        private long mMaxIntervalMillis;
        private int mMinComboTimesCared;

        /**
         * default RxComboDetector builder, build with {@link #DEFAULT_MAX_INTERVAL_MILLIS} and
         * {@link #DEFAULT_MIN_COMBO_TIMES_CARED}
         */
        public Builder() {
            this(DEFAULT_MAX_INTERVAL_MILLIS, DEFAULT_MIN_COMBO_TIMES_CARED);
        }

        /**
         * RxComboDetector builder
         *
         * @param maxIntervalMillis combo detect max interval
         * @param minComboTimesCared combo detect min cared times
         */
        public Builder(@IntRange(from = 1) long maxIntervalMillis,
                @IntRange(from = DEFAULT_MIN_COMBO_TIMES_CARED) int minComboTimesCared) {
            mMaxIntervalMillis = maxIntervalMillis;
            mMinComboTimesCared = minComboTimesCared;
        }

        /**
         * set {@link #mMaxIntervalMillis}
         *
         * @param maxIntervalMillis combo detect max interval
         * @return {@link Builder} instance after {@link #mMaxIntervalMillis} set
         */
        public Builder maxIntervalMillis(@IntRange(from = 1) long maxIntervalMillis) {
            mMaxIntervalMillis = maxIntervalMillis;
            return this;
        }

        /**
         * set {@link #mMinComboTimesCared}
         * @param minComboTimesCared combo detect min cared times
         * @return {@link Builder} instance after {@link #mMinComboTimesCared} set
         */
        public Builder minComboTimesCared(
                @IntRange(from = DEFAULT_MIN_COMBO_TIMES_CARED) int minComboTimesCared) {
            mMinComboTimesCared = minComboTimesCared;
            return this;
        }

        /**
         * set {@link #mTarget}
         * @param target target to detect combo on
         * @return {@link RxComboDetector} instance
         */
        public RxComboDetector detectOn(@NonNull View target) {
            return new RxComboDetector(target, mMaxIntervalMillis, mMinComboTimesCared);
        }
    }
}
