package net.kerod.android.questionbank;

import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;

import net.kerod.android.questionbank.manager.SettingManager;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onStart() {
        super.onStart();
        if (SettingManager.isFirstTimeLaunch()) {
            TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(new Intent(this, ExamListActivity.class))
                    .addNextIntent(new Intent(this, TempIntroActivity.class))
                    .startActivities();
        } else {
            Intent intent = new Intent(this, ExamListActivity.class);
            startActivity(intent);
        }
    }
}