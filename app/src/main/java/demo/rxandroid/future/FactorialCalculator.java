package demo.rxandroid.future;

import junit.framework.Test;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Raviteja on 9/21/2016. RxAndroid
 */

public class FactorialCalculator implements Callable<String> {

    @Override
    public String call() {
        int factorialNumber = new Random().nextInt(25);
        long output = 0;
        try {
            output = factorial(factorialNumber);
        } catch (InterruptedException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
        return String.format(Locale.getDefault(),
                "Factorial of %d id : %d\n", factorialNumber, output);
    }

    private long factorial(int number) throws InterruptedException {
        if (number < 0) {
            throw new IllegalArgumentException("Number must be greater than zero");
        }
        long result = 1;
        while (number > 0) {
            Thread.sleep(50);
            result = result * number;
            number--;
        }
        return result;
    }
}
