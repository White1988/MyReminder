package reminder.android.myreminder;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RemindActivity extends AppCompatActivity {

    private static final int RQ_PERMISSION_WRITE_EXTERNAL_STORAGE = 1;
    Button btnSet;
    Button btnStop;
    Button btnStopNotif;
    EditText edtMessage;

    Vibrator vibrator;
    Ringtone ringtone;

    boolean createAlarm = true;

    private String m_Text = "";
    private final long[] mVibratePattern = { 0, 500, 500 };


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_remind);

        btnSet = (Button) findViewById(R.id.btnSet);
        edtMessage = (EditText) findViewById(R.id.etAlarm);
        btnStop = findViewById(R.id.stop);
        btnStopNotif = findViewById(R.id.btnStopSendingNotifications);

        btnStopNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlarm = false;
                stop();
            }
        });

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
                askCreateFilePermission();

                finish();
            }
        });
        edtMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

        Uri mAlarmSound = Uri.parse("DEFAULT_RINGTONE_URI");
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), mAlarmSound);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        play();
    }

    public void play() {
        ringtone.play();
        vibrator.vibrate(mVibratePattern, 0);
    }

    public void stop() {
        try {
            vibrator.cancel();
            ringtone.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeFile() {
        try
        {
            String filename= Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_ALARMS) + "/MessagesAlarms.txt";
            File file = new File(filename);
            if(!file.exists())
                file.createNewFile();

            FileWriter fw = new FileWriter(file,true); //the true will append the new data
            fw.write(edtMessage.getText().toString() + "\n");//appends the string to the file
            fw.close();
        }
        catch(IOException ioe)
        {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }

    private void askCreateFilePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(this, "Permission is needed to write file!", Toast.LENGTH_SHORT).show();
                //askCreateFilePermission();
            }

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    RQ_PERMISSION_WRITE_EXTERNAL_STORAGE);

            // RQ_PERMISSION_WRITE_EXTERNAL_STORAGE is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        } else {
            // Permission has already been granted
            writeFile();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RQ_PERMISSION_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    writeFile();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permisson denied, file wasn't created!", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    protected void onPause() {
        sendNewReminder();
        super.onPause();
    }



    @Override
    protected void onResume() {
        play();
        super.onResume();
    }

    public void sendNewReminder() {
        if(createAlarm) {
            SharedPreferences sharedPreferences = getSharedPreferences("AlarmPrefs", MODE_PRIVATE);
            int time = sharedPreferences.getInt(MainActivity.SHR_TIME, 0);
            Intent i = new Intent(RemindActivity.this, RemindReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);

            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time * 1000, pi);
        }
    }
}
