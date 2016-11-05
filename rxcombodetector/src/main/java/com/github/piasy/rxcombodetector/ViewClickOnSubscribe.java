/*
 * Copyright (C) 2015 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.piasy.rxcombodetector;

import android.view.View;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.MainThreadDisposable;

import static com.github.piasy.rxcombodetector.Preconditions.checkUiThread;

final class ViewClickOnSubscribe implements FlowableOnSubscribe<Integer> {
    private final View view;

    ViewClickOnSubscribe(View view) {
        this.view = view;
    }

    @Override
    public void subscribe(final FlowableEmitter<Integer> emitter) throws Exception {
        checkUiThread();

        View.OnClickListener listener = v -> {
            if (!emitter.isCancelled()) {
                emitter.onNext(1);
            }
        };
        view.setOnClickListener(listener);

        emitter.setDisposable(new MainThreadDisposable() {
            @Override
            protected void onDispose() {
                view.setOnClickListener(null);
            }
        });
    }
}
