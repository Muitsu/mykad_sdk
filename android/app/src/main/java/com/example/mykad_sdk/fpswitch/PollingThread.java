package com.example.mykad_sdk.fpswitch;

import android.util.Log;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

class PollingThread {
    private static final String TAG = "MyIDApp_PollingThread";
    public static final int DEFAULT_POLLING_PERIOD_MS = 1000;

    private final ReentrantLock mLock = new ReentrantLock();
    private long mIntervalPeriodMillis = 0;
    private OnPollingExecute mOnPollingExecute;
    private ScheduledExecutorService mExecutorService = null;
    private ScheduledFuture<?> mScheduledTaskFuture = null;

    interface OnPollingExecute {
        void onExecute();
    }

    private final Runnable executeRunnable = () -> {
        if (mExecutorService == null || mScheduledTaskFuture.isCancelled() || mScheduledTaskFuture.isDone()) {
            Log.w(TAG, String.format(Locale.getDefault(), "Task is stopped, isCanceled: %s, isDone: %s",
                    mScheduledTaskFuture.isCancelled() ? "true" : "false",
                    mScheduledTaskFuture.isDone() ? "true" : "false"));
            return;
        }

        if (!mLock.tryLock()) {
            Log.w(TAG, "Lock is hold by other thread");
            return;
        }
        try {
            if (mOnPollingExecute != null)
                mOnPollingExecute.onExecute();
        } finally {
            mLock.unlock();
        }
    };

    public PollingThread(OnPollingExecute onPollingExecute) {
        this(DEFAULT_POLLING_PERIOD_MS, onPollingExecute);
    }

    public PollingThread(long intervalPeriodMillis, OnPollingExecute onPollingExecute) {
        mOnPollingExecute = onPollingExecute;
        mIntervalPeriodMillis = intervalPeriodMillis;
    }

    // TODO: throw exceptions
    public void startPolling() {
        if (mExecutorService != null && mExecutorService.isShutdown())
            return;

        mExecutorService = Executors.newSingleThreadScheduledExecutor();
        resumePolling();
    }

    public void quitPolling() {
        if (mExecutorService == null)
            return;

        haltPolling();
        mExecutorService.shutdownNow();
    }

    // TODO: throw exceptions
    public void haltPolling() {
        if (mExecutorService == null || mScheduledTaskFuture == null || mScheduledTaskFuture.isCancelled())
            return;

        mLock.lock();
        try {
            mScheduledTaskFuture.cancel(true);
            mScheduledTaskFuture = null;
        } finally {
            mLock.unlock();
        }
    }

    // TODO: throw exceptions
    public void resumePolling() {
        if (mExecutorService == null || (mScheduledTaskFuture != null && !mScheduledTaskFuture.isCancelled()))
            return;

        mLock.lock();
        try {
            mScheduledTaskFuture = mExecutorService.scheduleWithFixedDelay(executeRunnable, mIntervalPeriodMillis, mIntervalPeriodMillis, TimeUnit.MILLISECONDS);
        } finally {
            mLock.unlock();
        }
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        if (mExecutorService == null || mScheduledTaskFuture == null)
            return false;

        return mLock.tryLock(time, unit);
    }

    public boolean tryLock() {
        if (mExecutorService == null || mScheduledTaskFuture == null)
            return false;

        return mLock.tryLock();
    }

    // TODO: throw exceptions
    public void lock() {
        if (mExecutorService == null || mScheduledTaskFuture == null)
            return;

        mLock.lock();
    }

    // TODO: throw exceptions
    public void unlock() {
        if (mExecutorService == null || mScheduledTaskFuture == null)
            return;

        mLock.unlock();
    }

}