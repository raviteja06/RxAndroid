package demo.rxandroid;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;

import java.util.Locale;
import java.util.Random;

import demo.rxandroid.databinding.ActivityObserverPatternBinding;
import demo.rxandroid.op.BinaryObserver;
import demo.rxandroid.op.HexObserver;
import demo.rxandroid.op.OctalObserver;
import demo.rxandroid.op.Subject;

public class ObserverPatternActivity extends AppCompatActivity {
    private ActivityObserverPatternBinding activityObserverPatternBinding;
    private BinaryObserver binaryObserver;
    private OctalObserver octalObserver;
    private HexObserver hexObserver;
    private Subject subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityObserverPatternBinding = DataBindingUtil.setContentView(this, R.layout.activity_observer_pattern);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        init();
    }

    private void init() {
        if (activityObserverPatternBinding == null)
            return;
        subject = new Subject();
        activityObserverPatternBinding.result.setMovementMethod(new ScrollingMovementMethod());
        subscribeToAll();
        activityObserverPatternBinding.sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subject != null) {
                    int randomNumber = random();
                    updateResult.result(String.format(Locale.getDefault(), "\nRandom Number : %d", randomNumber));
                    subject.setState(randomNumber);
                }
            }
        });
        activityObserverPatternBinding.subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityObserverPatternBinding.subscribe.getTag().equals(getString(R.string.subscribed))) {
                    subscribeToAll();
                } else {
                    unSubscribeToAll();
                }
            }
        });
        activityObserverPatternBinding.subscribe1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityObserverPatternBinding.subscribe1.getTag().equals(getString(R.string.subscribed))) {
                    subscribeToBinary();
                } else {
                    unSubscribeToBinary();
                }
            }
        });
        activityObserverPatternBinding.subscribe2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityObserverPatternBinding.subscribe2.getTag().equals(getString(R.string.subscribed))) {
                    subscribeToOctal();
                } else {
                    unSubscribeToOctal();
                }
            }
        });
        activityObserverPatternBinding.subscribe3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityObserverPatternBinding.subscribe3.getTag().equals(getString(R.string.subscribed))) {
                    subscribeToHex();
                } else {
                    unSubscribeToHex();
                }
            }
        });
    }

    private int random() {
        return new Random().nextInt(1000);
    }

    private void subscribeToAll() {
        if (subject != null) {
            subscribeToBinary();
            subscribeToOctal();
            subscribeToHex();
            activityObserverPatternBinding.subscribe.setText(R.string.un_subscribe_all);
            activityObserverPatternBinding.subscribe.setTag(getString(R.string.un_subscribed));
        }
    }

    private void subscribeToHex() {
        if (hexObserver != null) {
            subject.attach(hexObserver);
        } else {
            hexObserver = new HexObserver(subject, updateResult);
        }
        activityObserverPatternBinding.subscribe3.setText(R.string.un_subscribe_3);
        activityObserverPatternBinding.subscribe3.setTag(getString(R.string.un_subscribed));
    }

    private void subscribeToOctal() {
        if (octalObserver != null) {
            subject.attach(octalObserver);
        } else {
            octalObserver = new OctalObserver(subject, updateResult);
        }
        activityObserverPatternBinding.subscribe2.setText(R.string.un_subscribe_2);
        activityObserverPatternBinding.subscribe2.setTag(getString(R.string.un_subscribed));
    }

    private void subscribeToBinary() {
        if (binaryObserver != null) {
            subject.attach(binaryObserver);
        } else {
            binaryObserver = new BinaryObserver(subject, updateResult);
        }
        activityObserverPatternBinding.subscribe1.setText(R.string.un_subscribe_1);
        activityObserverPatternBinding.subscribe1.setTag(getString(R.string.un_subscribed));
    }

    private void unSubscribeToHex() {
        if (hexObserver != null) {
            subject.detach(hexObserver);
        }
        activityObserverPatternBinding.subscribe3.setText(R.string.subscribe_3);
        activityObserverPatternBinding.subscribe3.setTag(getString(R.string.subscribed));
    }

    private void unSubscribeToOctal() {
        if (octalObserver != null) {
            subject.detach(octalObserver);
        }
        activityObserverPatternBinding.subscribe2.setText(R.string.subscribe_2);
        activityObserverPatternBinding.subscribe2.setTag(getString(R.string.subscribed));
    }

    private void unSubscribeToBinary() {
        if (binaryObserver != null) {
            subject.detach(binaryObserver);
        }
        activityObserverPatternBinding.subscribe1.setText(R.string.subscribe_1);
        activityObserverPatternBinding.subscribe1.setTag(getString(R.string.subscribed));
    }

    private void unSubscribeToAll() {
        if (subject != null) {
            unSubscribeToBinary();
            unSubscribeToOctal();
            unSubscribeToHex();
            activityObserverPatternBinding.subscribe.setText(R.string.subscribe_all);
            activityObserverPatternBinding.subscribe.setTag(getString(R.string.subscribed));
        }
    }

    private UpdateResult updateResult = new UpdateResult() {
        @Override
        public void result(String res) {
            if (activityObserverPatternBinding != null && activityObserverPatternBinding.result != null) {
                activityObserverPatternBinding.result.append(res + "\n");
            }
        }
    };

    public interface UpdateResult {
        void result(String res);
    }
}