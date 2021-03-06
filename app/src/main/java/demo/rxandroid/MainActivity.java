package demo.rxandroid;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import demo.rxandroid.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityMainBinding.setOnClickButtons(onClickButtons);
    }

    OnClickButtons onClickButtons = new OnClickButtons() {
        @Override
        public void onClickObserverPattern(View v) {
            startActivity(new Intent(MainActivity.this, ObserverPatternActivity.class));
        }

        @Override
        public void onClickFuture(View v) {
            startActivity(new Intent(MainActivity.this, FutureActivity.class));
        }

        @Override
        public void onClickNext(View v) {
            startActivity(new Intent(MainActivity.this, FunStartsHereActivity.class));
        }
    };
}
