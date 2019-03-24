package com.ysered.notificationscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int JOB_ID = 0;

    private Button scheduleJobButton;
    private Button cancelJobsButton;
    private RadioGroup networkOptionsRadioGroup;
    private JobScheduler scheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scheduleJobButton = findViewById(R.id.scheduleButton);
        scheduleJobButton.setOnClickListener(this);

        cancelJobsButton = findViewById(R.id.cancelJobsButton);
        cancelJobsButton.setEnabled(false);
        cancelJobsButton.setOnClickListener(this);

        networkOptionsRadioGroup = findViewById(R.id.networkOptionsGroup);

        scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scheduleButton:
                scheduleJob();
                break;
            case R.id.cancelJobsButton:
                cancelJobs();
                break;
        }
    }

    private void scheduleJob() {
        ComponentName serviceName = new ComponentName(getPackageName(),
                NotificationJobService.class.getName());
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID, serviceName)
                .setRequiredNetworkType(getSelectedNetworkOption(networkOptionsRadioGroup))
                .build();
        scheduler.schedule(jobInfo);
        Toast.makeText(this, R.string.job_scheduled_message, Toast.LENGTH_SHORT).show();
        scheduleJobButton.setEnabled(false);
        cancelJobsButton.setEnabled(true);
    }

    private void cancelJobs() {
        if (scheduler != null) {
            scheduler.cancelAll();
            scheduler = null;
            Toast.makeText(this, "Jobs cancelled", Toast.LENGTH_SHORT).show();
            scheduleJobButton.setEnabled(true);
            cancelJobsButton.setEnabled(false);
        }
    }

    private int getSelectedNetworkOption(RadioGroup radioGroup) {
        int selectedNetworkOption;
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.anyButton:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY;
                break;

            case R.id.wifiButton:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED;
                break;

            default:
            case R.id.noneButton:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
                break;
        }
        return selectedNetworkOption;
    }
}
