package demo.rxandroid;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import demo.rxandroid.databinding.ActivityRetrofitBinding;
import demo.rxandroid.retrofit.Contributor;
import demo.rxandroid.retrofit.GithubApi;
import demo.rxandroid.retrofit.GithubService;
import demo.rxandroid.retrofit.User;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

import static android.text.TextUtils.isEmpty;

public class RetrofitActivity extends AppCompatActivity {
    ActivityRetrofitBinding activityRetrofitBinding;
    private GithubApi _githubService;
    private CompositeSubscription _subscriptions;
//    RxCache rxCache;
    GithubModel githubModel;

    @Override
    protected void onDestroy() {
        _subscriptions.unsubscribe();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRetrofitBinding = DataBindingUtil.setContentView(this, R.layout.activity_retrofit);
        githubModel = new GithubModel();
        activityRetrofitBinding.setGithubModel(githubModel);
        activityRetrofitBinding.getContributors.setEnabled(false);
        activityRetrofitBinding.getContributorsAndUsers.setEnabled(false);

//        rxCache = RxCache.instanceOf();
//        rxCache.cache();

        String githubToken = "628a5a176f7beb54611e890314299b230a218dea";
        _githubService = GithubService.createGithubService(githubToken);
        _subscriptions = new CompositeSubscription();

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {
        activityRetrofitBinding.getContributors.setOnClickListener(v -> {
            activityRetrofitBinding.result.setText("");
            callCont();
        });
        activityRetrofitBinding.getContributorsAndUsers.setOnClickListener(v -> {
            activityRetrofitBinding.result.setText("");
            callContUser();
        });
        activityRetrofitBinding.clear.setOnClickListener(v -> activityRetrofitBinding.result.setText(""));
    }

//    private void getCache() {
//        System.out.println("1");
//        ConnectableObservable<String> userConnectableObservable = rxCache.getUserEvents().distinct().replay();
//        userConnectableObservable.subscribe(new Subscriber<String>() {
//            @Override
//            public void onCompleted() {
//            }
//
//            @Override
//            public void onError(Throwable e) {
//            }
//
//            @Override
//            public void onNext(String list) {
////                if (list != null) {
////                    ArrayAdapter adapter = new ArrayAdapter<>(RetrofitActivity.this, android.R.layout.simple_list_item_1, (String[]) list.toArray());
////                    activityRetrofitBinding.userName.setAdapter(adapter);
////                }
//            }
//        });
//        userConnectableObservable.connect();
//        ConnectableObservable<String> repoConnectableObservable = rxCache.getRepoEvents().distinct().replay();
//        repoConnectableObservable.toList().observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<String>>() {
//            @Override
//            public void onCompleted() {
//            }
//
//            @Override
//            public void onError(Throwable e) {
//            }
//
//            @Override
//            public void onNext(List<String> list) {
//                if (list != null) {
//                    ArrayAdapter adapter = new ArrayAdapter<>(RetrofitActivity.this, android.R.layout.simple_list_item_1, (String[]) list.toArray());
//                    activityRetrofitBinding.repoName.setAdapter(adapter);
//                }
//            }
//        });
//        repoConnectableObservable.connect();
//    }

    private void callContUser() {
//        rxCache.storeUserValues(activityRetrofitBinding.getGithubModel().user.get());
//        rxCache.storeRepoValues(activityRetrofitBinding.getGithubModel().repo.get());
        _subscriptions.add(//
                _githubService.contributors(activityRetrofitBinding.getGithubModel().user.get(),
                        activityRetrofitBinding.getGithubModel().repo.get())
                        .flatMap(new Func1<List<Contributor>, Observable<Contributor>>() {
                            @Override
                            public Observable<Contributor> call(List<Contributor> contributors) {
                                return Observable.from(contributors);
                            }
                        })
                        .flatMap(new Func1<Contributor, Observable<Pair<User, Contributor>>>() {
                            @Override
                            public Observable<Pair<User, Contributor>> call(Contributor contributor) {
                                Observable<User> _userObservable = _githubService.user(contributor.login)
                                        .filter(user -> !isEmpty(user.name) && !isEmpty(user.email));

                                return Observable.zip(_userObservable,
                                        Observable.just(contributor),
                                        Pair::new);
                            }
                        })
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Pair<User, Contributor>>() {
                            @Override
                            public void onCompleted() {
                                activityRetrofitBinding.result.append("Retrofit call completed ");
                            }

                            @Override
                            public void onError(Throwable e) {
                                activityRetrofitBinding.result.append("error while getting the list of contributors along with full names");
                            }

                            @Override
                            public void onNext(Pair<User, Contributor> pair) {
                                User user = pair.first;
                                Contributor contributor = pair.second;
                                activityRetrofitBinding.result.append(String.format(Locale.getDefault(),
                                        "%s(%s) has made %d contributions to %s",
                                        user.name,
                                        user.email,
                                        contributor.contributions,
                                        activityRetrofitBinding.getGithubModel().repo.get()) + "\n");
                            }
                        }));
    }

    private void callCont() {
//        rxCache.storeUserValues(activityRetrofitBinding.getGithubModel().user.get());
//        rxCache.storeRepoValues(activityRetrofitBinding.getGithubModel().repo.get());
        _subscriptions.add(//
                _githubService.contributors(activityRetrofitBinding.getGithubModel().user.get(),
                        activityRetrofitBinding.getGithubModel().repo.get())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<List<Contributor>>() {
                            @Override
                            public void onCompleted() {
                                activityRetrofitBinding.result.append("Retrofit call completed\n");
                            }

                            @Override
                            public void onError(Throwable e) {
                                activityRetrofitBinding.result.append("oops we got an error while getting the list of contributors\n");
                            }

                            @Override
                            public void onNext(List<Contributor> contributors) {
                                for (Contributor c : contributors) {
                                    activityRetrofitBinding.result.append(String.format(Locale.getDefault(),
                                            "%s has made %d contributions to %s",
                                            c.login,
                                            c.contributions,
                                            activityRetrofitBinding.getGithubModel().repo.get()) + "\n");
                                }
                            }
                        }));
    }

    public class GithubModel {
        public ObservableField<String> user = new ObservableField<>();
        public ObservableField<String> repo = new ObservableField<>();

        GithubModel() {
            Observable.combineLatest(toObservable(user), toObservable(repo), (user, repo)
                    -> isNotNullOrEmpty(user) && isNotNullOrEmpty(repo))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        activityRetrofitBinding.getContributors.setEnabled(result);
                        activityRetrofitBinding.getContributorsAndUsers.setEnabled(result);
                    }, Throwable::printStackTrace);
        }
    }

    public static <T> Observable<T> toObservable(@NonNull final ObservableField<T> observableField) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> subscriber) {
                subscriber.onNext(observableField.get());

                final android.databinding.Observable.OnPropertyChangedCallback callback = new android.databinding.Observable.OnPropertyChangedCallback() {
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
