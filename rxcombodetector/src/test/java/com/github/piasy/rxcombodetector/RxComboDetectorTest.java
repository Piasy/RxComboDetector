package com.github.piasy.rxcombodetector;

import android.support.annotation.NonNull;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class RxComboDetectorTest {
    @Test
    public void testDetect() {
        Observable<Void> mockClicks = Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                subscriber.onNext(null);
                sleepSilently(50);
                subscriber.onNext(null);
                sleepSilently(50);
                subscriber.onNext(null);
                sleepSilently(50);
                subscriber.onNext(null);

                sleepSilently(150);

                subscriber.onNext(null);
                sleepSilently(50);
                subscriber.onNext(null);

                sleepSilently(150);
                subscriber.onNext(null);

                sleepSilently(150);
                subscriber.onNext(null);

                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.from(new Executor() {
            @Override
            public void execute(@NonNull Runnable command) {
                new Thread(command).start();
            }
        }));

        TestSubscriber<Integer> subscriber = new TestSubscriber<>();
        RxComboDetector.detect(mockClicks, 100, 2)
                .subscribe(subscriber);

        subscriber.awaitTerminalEventAndUnsubscribeOnTimeout(30, TimeUnit.SECONDS);
        List<Integer> items = subscriber.getOnNextEvents();
        Assert.assertEquals(4, items.size());
        Assert.assertEquals(2, items.get(0).intValue());
        Assert.assertEquals(3, items.get(1).intValue());
        Assert.assertEquals(4, items.get(2).intValue());
        Assert.assertEquals(2, items.get(3).intValue());
    }

    private void sleepSilently(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}