package demo.rxandroid.op;

/**
 * Created by Raviteja on 9/21/2016. RxAndroid
 */

abstract class Observer {
    protected Subject subject;

    abstract void update();
}