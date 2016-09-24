package demo.rxandroid;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import demo.rxandroid.databinding.ActivityFunStartsHereBinding;

public class FunStartsHereActivity extends AppCompatActivity {
    ActivityFunStartsHereBinding activityFunStartsHereBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFunStartsHereBinding = DataBindingUtil.setContentView(this, R.layout.activity_fun_starts_here);
        activityFunStartsHereBinding.setClickEvents(clickEvents);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        init();
    }

    private void init() {
//
//        Observable
//                .from(Executors.newFixedThreadPool(1).submit(new FactorialCalculator()))
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<String>() {
//                    @Override
//                    public void onCompleted() {
//                        System.out.println("Done");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                        System.out.println("Error");
//                    }
//
//                    @Override
//                    public void onNext(String s) {
//
//                        System.out.println(s);
//                    }
//                });
//
//        for (int i = 0; i < 100; i++) {
//            Observable
//                    .from(Executors.newFixedThreadPool(1).submit(new FactorialCalculator()))
//                    .subscribeOn(Schedulers.computation())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(System.out::println);
//        }
//
//        ExecutorService executorService = Executors.newFixedThreadPool(3);
//        for (int i = 0; i < 100; i++) {
//            Observable
//                    .from(executorService.submit(new FactorialCalculator()))
//                    .subscribeOn(Schedulers.computation())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(System.out::println);
//        }
    }

    ClickEvents clickEvents = new ClickEvents() {
        @Override
        public void onClick1(View v) {
            startActivity(new Intent(FunStartsHereActivity.this, RxExample1Activity.class));
        }

        @Override
        public void onClick2(View v) {

        }

        @Override
        public void onClick3(View v) {

        }
    };

    public interface ClickEvents{
        void onClick1(View v);
        void onClick2(View v);
        void onClick3(View v);
    }
}