package demo.rxandroid.rxbus_fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import demo.rxandroid.R;
import demo.rxandroid.databinding.FragmentTopRxBinding;

public class TopRxFragment extends Fragment {
    FragmentTopRxBinding fragmentTopRxBinding;
    RxBus rxBus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentTopRxBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_top_rx, container, false);
        fragmentTopRxBinding.setClickEvents(clickEvents);
        rxBus = RxBus.instanceOf();
        return fragmentTopRxBinding.getRoot();
    }

    ClickEvents clickEvents = new ClickEvents() {
        @Override
        public void onClick1(View v) {
            if (rxBus != null && fragmentTopRxBinding.editText.getText().toString().length() > 0) {
                rxBus.send(fragmentTopRxBinding.editText.getText().toString());
                fragmentTopRxBinding.editText.setText("");
            } else if (getContext() != null) {
                Toast.makeText(getContext(), "Enter some text to send.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onClick2(View v) {
            if (rxBus != null) {
                rxBus.send(new MyObject());
            }
        }
    };

    public interface ClickEvents {
        void onClick1(View v);

        void onClick2(View v);
    }

    public class MyObject {
        int a;
        String b;

        MyObject() {
            this.a = new Random().nextInt(100);
            this.b = UUID.randomUUID().toString();
        }

        public String getString() {
            return String.format(Locale.getDefault(), "%s/%d", b, a);
        }
    }
}
