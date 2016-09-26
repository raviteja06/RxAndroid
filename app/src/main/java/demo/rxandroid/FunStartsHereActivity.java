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

    }

    ClickEvents clickEvents = new ClickEvents() {
        @Override
        public void onClick1(View v) {
            startActivity(new Intent(FunStartsHereActivity.this, demo.rxandroid.SubscribeOnActivity.class));
        }

        @Override
        public void onClick2(View v) {
            startActivity(new Intent(FunStartsHereActivity.this, RxBusActivity.class));
        }

        @Override
        public void onClick3(View v) {
            startActivity(new Intent(FunStartsHereActivity.this, BindingActivity.class));
        }

        @Override
        public void onClick4(View v) {
            startActivity(new Intent(FunStartsHereActivity.this, RxExampleActivity.class));
        }
    };

    public interface ClickEvents{
        void onClick1(View v);
        void onClick2(View v);
        void onClick3(View v);
        void onClick4(View v);
    }
}