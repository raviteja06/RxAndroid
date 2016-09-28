package demo.rxandroid;

import android.databinding.DataBindingUtil;
import android.databinding.Observable.OnPropertyChangedCallback;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import demo.rxandroid.databinding.ActivityBindingBinding;
import rx.AsyncEmitter;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;

public class BindingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding);
        ActivityBindingBinding activityBindingBinding = DataBindingUtil.setContentView(this, R.layout.activity_binding);
        activityBindingBinding.setViewModel(new MainViewModel());

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public class MainViewModel {

        public ObservableField<String> firstName = new ObservableField<>();
        public ObservableField<String> lastName = new ObservableField<>();
        public ObservableField<String> helloText = new ObservableField<>();
        public ObservableBoolean helloButtonEnabled = new ObservableBoolean(false);

        MainViewModel() {
//            Observable.combineLatest(toObservable(firstName), toObservable(lastName), new Func2<String, String, Boolean>() {
//                @Override
//                public Boolean call(String firstName, String lastName) {
//                    return isNotNullOrEmpty(firstName) && isNotNullOrEmpty(lastName);
//                }
//            }).subscribe(new Subscriber<Boolean>() {
//                @Override
//                public void onCompleted() {
//
//                }
//
//                @Override
//                public void onError(Throwable e) {
//                    e.printStackTrace();
//                }
//
//                @Override
//                public void onNext(Boolean result) {
//                    helloButtonEnabled.set(result);
//                    if (!result) {
//                        helloText.set("");
//                    }
//                }
//            });

            Observable.combineLatest(toObservable(firstName), toObservable(lastName), (firstName, lastName)
                    -> isNotNullOrEmpty(firstName) && isNotNullOrEmpty(lastName))
                    .subscribe(result -> {
                        helloButtonEnabled.set(result);
                        if (!result) {
                            helloText.set("");
                        }
                    }, Throwable::printStackTrace);
        }

        public void buttonClicked() {
            if (isNotNullOrEmpty(firstName.get()) && isNotNullOrEmpty(lastName.get()))
                helloText.set(String.format("Hello %s %s !", firstName.get(), lastName.get()));
        }
    }

    public static <T> Observable<T> toObservable(@NonNull final ObservableField<T> observableField) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> subscriber) {
                subscriber.onNext(observableField.get());

                final OnPropertyChangedCallback callback = new OnPropertyChangedCallback() {
                    @Override
                    public void onPropertyChanged(android.databinding.Observable observable, int i) {
                        if (observable == observableField) {
                            subscriber.onNext(observableField.get());
                        }
                    }
                };
                observableField.addOnPropertyChangedCallback(callback);
                // when subscribe is un subscribed the observableField will make sure to remove call back
                subscriber.add(Subscriptions.create(() -> observableField.removeOnPropertyChangedCallback(callback)));
            }
        }).debounce(1, TimeUnit.SECONDS);
    }

    public static boolean isNotNullOrEmpty(String string) {
        return string != null && !string.equals("");
    }
}
