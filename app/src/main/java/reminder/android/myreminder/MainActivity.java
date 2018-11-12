package reminder.android.myreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String SHR_TIME = "time";
    EditText edtTime;
    Button start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.start);
        edtTime = findViewById(R.id.edtTime);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edtTime.getText().toString().isEmpty()) {
                    int time = Integer.parseInt(edtTime.getText().toString());
                    SharedPreferences preferences = getSharedPreferences("AlarmPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(SHR_TIME, time);
                    editor.apply();
                    editor.commit();
                    Intent i = new Intent(MainActivity.this, RemindReceiver.class);
                    PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
                    AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                    am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time * 1000, pi);
                }
                else
                    Toast.makeText(MainActivity.this, "Input seconds to start", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
