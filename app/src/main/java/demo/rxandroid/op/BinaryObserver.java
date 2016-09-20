package demo.rxandroid.op;

import demo.rxandroid.ObserverPatternActivity.UpdateResult;

/**
 * Created by Raviteja on 9/21/2016. RxAndroid
 */

public class BinaryObserver extends Observer {

    private UpdateResult updateResult;

    public BinaryObserver(Subject subject, UpdateResult updateResult) {
        this.subject = subject;
        this.updateResult = updateResult;
        this.subject.attach(this);
    }

    @Override
    public void update() {
        updateResult.result("Binary String: " + Integer.toBinaryString(subject.getState()));
    }
}