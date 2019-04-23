package net.kerod.android.questionbank.model;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ServerTimestamp;

import net.kerod.android.questionbank.manager.SettingsManager;
import net.kerod.android.questionbank.utility.StringUtil;

import java.util.Comparator;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public abstract class FirebaseModel implements Comparator<FirebaseModel> {
    public static final long DEFAULT_QUERY_DATA_SIZE = 100L;

    public static final String FIELD_UID = "uid";
    public static final String FIELD_UPDATED_TIMESTAMP = "updatedDate";
    public static final String FIELD_CREATED_TIMESTAMP = "createdDate";
    public static final String FIELD_CREATED_BY = "createdBy";
    //public static final String FIELD_SORT_ORDER = "sortOrder";
    protected String uid;
    protected String createdBy = SettingsManager.getUserUid();
    protected String updatedBy = SettingsManager.getUserUid();
    @ServerTimestamp
    protected Timestamp updatedDate;
    @ServerTimestamp
    protected Timestamp createdDate;

    public FirebaseModel() {
    }

    public static FirebaseFirestoreSettings getFirestoreSettings() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        return settings;
    }

    @NonNull
    public static FirebaseFirestore getFirestoreInstance() {
        FirebaseFirestore instance = FirebaseFirestore.getInstance();
        instance.setFirestoreSettings(getFirestoreSettings());
        return instance;
    }

    @Exclude
    public abstract String getTitle();

    @Exclude
    public abstract String getSubTitle();

    //public abstract String toString();

    @Override
    public String toString() {
        if (StringUtil.isNullOrEmpty(super.toString())) {
            return "";
        }
        return super.toString();

    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * We may want to sort the models locally
     */
    @Override
    public int compare(@Nullable FirebaseModel current, @Nullable FirebaseModel other) {
        if (current == null || other == null) {
            return 0;
        }
        Timestamp lhsSortLetters = current.getUpdatedDate();
        Timestamp rhsSortLetters = other.getUpdatedDate();
        if (lhsSortLetters == null || rhsSortLetters == null) {
            return 0;
        }
        return lhsSortLetters.compareTo(rhsSortLetters);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FirebaseModel) {
            FirebaseModel other = (FirebaseModel) obj;
            if (uid.equals(other.getUid())) return true;
        }

        return false;
    }

    public Timestamp getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Timestamp updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

//
//    public interface OnViewModelItemClickListener<T extends FirebaseModel> {
//        void onItemClicked(T model);
//    }

}
