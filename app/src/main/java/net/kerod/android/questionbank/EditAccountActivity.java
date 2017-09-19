package net.kerod.android.questionbank;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import net.kerod.android.questionbank.adapter.AvatarAdapter;
import net.kerod.android.questionbank.manager.SettingsManager;
import net.kerod.android.questionbank.utility.Constants;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditAccountActivity extends AppCompatActivity {

    //
    private RecyclerView mRecyclerView;
    // private CircleImageView mCimgSelectedAvatar;
    private EditText mTxteName;
    private EditText mTxteEmail;
    private Button mBtnnSave;
    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        initComponents();
        initRecyclerView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
    private void initComponents() {
        mTxteName = (EditText) findViewById(R.id.txtv_display_name);
        mTxteEmail = (EditText) findViewById(R.id.txtv_email);
        mBtnnSave = (Button) findViewById(R.id.btnn_update);
         mTxteName.setText(SettingsManager.getFirstName());
        mTxteEmail.setText(SettingsManager.getEmail());
        mBtnnSave.setOnClickListener(actionSave);
        //
        mRadioGroup = (RadioGroup) findViewById(R.id.radg_class_group);
        String classGroup = SettingsManager.getClassGroup();
        if (Constants.CLASS_HIGH_SCHOOL.equals(classGroup)) {
            mRadioGroup.check(R.id.radb_high_school);
        } else if (Constants.CLASS_PREP_SOCIAL.equals(classGroup)) {
            mRadioGroup.check(R.id.radb_prep_social);
        } else if (Constants.CLASS_PREP_NATURAL.equals(classGroup)) {
            mRadioGroup.check(R.id.radb_prep_natural);
        }
    }

    View.OnClickListener actionSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (true) {
                SettingsManager.setDisplayName(mTxteName.getText().toString());
                SettingsManager.setEmail(mTxteEmail.getText().toString());
                SettingsManager.setClassGroup(mTxteName.getText().toString());

                int id = mRadioGroup.getCheckedRadioButtonId();
                if (id > 0) {
                    if (id == R.id.radb_high_school) {
                        SettingsManager.setClassGroup(Constants.CLASS_HIGH_SCHOOL);
                    } else if (id == R.id.radb_prep_social) {
                        SettingsManager.setClassGroup(Constants.CLASS_PREP_SOCIAL);
                    } else if (id == R.id.radb_prep_natural) {
                        SettingsManager.setClassGroup(Constants.CLASS_PREP_NATURAL);
                    }
                }
                finish();
            }
        }
    };
//    public static final int[] mAvatarList = {
//            R.drawable.avatar_1,
//            R.drawable.avatar_2,
//            R.drawable.avatar_3,
//            R.drawable.avatar_4,
//            R.drawable.avatar_5,
//            R.drawable.avatar_6,
//            R.drawable.avatar_7,
//            R.drawable.avatar_8,
//            R.drawable.avatar_9,
//            R.drawable.avatar_10,
//            R.drawable.avatar_11,
//            R.drawable.avatar_12,
//            R.drawable.avatar_13,
//            R.drawable.avatar_14,
//            R.drawable.avatar_15,
//            // R.drawable.avatar_16,
//    };
    private static final String TAG = "EditAccountActivity";

    void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recv_avatar_grid);
        int savedAvatarIndex= SettingsManager.getAvatarIndex();
        final AvatarAdapter adapter = new AvatarAdapter(this, Constants.AVATAR_RESOURCE_IDS,   savedAvatarIndex);
        AvatarAdapter.SelectCallback callback = new AvatarAdapter.SelectCallback() {
            @Override
            public void onAttempt(CircleImageView[] imageArray, int selectedIndex) {
                 for (int index = 0; index < imageArray.length; index++) {
                    Log.e(TAG, "onAttempt index :: : " + index);
                    if (index == selectedIndex) {
                        imageArray[index].setBorderColor(ContextCompat.getColor(EditAccountActivity.this,R.color.colorAccent));
                        imageArray[index].setBorderWidth(5);
                        SettingsManager.setAvatarIndex(index);
                    } else {
                        imageArray[index].setBorderColor(ContextCompat.getColor(EditAccountActivity.this,R.color.brokenWhite));
                        imageArray[index].setBorderWidth(2);
                    }
                }
            }
        };
        adapter.setSelectCallback(callback);
        mRecyclerView.setAdapter(adapter);
    }

}
