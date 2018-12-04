package com.example.android.workmanagerexample;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;

public class MainActivity extends AppCompatActivity {

    OneTimeWorkRequest simpleRequest;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Data data = new Data.Builder()
                .putString(MyWorker.EXTRA_TITLE, "Message from Activity!")
                .putString(MyWorker.EXTRA_TEXT, "Hi! I have come from activity.")
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .build();

        simpleRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                .setInputData(data)
                .setConstraints(constraints)
                .build();

        final PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(MyWorker.class, 5, TimeUnit.SECONDS)
                .addTag("periodic_work")
                .build();

        findViewById(R.id.simpleWorkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert WorkManager.getInstance() != null;
                WorkManager.getInstance().enqueue(simpleRequest);
            }
        });


        textView = findViewById(R.id.textView);

        if (WorkManager.getInstance() != null) {
            WorkManager.getInstance().getStatusById(simpleRequest.getId())
                    .observe(this, new Observer<WorkStatus>() {
                        @Override
                        public void onChanged(@Nullable WorkStatus workStatus) {
                            if (workStatus != null) {
                                textView.append("SimpleWorkRequest: " + workStatus.getState().name() + "\n");
                            }
                            if (workStatus != null && workStatus.getState().isFinished()) {
                                String message = workStatus.getOutputData().getString(MyWorker.EXTRA_OUTPUT_MESSAGE, "Default message");
                                textView.append("SimpleWorkRequest (Data): " + message);
                            }
                        }
                    });
        }

        final UUID workId = simpleRequest.getId();

        findViewById(R.id.cancelWorkButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WorkManager.getInstance().cancelWorkById(workId);
            }
        });
    }

}
