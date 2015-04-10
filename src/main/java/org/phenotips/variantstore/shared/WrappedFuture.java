package org.phenotips.variantstore.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A Future that wraps around multiple futures, and completes only when they all complete.
 */
public class WrappedFuture implements Future<List> {
    private List<Future> futures;
    private boolean canceled = false;

    public WrappedFuture(Future... futures) {
        this.futures = Arrays.asList(futures);
    }

    @Override
    public boolean cancel(boolean b) {
        boolean ret = true;
        this.canceled = true;

        for (Future f : futures) {
            ret = ret && f.cancel(b);
        }

        return ret;
    }

    @Override
    public boolean isCancelled() {
        return this.canceled;
    }

    @Override
    public boolean isDone() {
        for (Future f : futures) {
            if (!f.isDone()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Object> get() throws InterruptedException, ExecutionException {
        List<Object> list = new ArrayList<Object>();
        for (Future f : futures) {
            list.add(f.get());
        }
        return list;
    }

    @Override
    public List get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        List<Object> list = new ArrayList<Object>();
        for (Future f : futures) {
            list.add(f.get(l, timeUnit));
        }
        return list;
    }
}
