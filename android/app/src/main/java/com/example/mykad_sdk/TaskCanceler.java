package com.example.mykad_sdk;

import android.util.Log;

public class TaskCanceler implements Runnable {
    private final AsyncTaskExecutorService<String, String, String> task;
    private final int how;

    public TaskCanceler(AsyncTaskExecutorService<String, String, String> task, final int how) {
        this.task = task;
        this.how = how;
    }

    @Override
    public void run() {
        if (task.getStatus() == AsyncTaskExecutorService.Status.RUNNING) {
            task.cancel();
            if (how == 0x00) {
                Log.d("MyKAD_SDK", "Process Cancel by User");
            } else if (how == 0xFF) {
                Log.d("MyKAD_SDK", "Timeout reading process");
            }
        }

    }
}