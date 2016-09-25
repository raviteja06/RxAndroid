package demo.rxandroid;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import demo.rxandroid.databinding.ActivityRxBusBinding;
import demo.rxandroid.rxbus_fragments.BottomRxFragment;
import demo.rxandroid.rxbus_fragments.TopRxFragment;

public class RxBusActivity extends AppCompatActivity {
    ActivityRxBusBinding activityRxBusBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRxBusBinding = DataBindingUtil.setContentView(this, R.layout.activity_rx_bus);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        init();
    }

    private void init() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(activityRxBusBinding.frame1.getId(), new TopRxFragment());
        ft.add(activityRxBusBinding.frame2.getId(), new BottomRxFragment()).commit();
    }
}
