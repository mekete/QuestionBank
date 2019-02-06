package net.kerod.android.questionbank.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.kerod.android.questionbank.BuildConfig;

/**
 * Created by makata on 10/28/17.
 */

public class AppVersion {

    public static DatabaseReference getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference().child("appVersion").child("version"+ BuildConfig.VERSION_CODE);
    }
    public enum UpdateLevel{
        Critical, BigFeature, MinorUpgrade
    }


    Integer currentVersion;
    Integer latestVersionCode;
    String latestVersionName;
    String updateLevel;
    String updateSummary;
    //

    public Integer getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(Integer currentVersion) {
        this.currentVersion = currentVersion;
    }

    public Integer getLatestVersionCode() {
        return latestVersionCode;
    }

    public void setLatestVersionCode(Integer latestVersionCode) {
        this.latestVersionCode = latestVersionCode;
    }

    public String getLatestVersionName() {
        return latestVersionName;
    }

    public void setLatestVersionName(String latestVersionName) {
        this.latestVersionName = latestVersionName;
    }

    public String getUpdateLevel() {
        return updateLevel;
    }

    public void setUpdateLevel(String updateLevel) {
        this.updateLevel = updateLevel;
    }

    public String getUpdateSummary() {
        return updateSummary;
    }

    public void setUpdateSummary(String updateSummary) {
        this.updateSummary = updateSummary;
    }
}
