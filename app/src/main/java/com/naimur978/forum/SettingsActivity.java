package com.naimur978.forum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class SettingsActivity extends AppCompatActivity {

    SwitchCompat postSwitch;

    //use shared preferences to save the state of switch
    SharedPreferences sp;
    SharedPreferences.Editor editor;//to edit value of shared preferences

    //constant for topic
    private static final String TOPIC_POST_NOTIFICATION = "POST"; //assign any value but use same for this kind of notifications

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        postSwitch = findViewById(R.id.action_settings);

        //init sp
        sp = getSharedPreferences("Notification_SP", MODE_PRIVATE);
        boolean isPostEnabled = sp.getBoolean("" + TOPIC_POST_NOTIFICATION, false);


        //if enabled check switch, otherwise uncheck switch - by default unchecked
        if (isPostEnabled) {
            postSwitch.setChecked(true);
        } else {
            postSwitch.setChecked(false);
        }

        //implement switch change listener
        postSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                //edit switch state
                editor = sp.edit();
                editor.putBoolean("" + TOPIC_POST_NOTIFICATION, b);
                editor.apply();

                if (b) {
                    subscribePostNotification(); //call to subscribe
                } else {
                    unsubscribePostNotification(); //call to unsubscribe

                }
            }
        });
    }

    private void subscribePostNotification() {
        //subscribe to a topic to enable it's notification
        FirebaseMessaging.getInstance().subscribeToTopic(""+TOPIC_POST_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "You will receive post notifications";
                        if(!task.isSuccessful()){
                            msg = "Subscription failed";

                        }
                        Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void unsubscribePostNotification() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(""+TOPIC_POST_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "You will not receive post notifications";
                        if(!task.isSuccessful()){
                            msg = "Un-Subscription failed";

                        }
                        Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}

