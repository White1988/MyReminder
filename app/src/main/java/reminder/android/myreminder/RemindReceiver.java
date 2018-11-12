package reminder.android.myreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class RemindReceiver extends BroadcastReceiver {
    private String m_Text = "";
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "ALARM!!!!!!!!", Toast.LENGTH_LONG).show();
        Intent newIntent = new Intent(context, RemindActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(newIntent);
    }
}
