package demo.rxandroid;

        import android.databinding.DataBindingUtil;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.text.method.ScrollingMovementMethod;

        import java.util.concurrent.ExecutorService;
        import java.util.concurrent.Executors;

        import demo.rxandroid.databinding.ActivityRxExample1Binding;
        import demo.rxandroid.future.FactorialCalculator;
        import rx.Observable;
        import rx.android.schedulers.AndroidSchedulers;
        import rx.schedulers.Schedulers;

public class RxExample1Activity extends AppCompatActivity {
    ActivityRxExample1Binding activityRxExample1Binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRxExample1Binding = DataBindingUtil.setContentView(this, R.layout.activity_rx_example1);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        init();
    }

    private void init() {
        if (activityRxExample1Binding != null) {
            activityRxExample1Binding.result.setMovementMethod(new ScrollingMovementMethod());
            activityRxExample1Binding.clear.setOnClickListener(v -> activityRxExample1Binding.result.setText(""));
            activityRxExample1Binding.runOnMain.setOnClickListener(v -> runOnMainThread());
            activityRxExample1Binding.runOnCMPT.setOnClickListener(v -> runOnCmpt());
            activityRxExample1Binding.runOnImm.setOnClickListener(v -> runOnImmediate());
            activityRxExample1Binding.runOnNew.setOnClickListener(v -> runOnNewES());
            activityRxExample1Binding.runOnNewCommonES.setOnClickListener(v -> runOnNewAndES());
        }
    }

    private void runOnMainThread() {
        activityRxExample1Binding.result.append(getString(R.string.start_of_block) + "\n");
        for (int i = 0; i < 5; i++)
            Observable.from(Executors.newSingleThreadExecutor().submit(new FactorialCalculator()))
                    .subscribe(s -> activityRxExample1Binding.result.append(s));
        activityRxExample1Binding.result.append(getString(R.string.end_of_block) + "\n");
    }

    private void runOnNewES() {
        activityRxExample1Binding.result.append(getString(R.string.start_of_block) + "\n");
        for (int i = 0; i < 10; i++)
            Observable.from(Executors.newSingleThreadExecutor().submit(new FactorialCalculator()))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(s -> activityRxExample1Binding.result.append(s));
        activityRxExample1Binding.result.append(getString(R.string.end_of_block) + "\n");
    }

    private void runOnNewAndES() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        activityRxExample1Binding.result.append(getString(R.string.start_of_block) + "\n");
        for (int i = 0; i < 10; i++)
            Observable.from(executorService.submit(new FactorialCalculator()))
                    .subscribeOn(Schedulers.from(executorService))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(s -> activityRxExample1Binding.result.append(s));
        activityRxExample1Binding.result.append(getString(R.string.end_of_block) + "\n");
    }

    private void runOnCmpt() {
        activityRxExample1Binding.result.append(getString(R.string.start_of_block) + "\n");
        Observable.range(1, 20)
                .map(i -> i * 100)
                .doOnNext(i -> runOnUiThread(() -> activityRxExample1Binding.result.append("Emitting " + i + "\n")))
                .map(i -> i * 10)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(i -> activityRxExample1Binding.result.append("Received " + i + "\n"));
        activityRxExample1Binding.result.append(getString(R.string.end_of_block) + "\n");
    }

    private void runOnImmediate() {
        activityRxExample1Binding.result.append(getString(R.string.start_of_block) + "\n");
        Observable.range(1, 20)
                .map(i -> i * 100)
                .doOnNext(i -> runOnUiThread(() -> activityRxExample1Binding.result.append("Emitting " + i + "\n")))
                .map(i -> i * 10)
                .subscribeOn(Schedulers.immediate())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(i -> activityRxExample1Binding.result.append("Received " + i + "\n"));
        activityRxExample1Binding.result.append(getString(R.string.end_of_block) + "\n");
    }
}