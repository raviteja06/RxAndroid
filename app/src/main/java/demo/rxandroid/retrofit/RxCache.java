package demo.rxandroid.retrofit;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by Raviteja on 9/25/2016. RxAndroid
 */

public class RxCache {
    private static RxCache instance;
    private boolean isCached = false;

    private PublishSubject<String> userPublishSubject = PublishSubject.create();
    private PublishSubject<String> repoPublishSubject = PublishSubject.create();

    public void storeUserValues(String string) {
        userPublishSubject.onNext(string);
    }

    public void storeRepoValues(String string) {
        repoPublishSubject.onNext(string);
    }

    public static RxCache instanceOf() {
        if (instance == null) {
            instance = new RxCache();
        }
        return instance;
    }

    public void cache() {
        if (!isCached) {
            userPublishSubject.cache();
            repoPublishSubject.cache();
            isCached = true;
        }
    }

    public Observable<String> getUserEvents() {
        return userPublishSubject;
    }

    public Observable<String> getRepoEvents() {
        return repoPublishSubject;
    }
}
