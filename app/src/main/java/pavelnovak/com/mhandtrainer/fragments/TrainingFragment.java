package pavelnovak.com.mhandtrainer.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import pavelnovak.com.mhandtrainer.PresetRadioGroup;
import pavelnovak.com.mhandtrainer.PresetValueButton;
import pavelnovak.com.mhandtrainer.R;
import pavelnovak.com.mhandtrainer.model.Training;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingFragment extends Fragment implements SensorEventListener {

    private SensorManager msensorManager;

    private float[] rotationMatrix;
    private float[] accelData;
    private float[] magnetData;
    private float[] OrientationData;

    private String login;

    private ProgressBar progressBar;
    private TextView progressTextView;
    private Button button;
    private TextView timeView;
    private EditText comment;
    private PresetValueButton easy_button;
    private PresetValueButton norm_button;
    private PresetValueButton hard_button;
    private PresetRadioGroup mSetTimesRadioGroup;

    boolean beginAccelerometer = false;
    boolean inProcessAccelerometer = false;

    boolean runAcc = false;
    boolean runningTimer = false;
    private boolean wasRunning;

    boolean goToStatistic;

    private int maxCount = 0;

    private int milliseconds = 0;

    private Date start_date;

    int i = 0;

    boolean needRunTimer;

    String time;
    String type;

    public static interface SaveClickListener {
        void saveClicked(boolean clicked);
    }

    SaveClickListener saveClickListener;

    public TrainingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_training, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        saveClickListener = (SaveClickListener) activity;
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null){
            goToStatistic = false;

            milliseconds = 0;
            runningTimer = false;

            progressBar = view.findViewById(R.id.progress_bar);
            progressBar.setProgress(0);

            progressTextView = view.findViewById(R.id.progress_text_view);

            msensorManager = (SensorManager) this.getActivity().getSystemService(Context.SENSOR_SERVICE);

            rotationMatrix = new float[16];
            accelData = new float[3];
            magnetData = new float[3];
            OrientationData = new float[3];

            timeView = view.findViewById(R.id.timer);
            time = String.format("%d:%02d:%02d", 0, 0, 0);
            timeView.setText(time);

            comment = view.findViewById(R.id.comment_text_view);

            button = view.findViewById(R.id.start_button);
            mSetTimesRadioGroup = view.findViewById(R.id.preset_times_radio_group);
            easy_button = view.findViewById(R.id.preset_times_value_button_30);
            norm_button = view.findViewById(R.id.preset_times_value_button_45);
            hard_button = view.findViewById(R.id.preset_times_value_button_60);

            progressBar.setMax(100);
            progressBar.setSecondaryProgress(100);

            mSetTimesRadioGroup.clearCheck();

            mSetTimesRadioGroup.setOnCheckedChangeListener(new PresetRadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(View radioGroup, View radioButton, boolean isChecked, int checkedId) {
                    switch (checkedId){
                        case R.id.preset_times_value_button_30:
                            maxCount = getResources().getInteger(R.integer.times_30);
                            progressBar.setMax(maxCount);
                            progressBar.setSecondaryProgress(maxCount);
                            type = getResources().getString(R.string.easy);
                            progressTextView.setText(String.valueOf(0) + "/" + String.valueOf(maxCount));
                            break;
                        case R.id.preset_times_value_button_45:
                            maxCount = getResources().getInteger(R.integer.times_45);
                            progressBar.setMax(maxCount);
                            progressBar.setSecondaryProgress(maxCount);
                            progressTextView.setText(String.valueOf(0) + "/" + String.valueOf(maxCount));
                            type = getResources().getString(R.string.norm);
                            break;
                        case R.id.preset_times_value_button_60:
                            maxCount = getResources().getInteger(R.integer.times_60);
                            progressBar.setMax(maxCount);
                            progressBar.setSecondaryProgress(maxCount);
                            progressTextView.setText(String.valueOf(0) + "/" + String.valueOf(maxCount));
                            type = getResources().getString(R.string.hard);
                            break;
                        default:
                            break;
                    }
                }
            });

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!easy_button.isChecked() && !norm_button.isChecked() && !hard_button.isChecked()){
                        Toast.makeText(getActivity().getApplicationContext(), "Pleas, choose the type of training!", Toast.LENGTH_LONG).show();
                    } else {
                        if (button.getText().equals(getResources().getString(R.string.start))) {
                            runAcc = true;
                            runningTimer = true;
                            button.setText(getResources().getString(R.string.finish));
                            easy_button.setEnabled(false);
                            norm_button.setEnabled(false);
                            hard_button.setEnabled(false);
                            start_date = new Date();
                            runTimer(true);
                        } else if (button.getText().equals(getResources().getString(R.string.finish))) {
                            runAcc = false;
                            runningTimer = false;
                            comment.setEnabled(true);
                            button.setText(getResources().getString(R.string.save));
                        } else if (button.getText().equals(getResources().getString(R.string.save))) {
                            easy_button.setEnabled(true);
                            norm_button.setEnabled(true);
                            hard_button.setEnabled(true);
                            runningTimer = false;
                            runTimer(false);
                            String com = comment.getText().toString();
                            String durat = timeView.getText().toString();
                            int count = i;
                            addTraining(start_date, type, login, count, com, durat);
                            comment.setText("");
                            comment.setEnabled(false);
                            progressBar.setProgress(0);
                            runningTimer = false;
                            needRunTimer = true;
                            runTimer(false);
                            milliseconds = 0;
                            progressTextView.setText(String.valueOf(0) + "/" + String.valueOf(maxCount));
                            button.setText(getResources().getString(R.string.start));
                            if (saveClickListener != null) {
                                saveClickListener.saveClicked(true);
                            }
                        }
                    }
                }
            };

            button.setOnClickListener(listener);
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        msensorManager.registerListener(this, msensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI );
        msensorManager.registerListener(this, msensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI );
        if (!wasRunning) button.setText(getResources().getString(R.string.start));
    }

    @Override
    public void onPause() {
        super.onPause();
        msensorManager.unregisterListener(this);
        wasRunning = button.getText().toString().equals(getResources().getString(R.string.start));
        runAcc = false;
        comment.setText("");
        comment.setEnabled(false);
        progressBar.setProgress(0);
        mSetTimesRadioGroup.clearCheck();
        easy_button.setEnabled(true);
        norm_button.setEnabled(true);
        hard_button.setEnabled(true);
        progressTextView.setText("");
        needRunTimer = false;
    }



    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (runAcc) {
            loadNewSensorData(sensorEvent);
            SensorManager.getRotationMatrix(rotationMatrix, null, accelData, magnetData);
            SensorManager.getOrientation(rotationMatrix, OrientationData);

            if ((int) (OrientationData[2] * (180 / Math.PI)) == -90) {
                beginAccelerometer = true;
            }

            if (beginAccelerometer && (int) (OrientationData[2] * (180 / Math.PI)) == 90) {
                inProcessAccelerometer = true;
                beginAccelerometer = false;
                i++;
                progressTextView.setText(String.valueOf(i) + "/" + String.valueOf(maxCount));
                progressBar.setProgress(i);
                if (i == maxCount){
                    runAcc = false;
                    runningTimer = false;
                    button.setText(getResources().getString(R.string.save));
                    comment.setEnabled(true);
                }
            }

            if (inProcessAccelerometer && (int) (OrientationData[2] * (180 / Math.PI)) == -90) {
                inProcessAccelerometer = false;
                beginAccelerometer = true;
                i++;
                progressTextView.setText(String.valueOf(i) + "/" + String.valueOf(maxCount));
                progressBar.setProgress(i);
                if (i == maxCount){
                    runAcc = false;
                    runningTimer = false;
                    button.setText(getResources().getString(R.string.save));
                    comment.setEnabled(true);
                }
            }
        }
    }

    private void runTimer(Boolean needRunTimer){
        if (needRunTimer) {
            final Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    int minutes = (milliseconds / (100 * 60)) % 60;
                    int seconds = (milliseconds / 100) % 60;
                    int millisecs = milliseconds % 100;

                    String time = String.format("%d:%02d:%02d", minutes, seconds, millisecs);
                    timeView.setText(time);
                    if (runningTimer) {
                        milliseconds++;
                        if (milliseconds == (100 * 60)) {
                            runningTimer = false;
                            runAcc = false;
                            button.setText(getResources().getString(R.string.save));
                            comment.setEnabled(true);
                        }
                    }
                    handler.postDelayed(this, 1);
                }
            });
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void loadNewSensorData(SensorEvent event) {

        final int type = event.sensor.getType();

        if (type == Sensor.TYPE_ACCELEROMETER) {
            accelData = event.values.clone();
        }

        if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            magnetData = event.values.clone();
        }
    }

    public void setUserLogin(String login){
        this.login = login;
    }

    private void addTraining(Date start_date, String type, String login, int count, String comment, String duration){
        Training training = new Training();
        training.setType(type);
        training.setLoginUser(login);
        training.setComment(comment);
        training.setCount(count);
        training.setDuration(duration);
        training.setStartDate(start_date);
        training.save();
    }
}
