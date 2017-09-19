package net.kerod.android.questionbank.widget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import net.kerod.android.questionbank.R;
import net.kerod.android.questionbank.adapter.AvatarAdapter;
import net.kerod.android.questionbank.manager.SettingManager;
import net.kerod.android.questionbank.utility.Constants;

import agency.tango.materialintroscreen.SlideFragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileSlide extends SlideFragment {
    //private CheckBox mCkbxAcceptTerms;

//    public static final String CLASS_HIGH_SCHOOL = "HS";
//    public static final String CLASS_PREP_SOCIAL = "PS";
//    public static final String CLASS_PREP_NATURAL = "PN";
//    //
    private RecyclerView mRecyclerView;
    // private CircleImageView mCimgSelectedAvatar;
    private EditText mTxteName;
    private EditText mTxteEmail;
    private Button mBtnnSave;
    private RadioGroup mRadioGroup;

    //
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_edit_account, container, false);
        initComponents(view);
        initRecyclerView(view);
        return view;
    }


    @Override
    public int backgroundColor() {
        return R.color.custom_slide_background;
    }

    @Override
    public int buttonsColor() {
        return R.color.custom_slide_buttons;
    }


    @Override
    public String cantMoveFurtherErrorMessage() {
        return getString(R.string.error_message);
    }


    private void initComponents(View view) {
        mTxteName = (EditText) view.findViewById(R.id.txtv_display_name);
        mTxteEmail = (EditText) view.findViewById(R.id.txtv_email);
        mBtnnSave = (Button) view.findViewById(R.id.btnn_update);
        mTxteName.setText(SettingManager.getFirstName());
        mTxteEmail.setText(SettingManager.getEmail());
        mBtnnSave.setOnClickListener(actionSave);
        //
        mRadioGroup = (RadioGroup) view.findViewById(R.id.radg_class_group);
        String classGroup = SettingManager.getClassGroup();
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
                SettingManager.setDisplayName(mTxteName.getText().toString());
                SettingManager.setEmail(mTxteEmail.getText().toString());
                SettingManager.setClassGroup(mTxteName.getText().toString());

                int id = mRadioGroup.getCheckedRadioButtonId();
                if (id > 0) {
                    if (id == R.id.radb_high_school) {
                        SettingManager.setClassGroup(Constants.CLASS_HIGH_SCHOOL);
                    } else if (id == R.id.radb_prep_social) {
                        SettingManager.setClassGroup(Constants.CLASS_PREP_SOCIAL);
                    } else if (id == R.id.radb_prep_natural) {
                        SettingManager.setClassGroup(Constants.CLASS_PREP_NATURAL);
                    }
                }
                //finish();
            }
        }
    };

    private static final String TAG = "EditAccountActivity";

    void initRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recv_avatar_grid);
        int savedAvatarIndex = SettingManager.getAvatarIndex();
        final AvatarAdapter adapter = new AvatarAdapter(getActivity(), Constants.AVATAR_RESOURCE_IDS, savedAvatarIndex);
        AvatarAdapter.SelectCallback callback = new AvatarAdapter.SelectCallback() {
            @Override
            public void onAttempt(CircleImageView[] imageArray, int selectedIndex) {
                for (int index = 0; index < imageArray.length; index++) {
                    Log.e(TAG, "onAttempt index :: : " + index);
                    if (index == selectedIndex) {
                        imageArray[index].setBorderColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                        imageArray[index].setBorderWidth(5);
                        SettingManager.setAvatarIndex(index);
                    } else {
                        imageArray[index].setBorderColor(ContextCompat.getColor(getActivity(), R.color.brokenWhite));
                        imageArray[index].setBorderWidth(2);
                    }
                }
            }
        };
        adapter.setSelectCallback(callback);
        mRecyclerView.setAdapter(adapter);
    }

}