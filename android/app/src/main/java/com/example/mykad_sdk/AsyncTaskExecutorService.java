package com.example.mykad_sdk;


import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/** This is a temporary solution to the deprecation of Async Task
 *  Replacement Of Deprecated AsyncTask In Android
 *  Jignesh Mundhava
 *  https://techblogs.42gears.com/replacement-of-deprecated-asynctask-in-android
 *  TODO: Make a proper move to HandlerThread for any AsyncTask
 **/

public abstract class AsyncTaskExecutorService < Params, Progress, Result > {
    public static enum Status{
        FINISHED,
        PENDING,
        RUNNING
    }

    private final ExecutorService executor;
    private Handler handler;
    private final AtomicBoolean isCancel = new AtomicBoolean(false);
    private final AtomicReference<Status> statusAtomicReference = new AtomicReference<>(Status.PENDING);
    private Future<?> runningThreadFuture = null;

    protected AsyncTaskExecutorService() {
        executor = Executors.newSingleThreadExecutor((Runnable r) -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public Handler getHandler() {
        if (handler == null) {
            synchronized(AsyncTaskExecutorService.class) {
                handler = new Handler(Looper.getMainLooper());
            }
        }
        return handler;
    }

    protected void onPreExecute() {
        // Override this method wherever you want to perform task before background execution get started
    }

    protected abstract Result doInBackground(Params params);

    protected abstract void onPostExecute(Result result);

    protected abstract void onCancelled(Result result);

    //    protected void onProgressUpdate(@NotNull Progress value) {
    protected void onProgressUpdate(Progress value) {
        // Override this method whenever you want update a progress result
    }

    // used for push progress resport to UI
//    public void publishProgress(@NotNull Progress value) {
    public void publishProgress(Progress value) {
        getHandler().post(() -> onProgressUpdate(value));
    }

    public void execute() {
        execute(null);
    }

    public void execute(Params params) {
        getHandler().post(() -> {
            statusAtomicReference.set(Status.RUNNING);
            onPreExecute();
            if(isCancel.get())
                onCancelled(null);
            runningThreadFuture = executor.submit(() -> {
                Result result = doInBackground(params);
                getHandler().post(() -> {
                    if(isCancelled())
                        onCancelled(result);
                    else
                        onPostExecute(result);

                    statusAtomicReference.set(Status.FINISHED);
                });
            });
        });
    }

    public void cancel() {
        if (runningThreadFuture != null) {
            runningThreadFuture.cancel(true);
        }
        isCancel.set(true);
    }

    public void shutdown(){
        if (executor != null)
            executor.shutdownNow();
    }

    public Status getStatus(){
        return statusAtomicReference.get();
    }

    public boolean isCancelled() {
        return runningThreadFuture == null ? isCancel.get(): runningThreadFuture.isCancelled();
    }

    public boolean isShutdown() {
        return executor == null || executor.isTerminated() || executor.isShutdown();
    }
}