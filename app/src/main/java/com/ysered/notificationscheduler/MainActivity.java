package com.ysered.notificationscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int JOB_ID = 0;

    private RadioGroup networkOptionsRadioGroup;
    private JobScheduler scheduler;
    private Switch idleSwitch;
    private Switch chargingSwitch;
    private TextView deadlineText;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button scheduleJobButton = findViewById(R.id.scheduleButton);
        Button cancelJobsButton = findViewById(R.id.cancelJobsButton);
        networkOptionsRadioGroup = findViewById(R.id.networkOptionsGroup);
        idleSwitch = findViewById(R.id.idleSwitch);
        chargingSwitch = findViewById(R.id.chargingSwitch);
        deadlineText = findViewById(R.id.deadlineText);
        seekBar = findViewById(R.id.seekBar);

        scheduleJobButton.setOnClickListener(this);
        cancelJobsButton.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateDeadlineLabel(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        updateDeadlineLabel(seekBar.getProgress());
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
        int selectedNetworkOption = getSelectedNetworkOption(networkOptionsRadioGroup);
        int deadlineSeconds = seekBar.getProgress();
        boolean isDeadlineSet = deadlineSeconds > 0;

        boolean constraintSet = selectedNetworkOption != JobInfo.NETWORK_TYPE_NONE
                || idleSwitch.isChecked()
                || chargingSwitch.isChecked()
                || isDeadlineSet;

        if (constraintSet) {
            ComponentName serviceName = new ComponentName(getPackageName(),
                    NotificationJobService.class.getName());

            JobInfo jobInfo = new JobInfo.Builder(JOB_ID, serviceName)
                    .setRequiredNetworkType(selectedNetworkOption)
                    //.setOverrideDeadline(deadlineSeconds)
                    //.setRequiresDeviceIdle(true) //idleSwitch.isChecked()
                    .setRequiresCharging(true) //chargingSwitch.isChecked()
                    .build();

            scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            scheduler.schedule(jobInfo);

            Toast.makeText(this, R.string.job_scheduled_message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.set_constraints, Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelJobs() {
        if (scheduler != null) {
            scheduler.cancelAll();
            scheduler = null;
            Toast.makeText(this, R.string.jobs_cancelled, Toast.LENGTH_SHORT).show();
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

    private void updateDeadlineLabel(int progress) {
        String head = getString(R.string.override_deadline);
        String tail;
        if (progress == 0) {
            tail = " " + getString(R.string.not_set);
        } else {
            tail = " " + progress + " " + getString(R.string.seconds);
        }
        String text = head + tail;
        deadlineText.setText(text);
    }
}
