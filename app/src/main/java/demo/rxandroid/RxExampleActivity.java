package demo.rxandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import demo.rxandroid.example.RxExamples;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;

public class RxExampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_example);
        new RxExamples().runTasks();
        try {
            new RxExamples().runExecutor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Observable
                .interval(1, TimeUnit.SECONDS)
                .map(Long::intValue)
                .take(20)
                .share();

        Observable//
                .error(new RuntimeException("testing")) // always fails
                .retryWhen(new RetryWithDelay(5, 1000)) // notice this is called only onError (onNext values sent are ignored)
                .doOnSubscribe(() -> System.out.println("Attempting the impossible 5 times in intervals of 1s"))//
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("on Completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("Error: I give up!");
                    }

                    @Override
                    public void onNext(Object aVoid) {
                        System.out.println("on Next");
                    }
                });

        Observable.range(1, 4)
                .delay(integer -> Observable.just(integer)
                        .delay(integer, TimeUnit.SECONDS))
                .doOnSubscribe(() -> System.out.println(String.format(Locale.getDefault(),
                        "Execute 4 tasks with delay - time now: [xx:%02d]",
                        _getSecondHand())))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("arrrr. Error");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println(String.format(Locale.getDefault(), "executing Task %d [xx:%02d]", integer, _getSecondHand()));
                    }
                });
    }
    public class RetryWithDelay
            implements Func1<Observable<? extends Throwable>, Observable<?>> {

        private final int _maxRetries;
        private final int _retryDelayMillis;
        private int _retryCount;

        RetryWithDelay(final int maxRetries, final int retryDelayMillis) {
            _maxRetries = maxRetries;
            _retryDelayMillis = retryDelayMillis;
            _retryCount = 0;
        }

        // this is a notificationhandler, all that is cared about here
        // is the emission "type" not emission "content"
        // only onNext triggers a re-subscription (onError + onComplete kills it)

        @Override
        public Observable<?> call(Observable<? extends Throwable> inputObservable) {

            // it is critical to use inputObservable in the chain for the result
            // ignoring it and doing your own thing will break the sequence

            return inputObservable.flatMap(new Func1<Throwable, Observable<?>>() {
                @Override
                public Observable<?> call(Throwable throwable) {
                    if (++_retryCount < _maxRetries) {

                        // When this Observable calls onNext, the original
                        // Observable will be retried (i.e. re-subscribed)

                        System.out.println(String.format(Locale.getDefault(),
                                "Retrying in %d ms", _retryCount * _retryDelayMillis));

                        return Observable.timer(_retryCount * _retryDelayMillis,
                                TimeUnit.MILLISECONDS);
                    }

                    System.out.println("Argh! i give up");

                    // Max retries hit. Pass an error so the chain is forcibly completed
                    // only onNext triggers a re-subscription (onError + onComplete kills it)
                    return Observable.error(throwable);
                }
            });
        }
    }
    private int _getSecondHand() {
        long millis = System.currentTimeMillis();
        return (int) (TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }
}
