package demo.rxandroid;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    };
}
