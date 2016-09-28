package demo.rxandroid;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import demo.rxandroid.databinding.ActivityRxExampleBinding;
import demo.rxandroid.rxbus_fragments.RxBus;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RxExampleActivity extends AppCompatActivity {
    ActivityRxExampleBinding activityRxExampleBinding;
    RxBus rxBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRxExampleBinding = DataBindingUtil.setContentView(this, R.layout.activity_rx_example);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        init();
    }

    private void init() {
        activityRxExampleBinding.merge.setOnClickListener(v -> {
            activityRxExampleBinding.result.setText("");
            mergingAsync();
        });
        activityRxExampleBinding.retryWithDelay.setOnClickListener(v -> {
            activityRxExampleBinding.result.setText("");
            retryWithDelay();
        });
        activityRxExampleBinding.runOnDelay.setOnClickListener(v -> {
            activityRxExampleBinding.result.setText("");
            runOnDelay();
        });
        activityRxExampleBinding.zip.setOnClickListener(v -> {
            activityRxExampleBinding.result.setText("");
            zip();
        });
        activityRxExampleBinding.timer.setOnClickListener(v -> {
            activityRxExampleBinding.result.setText("");
            timmer();
        });
        activityRxExampleBinding.clear.setOnClickListener(v -> activityRxExampleBinding.result.setText(""));
    }

    private void zip() {
        Observable.from(new String[]{"Is this a thing", "I dont know"})
                .flatMap(word -> Observable.from(word.split(" ")))
//                .map(word -> word.replaceAll(" ",""))
                .zipWith(Observable.range(1, Integer.MAX_VALUE),
                        (string, count) -> String.format(Locale.getDefault(),
                                "%2d. %s", count, string))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> activityRxExampleBinding.result.append(result + "\n"));
    }

    private void timmer() {
        Observable.interval(1, TimeUnit.SECONDS)
                .flatMap(Observable::just)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> activityRxExampleBinding.result.append("Current Time : " + time() + "\n"));
    }

    private String time() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(cal.getTime());
    }

    private void runOnDelay() {
        Observable.range(1, 4)
                .delay(integer -> Observable.just(integer)
                        .delay((long) Math.pow(integer, 2), TimeUnit.SECONDS))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> activityRxExampleBinding.result.append(String.format(Locale.getDefault(),
                        "Execute 4 tasks with delay - time now: [xx:%02d]",
                        _getSecondHand()) + "\n"))
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                        activityRxExampleBinding.result.append("onCompleted" + "\n");
                    }

                    @Override
                    public void onError(Throwable e) {
                        activityRxExampleBinding.result.append("arrrr. Error" + "\n");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        activityRxExampleBinding.result.append(String.format(Locale.getDefault(), "executing Task %d [xx:%02d]", integer, _getSecondHand()) + "\n");
                    }
                });
    }

    private void retryWithDelay() {
        Observable
                .error(new RuntimeException("testing")) // always fails
                .retryWhen(new RetryWithDelay(5)) // notice this is called only onError
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> activityRxExampleBinding.result.append("Attempting the impossible 5 times in intervals of 1s" + "\n"))//
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        activityRxExampleBinding.result.append("on Completed" + "\n");
                    }

                    @Override
                    public void onError(Throwable e) {
                        activityRxExampleBinding.result.append("Error: I give up!" + "\n");
                    }

                    @Override
                    public void onNext(Object aVoid) {
                        activityRxExampleBinding.result.append("on Next" + "\n");
                    }
                });
    }

    private void mergingAsync() {
        Observable.merge(getDataAsync(1), getDataAsync(2)).observeOn(AndroidSchedulers.mainThread()).subscribe(integer ->
                        activityRxExampleBinding.result.append("Merging Async : " + String.valueOf(integer) + "\n"),
                System.out::println);
    }

    // artificial representations of IO work
    private Observable<Integer> getDataAsync(int i) {
        return getDataSync(i).subscribeOn(Schedulers.io());
    }

    private Observable<Integer> getDataSync(int i) {
        return Observable.create((Subscriber<? super Integer> s) -> {
            try {
                Thread.sleep(500 * new Random().nextInt(5));
            } catch (Exception e) {
                e.printStackTrace();
            }
            s.onNext(i);
            s.onCompleted();
        });
    }

    public class RetryWithDelay
            implements Func1<Observable<?>, Observable<?>> {

        private final int _maxRetries;
        private int _retryCount;

        RetryWithDelay(final int maxRetries) {
            _maxRetries = maxRetries;
            _retryCount = 0;
        }

        // this is a notificationhandler, all that is cared about here
        // is the emission "type" not emission "content"
        // only onNext triggers a re-subscription (onError + onComplete kills it)

        @Override
        public Observable<?> call(Observable<?> inputObservable) {

            // it is critical to use inputObservable in the chain for the result
            // ignoring it and doing your own thing will break the sequence
            inputObservable.observeOn(AndroidSchedulers.mainThread());
            return inputObservable.flatMap((Func1<Object, Observable<?>>) throwable -> {
                if (++_retryCount < _maxRetries) {

                    // When this Observable calls onNext, the original
                    // Observable will be retried (i.e. re-subscribed)

                    return Observable.timer(_retryCount,
                            TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).doOnNext(l -> activityRxExampleBinding.result.append(String.format(Locale.getDefault(),
                            "Retried after %d sec", _retryCount) + "\n"));
                }

                activityRxExampleBinding.result.append("Argh! i give up" + "\n");

                // Max retries hit. Pass an error so the chain is forcibly completed
                // only onNext triggers a re-subscription (onError + onComplete kills it)
                return Observable.error(new RuntimeException());
            });
        }
    }

    private int _getSecondHand() {
        long millis = System.currentTimeMillis();
        return (int) (TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }
}
