package demo.rxandroid.op;

import demo.rxandroid.ObserverPatternActivity.UpdateResult;

/**
 * Created by Raviteja on 9/21/2016. RxAndroid
 */

public class HexObserver extends Observer {

    private UpdateResult updateResult;

    public HexObserver(Subject subject, UpdateResult updateResult) {
        this.subject = subject;
        this.updateResult = updateResult;
        this.subject.attach(this);
    }

    @Override
    public void update() {
        updateResult.result("Hex String: " + Integer.toHexString(subject.getState()));
    }
}