package demo.rxandroid.rxbus_fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import demo.rxandroid.R;
import demo.rxandroid.databinding.FragmentBottomRxBinding;

public class BottomRxFragment extends Fragment {
    FragmentBottomRxBinding fragmentBottomRxBinding;
    RxBus rxBus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentBottomRxBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_rx, container, false);
        rxBus = RxBus.instanceOf();
        rxBus.getEvents().subscribe(o -> {
            if (o instanceof TopRxFragment.MyObject) {
                fragmentBottomRxBinding.setMyObject((TopRxFragment.MyObject) o);
            } else if (o instanceof String) {
                fragmentBottomRxBinding.setMyText((String) o);
            }
        });
        return fragmentBottomRxBinding.getRoot();
    }
}
