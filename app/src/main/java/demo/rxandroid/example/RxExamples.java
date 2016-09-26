package demo.rxandroid.example;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Raviteja on 9/26/2016. RxAndroid
 */

public class RxExamples {
    public void runTasks() {
        System.out.println("------------ mergingAsync");
        mergingAsync();
        System.out.println("------------ mergingSync");
        mergingSync();
        System.out.println("------------ mergingSyncMadeAsync");
        mergingSyncMadeAsync();
        System.out.println("------------ flatMapExampleSync");
        flatMapExampleSync();
        System.out.println("------------ flatMapExampleAsync");
        flatMapExampleAsync();
        System.out.println("------------");
    }

    private void mergingAsync() {
        Observable.merge(getDataAsync(1), getDataAsync(2)).toBlocking().forEach(System.out::println);
    }

    private void mergingSync() {
        // here you'll see the delay as each is executed synchronously
        Observable.merge(getDataSync(1), getDataSync(2)).toBlocking().forEach(System.out::println);
    }

    private void mergingSyncMadeAsync() {
        // if you have something synchronous and want to make it async, you can schedule it like this
        // so here we see both executed concurrently
        Observable.merge(getDataSync(1).subscribeOn(Schedulers.io()), getDataSync(2).subscribeOn(Schedulers.io())).toBlocking().forEach(System.out::println);
    }

    private void flatMapExampleAsync() {
        Observable.range(0, 5).flatMap(this::getDataAsync).toBlocking().forEach(System.out::println);
    }

    private void flatMapExampleSync() {
        Observable.range(0, 5).flatMap(this::getDataSync).toBlocking().forEach(System.out::println);
    }

    // artificial representations of IO work
    private Observable<Integer> getDataAsync(int i) {
        return getDataSync(i).subscribeOn(Schedulers.io());
    }

    private Observable<Integer> getDataSync(int i) {
        return Observable.create((Subscriber<? super Integer> s) -> {
            // simulate latency
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            s.onNext(i);
            s.onCompleted();
        });
    }

    public void runExecutor() throws Exception {
        final ExecutorService executor = new ThreadPoolExecutor(4, 4, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
        try {

            Future<String> f1 = executor.submit(new CallToRemoteServiceA());
            Observable<String> f1Observable = Observable.from(f1);
            Observable<String> f3Observable = f1Observable
                    .flatMap(new Func1<String, Observable<String>>() {
                        @Override
                        public Observable<String> call(String s) {
                            System.out.println("Observed from f1: " + s);
                            Future<String> f3 = executor.submit(new CallToRemoteServiceC(s));
                            return Observable.from(f3);
                        }
                    });

            Future<Integer> f2 = executor.submit(new CallToRemoteServiceB());
            Observable<Integer> f2Observable = Observable.from(f2);
            Observable<Integer> f4Observable = f2Observable
                    .flatMap(new Func1<Integer, Observable<Integer>>() {
                        @Override
                        public Observable<Integer> call(Integer integer) {
                            System.out.println("Observed from f2: " + integer);
                            Future<Integer> f4 = executor.submit(new CallToRemoteServiceD(integer));
                            return Observable.from(f4);
                        }
                    });

            Observable<Integer> f5Observable = f2Observable
                    .flatMap(new Func1<Integer, Observable<Integer>>() {
                        @Override
                        public Observable<Integer> call(Integer integer) {
                            System.out.println("Observed from f2: " + integer);
                            Future<Integer> f5 = executor.submit(new CallToRemoteServiceE(integer));
                            return Observable.from(f5);
                        }
                    });

            Observable.zip(f3Observable, f4Observable, f5Observable, (s, integer, integer2) -> {
                Map<String, String> map = new HashMap<>();
                map.put("f3", s);
                map.put("f4", String.valueOf(integer));
                map.put("f5", String.valueOf(integer2));
                return map;
            }).subscribe(map -> {
                System.out.println(map.get("f3") + " => " + (Integer.valueOf(map.get("f4")) * Integer.valueOf(map.get("f5"))));
            });

        } finally {
            executor.shutdownNow();
        }
    }

    private static final class CallToRemoteServiceA implements Callable<String> {
        @Override
        public String call() throws Exception {
            System.out.println("A called");
            // simulate fetching data from remote service
            Thread.sleep(100);
            return "responseA";
        }
    }

    private static final class CallToRemoteServiceB implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            System.out.println("B called");
            // simulate fetching data from remote service
            Thread.sleep(40);
            return 100;
        }
    }

    private static final class CallToRemoteServiceC implements Callable<String> {

        private final String dependencyFromA;

        CallToRemoteServiceC(String dependencyFromA) {
            this.dependencyFromA = dependencyFromA;
        }

        @Override
        public String call() throws Exception {
            System.out.println("C called");
            // simulate fetching data from remote service
            Thread.sleep(60);
            return "responseB_" + dependencyFromA;
        }
    }

    private static final class CallToRemoteServiceD implements Callable<Integer> {

        private final Integer dependencyFromB;

        CallToRemoteServiceD(Integer dependencyFromB) {
            this.dependencyFromB = dependencyFromB;
        }

        @Override
        public Integer call() throws Exception {
            System.out.println("D called");
            // simulate fetching data from remote service
            Thread.sleep(140);
            return 40 + dependencyFromB;
        }
    }

    private static final class CallToRemoteServiceE implements Callable<Integer> {

        private final Integer dependencyFromB;

        CallToRemoteServiceE(Integer dependencyFromB) {
            this.dependencyFromB = dependencyFromB;
        }

        @Override
        public Integer call() throws Exception {
            System.out.println("E called");
            // simulate fetching data from remote service
            Thread.sleep(55);
            return 5000 + dependencyFromB;
        }
    }
}