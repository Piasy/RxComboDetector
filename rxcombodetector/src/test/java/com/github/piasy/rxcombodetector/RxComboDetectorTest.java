package com.github.piasy.rxcombodetector;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class RxComboDetectorTest {
    @Test
    public void testDetect() {
        Flowable<Integer> mockClicks = Flowable
                .create(emitter -> {
                    emitter.onNext(1);
                    sleepSilently(50);
                    emitter.onNext(1);
                    sleepSilently(50);
                    emitter.onNext(1);
                    sleepSilently(50);
                    emitter.onNext(1);

                    sleepSilently(150);

                    emitter.onNext(1);
                    sleepSilently(50);
                    emitter.onNext(1);

                    sleepSilently(150);
                    emitter.onNext(1);

                    sleepSilently(150);
                    emitter.onNext(1);

                    emitter.onComplete();
                }, BackpressureStrategy.BUFFER);

        TestSubscriber<Integer> subscriber = new TestSubscriber<>();
        RxComboDetector.detect(mockClicks, 100, 2)
                .subscribeOn(Schedulers.from(command -> new Thread(command).start()))
                .subscribe(subscriber);

        subscriber.awaitTerminalEvent(30, TimeUnit.SECONDS);
        subscriber.assertValueCount(4);
        subscriber.assertValues(2, 3, 4, 2);
    }

    private void sleepSilently(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}