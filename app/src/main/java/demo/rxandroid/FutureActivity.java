package demo.rxandroid;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import demo.rxandroid.databinding.ActivityFutureBinding;
import demo.rxandroid.future.FactorialCalculator;

public class FutureActivity extends AppCompatActivity {
    private final ExecutorService threadPool = Executors.newFixedThreadPool(2);
    private ActivityFutureBinding activityFutureBinding;
    private Future<String> future;
//    private FutureTask<String> futureTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFutureBinding = DataBindingUtil.setContentView(this, R.layout.activity_future);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        threadPool.shutdown();
    }

    private void init() {
        if (activityFutureBinding != null) {
            activityFutureBinding.result.setMovementMethod(new ScrollingMovementMethod());
            activityFutureBinding.getData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (future != null && !future.isDone()) {
                        future.cancel(true);
                    }
                    future = threadPool.submit(new FactorialCalculator());
//                    futureTask =
//                            new FutureTask<>(new FactorialCalculator());
//                    threadPool.execute(futureTask);
                }
            });
            activityFutureBinding.dataAvailable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activityFutureBinding != null && activityFutureBinding.result != null) {
                        if (future != null) {
                            if (future.isDone()) {
                                try {
                                    activityFutureBinding.result.append(future.get());
                                } catch (InterruptedException | ExecutionException e) {
                                    activityFutureBinding.result.append("Interrupted in between process\n");
                                }
                            } else {
                                activityFutureBinding.result.append("Still processing data\n");
                            }
                        }
//                        if (futureTask != null) {
//                            if (futureTask.isDone()) {
//                                try {
//                                    activityFutureBinding.result.append(futureTask.get());
//                                } catch (InterruptedException | ExecutionException e) {
//                                    activityFutureBinding.result.append("Interrupted in between process\n");
//                                }
//                            } else {
//                                activityFutureBinding.result.append("Still processing data\n");
//                            }
//                        }
                    }
                }
            });
        }
    }
}