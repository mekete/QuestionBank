package net.kerod.android.questionbank.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AppUser extends FirebaseModel {
    public static final String USER_HIGH_SCHOOL = "HighSchool";
    public static final String USER_PREP_NATURAL = "PrepNatural";
    public static final String USER_PREP_SOCIAL = "PrepSocial";
    //
    public static final String CURRENT_STATUS_INACTIVE = "INC";
    //
    public static final String USER_ROLE_ADMIN = "Admin";
    public static final String USER_ROLE_SPONSOR = "Sponsor";
    public static final String USER_ROLE_SCHOOL_ADMIN = "SchoolAdmin";
    public static final String USER_ROLE_APP_USER = "AppUser";
    public static final String USER_ROLE_ANONYMOUS = "Anonymous";
    //
    public static final String AUTH_PROVIDER_GOOGLE = "Google";
    public static final String AUTH_PROVIDER_FACEBOOK = "Facebook";
    public static final String AUTH_PROVIDER_EMAIL = "Email";
    //
    private String searchId;
    private String userName;
    private String displayName;
    private String role=USER_ROLE_APP_USER;
    private String classCategory=USER_HIGH_SCHOOL;
    private String currentStatus;
    private Long memberSince;
    //
   // private String classCategory=USER_HIGH_SCHOOL;
//    private String userRole=USER_ROLE_APP_USER;

    //
    public static DatabaseReference getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference().child("appUser");
    }

    public AppUser() { }

    public AppUser(String userName, String authMethod) {
        this(userName, authMethod, USER_ROLE_APP_USER, USER_PREP_NATURAL, USER_PREP_SOCIAL);
    }

    public AppUser(String userName, String authMethod, String role, String company, String currentStatus) {
        this.searchId = authMethod + "_" + userName;
        this.userName = userName;
        this.role = role;
        this.classCategory = company;
        this.currentStatus = currentStatus;
    }

    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getClassCategory() {
        return classCategory;
    }

    public void setClassCategory(String company) {
        this.classCategory = company;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
