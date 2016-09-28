package demo.rxandroid;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import demo.rxandroid.databinding.ActivitySubscribeOnBinding;
import demo.rxandroid.future.FactorialCalculator;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SubscribeOnActivity extends AppCompatActivity {
    ActivitySubscribeOnBinding activitySubscribeOnBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySubscribeOnBinding = DataBindingUtil.setContentView(this, R.layout.activity_subscribe_on);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        init();
    }

    private void init() {
        if (activitySubscribeOnBinding != null) {
            activitySubscribeOnBinding.result.setMovementMethod(new ScrollingMovementMethod());
            activitySubscribeOnBinding.clear.setOnClickListener(v -> activitySubscribeOnBinding.result.setText(""));
            activitySubscribeOnBinding.runOnMain.setOnClickListener(v -> runOnMainThread());
            activitySubscribeOnBinding.runOnCMPT.setOnClickListener(v -> runOnCmpt());
            activitySubscribeOnBinding.runOnImm.setOnClickListener(v -> runOnImmediate());
            activitySubscribeOnBinding.runOnNew.setOnClickListener(v -> runOnNewES());
            activitySubscribeOnBinding.runOnNewCommonES.setOnClickListener(v -> runOnNewAndES());
        }
    }

    private void runOnMainThread() {
        activitySubscribeOnBinding.result.append(getString(R.string.start_of_block) + "\n");
        for (int i = 0; i < 5; i++)
            Observable.from(Executors.newSingleThreadExecutor().submit(new FactorialCalculator()))
                    .subscribe(s -> activitySubscribeOnBinding.result.append(s));
        activitySubscribeOnBinding.result.append(getString(R.string.end_of_block) + "\n");
    }

    private void runOnNewES() {
        activitySubscribeOnBinding.result.append(getString(R.string.start_of_block) + "\n");
        for (int i = 0; i < 10; i++)
            Observable.from(Executors.newSingleThreadExecutor().submit(new FactorialCalculator()))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(s -> activitySubscribeOnBinding.result.append(s));
        activitySubscribeOnBinding.result.append(getString(R.string.end_of_block) + "\n");
    }

    private void runOnNewAndES() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        activitySubscribeOnBinding.result.append(getString(R.string.start_of_block) + "\n");
        for (int i = 0; i < 10; i++)
            Observable.from(executorService.submit(new FactorialCalculator()))
                    .subscribeOn(Schedulers.from(executorService))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(s -> activitySubscribeOnBinding.result.append(s));
        activitySubscribeOnBinding.result.append(getString(R.string.end_of_block) + "\n");
    }

    private void runOnCmpt() {
        activitySubscribeOnBinding.result.append(getString(R.string.start_of_block) + "\n");
        Observable.range(1, 20)
                .map(i -> i * 100)
                .doOnNext(i -> runOnUiThread(() -> activitySubscribeOnBinding.result.append("Emitting " + i + "\n")))
                .map(i -> i * 10)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(i -> activitySubscribeOnBinding.result.append("Received " + i + "\n"));
//        Observable.range(1, 20)
//                .map(new Func1<Integer, Integer>() {
//                    @Override
//                    public Integer call(Integer i) {
//                        return i * 100;
//                    }
//                })
//                .doOnNext(i -> runOnUiThread(() -> activitySubscribeOnBinding.result.append("Emitting " + i + "\n")))
//                .map(i -> i * 10)
//                .subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(i -> activitySubscribeOnBinding.result.append("Received " + i + "\n"));
        activitySubscribeOnBinding.result.append(getString(R.string.end_of_block) + "\n");
    }

    private void runOnImmediate() {
        activitySubscribeOnBinding.result.append(getString(R.string.start_of_block) + "\n");
        Observable.range(1, 20)
                .map(i -> i * 100)
                .doOnNext(i -> runOnUiThread(() -> activitySubscribeOnBinding.result.append("Emitting " + i + "\n")))
                .map(i -> i * 10)
                .subscribeOn(Schedulers.immediate())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(i -> activitySubscribeOnBinding.result.append("Received " + i + "\n"));
        activitySubscribeOnBinding.result.append(getString(R.string.end_of_block) + "\n");
    }
}