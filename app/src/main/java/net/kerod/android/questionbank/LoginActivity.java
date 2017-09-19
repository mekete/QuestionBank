package net.kerod.android.questionbank;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import net.kerod.android.questionbank.manager.SettingsManager;
import net.kerod.android.questionbank.model.AppUser;
import net.kerod.android.questionbank.utility.StringUtil;
import net.kerod.android.questionbank.widget.CustomView;
import net.kerod.android.questionbank.widget.toast.LoadToast;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    public static final String DEFAULT_PROFILE_PIC_URL = "";
    private static final String TAG = "LoginActivity";

    private static final int RESULT_CODE_FACEBOOK_SIGN_IN = 777;
    private static final int RESULT_CODE_GOOGLE_SIGN_IN = 999;
    private static final int ACTION_LOG_IN = 1;
    private static final int ACTION_SIGN_UP = 2;
    //
    private static int mUserActionLoginOrSignUp = ACTION_LOG_IN;
    private TextView mTxtvLogInOrSignUp;
    private TextView mTxtvDontHaveAccount;
    private Button mBtnnEmailLogin;
    private TextView mBtnnForgotPassword;
    //
    private TextView mStatusTextView;
    private FirebaseAuth mFirebaseAuth;
    private LoadToast mLoadToast;
    private View mMainContent;
    //
    @Nullable
    private FirebaseAuth.AuthStateListener mAuthListener;
    //
    private CallbackManager mFacebookCallbackManager;
    private GoogleApiClient mGoogleApiClient;
    //    private LoginButton mFacebookLoginButton;
    //private SignInButton mGoogleLoginButton;
    //
    private EditText mTxteEmailField;
    private EditText mTxtePasswordField;
    private EditText mTxtePasswordRepeatField;
    //
    private static final String FACEBOOK_PERMISSION_BIRTHDAY = "user_birthday";
    private static final String FACEBOOK_PERMISSION_FRIENDS = "user_friends";
    private static final String FACEBOOK_PERMISSION_EMAIL = "email";
    private static final String FACEBOOK_PERMISSION_PROFILE = "public_profile";
    //
    private static final String FACEBOOK_MAIN_FIELD_FACEBOOK_ID = "id";
    private static final String FACEBOOK_MAIN_FIELD_FIRST_NAME = "first_name";
    private static final String FACEBOOK_MAIN_FIELD_LAST_NAME = "last_name";
    private static final String FACEBOOK_MAIN_FIELD_PROFILE_PIC = "profile_pic";
    //
    private static final String FACEBOOK_GRAPH_FIELD_ID = "id";
    private static final String FACEBOOK_GRAPH_FIELD_EMAIL = "email";
    private static final String FACEBOOK_GRAPH_FIELD_NAME = "name";
    private static final String FACEBOOK_GRAPH_FIELD_GENDER = "gender";
    private static final String FACEBOOK_GRAPH_FIELD_BIRTHDAY = "birthday";
    private static final String FACEBOOK_GRAPH_FIELD_LOCATION = "location";
    //


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //
        mStatusTextView = findViewById(R.id.txtv_logged_in_user);
        mMainContent = findViewById(R.id.main_content);
        mLoadToast = LoadToast.createLoadToast(this, getString(R.string.loading));
        //
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                updateUserStatusUi(user);
            }
        };
        initFacebookLogin();
        initGoogleLogin();
        initEmailLogin();
    }


    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        // mLoadToast.success();
        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void initGoogleLogin() {

        // Configure sign-in to request the user's ID, email address, and basic  profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, connectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();


        // It is hardly possible to customize SignInButton to match our design, so use fake button for UI.
        findViewById(R.id.btnn_fake_google_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadToast.show();
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RESULT_CODE_GOOGLE_SIGN_IN);
            }
        });

    }


    private void initFacebookLogin() {
        // It is hardly possible to customize LoginButton to our design, so use fake button for UI.
        mFacebookCallbackManager = CallbackManager.Factory.create();
        final LoginButton facebookLoginButton = new LoginButton(this);
        findViewById(R.id.btnn_fake_facebook_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadToast.show();
                facebookLoginButton.performClick();
            }
        });
        facebookLoginButton.setReadPermissions(FACEBOOK_PERMISSION_PROFILE, FACEBOOK_PERMISSION_EMAIL, FACEBOOK_PERMISSION_EMAIL);
        facebookLoginButton.registerCallback(mFacebookCallbackManager, mFacebookLoginCallback);
    }

    @NonNull
    FacebookCallback<LoginResult> mFacebookLoginCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(@NonNull LoginResult loginResult) {
            final AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());

            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject jsonObject, @NonNull GraphResponse response) {
                    try {
                        Profile profile = Profile.getCurrentProfile();
                        String displayName = profile.getFirstName() + " " + profile.getLastName();
                        int profilePicSize = (int) getResources().getDimension(R.dimen.profile_user_photo_size);
                        String profilePicUrl = profile.getProfilePictureUri(profilePicSize, profilePicSize) + "";

                        loginWithAuthCredential(profile.getId(), displayName, profilePicUrl, credential);

                        //Bundle bundle = getFacebookGraphData(jsonObject);
                    } catch (Exception e) {
                        Log.e(TAG, "onCompleted: " + e);
                        e.printStackTrace();
                    }
                }
            });
            Bundle parameters = new Bundle();
            //parameters.putString("fields", "id,name,email,gender,birthday,first_name, last_name, email,gender, birthday, location");
            parameters.putString("fields", FACEBOOK_GRAPH_FIELD_ID + ", " + FACEBOOK_GRAPH_FIELD_NAME + ", " + FACEBOOK_GRAPH_FIELD_EMAIL);
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            updateUserStatusUi(null);
        }

        @Override
        public void onError(FacebookException error) {
            mLoadToast.error();
            updateUserStatusUi(null);

        }
    };

    private Bundle getFacebookGraphData(@NonNull JSONObject jsonObject) {

        try {

            Bundle bundle = new Bundle();
            String id = jsonObject.getString(FACEBOOK_MAIN_FIELD_FACEBOOK_ID);
            String profilePicUrl = "https://graph.facebook.com/" + id + "/picture?width=200&height=150";

            bundle.putString(FACEBOOK_MAIN_FIELD_FACEBOOK_ID, id);
            bundle.putString(FACEBOOK_MAIN_FIELD_PROFILE_PIC, profilePicUrl);
            bundle.putString(FACEBOOK_MAIN_FIELD_FIRST_NAME, jsonObject.optString(FACEBOOK_MAIN_FIELD_FIRST_NAME));
            bundle.putString(FACEBOOK_MAIN_FIELD_LAST_NAME, jsonObject.optString(FACEBOOK_MAIN_FIELD_LAST_NAME));
            //
            bundle.putString(FACEBOOK_GRAPH_FIELD_EMAIL, jsonObject.optString(FACEBOOK_GRAPH_FIELD_EMAIL));
            bundle.putString(FACEBOOK_GRAPH_FIELD_GENDER, jsonObject.optString(FACEBOOK_GRAPH_FIELD_GENDER));
            bundle.putString(FACEBOOK_GRAPH_FIELD_BIRTHDAY, jsonObject.optString(FACEBOOK_GRAPH_FIELD_BIRTHDAY));

            if (jsonObject.has(FACEBOOK_GRAPH_FIELD_LOCATION)) {
                bundle.putString(FACEBOOK_GRAPH_FIELD_LOCATION, jsonObject.getJSONObject(FACEBOOK_GRAPH_FIELD_LOCATION).optString("name"));
            }
            return bundle;
        } catch (JSONException e) {
            FirebaseCrash.report(e);
            FirebaseCrash.log("Just to see why it failed to log");

            Log.d(TAG, "Error parsing JSON");
            return null;
        }
    }

    private void initEmailLogin() {
        mTxtvLogInOrSignUp = findViewById(R.id.txtv_log_in_or_sign_up);
        mTxtvDontHaveAccount = findViewById(R.id.txtv_dont_have_account);
        mTxteEmailField = findViewById(R.id.txtv_login_email);
        mTxtePasswordField = findViewById(R.id.txtv_login_password);
        mTxtePasswordRepeatField = findViewById(R.id.txtv_login_repeat_password);
        mBtnnEmailLogin = findViewById(R.id.btnn_email_login);
        mBtnnForgotPassword = findViewById(R.id.btnn_forgot_password);
        mBtnnEmailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserActionLoginOrSignUp == ACTION_LOG_IN) {
                    emailSignIn(mTxteEmailField.getText().toString(), mTxtePasswordField.getText().toString());
                } else {
                    emailSignUp(mTxteEmailField.getText().toString(), mTxtePasswordField.getText().toString());
                }
            }
        });
        mTxtvLogInOrSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserActionLoginOrSignUp == ACTION_LOG_IN) {
                    mUserActionLoginOrSignUp = ACTION_SIGN_UP;
                    setUpForSignUp();
                } else if (mUserActionLoginOrSignUp == ACTION_SIGN_UP) {
                    mUserActionLoginOrSignUp = ACTION_LOG_IN;
                    setUpForLogIn();
                }
            }
        });
        mBtnnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPasswordBottomSheetDialog.createAndShow(LoginActivity.this, mLoadToast, mMainContent);

            }
        });

    }

    private void showDisplayNameInputDialog() {
        final EditText input = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.your_display_name))
                .setMessage(getString(R.string.it_can_be_your_actual_or_nick_name))
                .setView(input)
                .setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String displayName = input.getText().toString().trim();
                        if (!StringUtil.isNullOrEmpty(displayName)) {
                            SettingsManager.setDisplayName(displayName);
                        }
                        finish();
                    }
                })
                .setNegativeButton(getString(android.R.string.cancel), null)
                .show();
    }

    private void setUpForLogIn() {
        mTxtePasswordRepeatField.setVisibility(View.GONE);
        mBtnnForgotPassword.setVisibility(View.VISIBLE);
        mTxtvDontHaveAccount.setText(R.string.dont_have_account);
        mTxtvLogInOrSignUp.setText(R.string.sign_up_now);
        mBtnnEmailLogin.setText(R.string.log_in_now);
    }

    private void setUpForSignUp() {
        mTxtePasswordRepeatField.setVisibility(View.VISIBLE);
        mBtnnForgotPassword.setVisibility(View.GONE);
        mTxtvDontHaveAccount.setText(R.string.already_registered);
        mTxtvLogInOrSignUp.setText(R.string.log_in_now);
        mBtnnEmailLogin.setText(R.string.sign_up_now);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RESULT_CODE_GOOGLE_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleGoogleSignInResult(result);
            } else {
                mLoadToast.error();
                CustomView.makeSnackBar(mMainContent, "Sorry, Google log in failed\nresultCode :" + resultCode, CustomView.SnackBarStyle.WARNING).show();

            }
        } else {// if (requestCode == RESULT_CODE_FACEBOOK_SIGN_IN) {
            // Pass the activity result back to the Facebook SDK
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleGoogleSignInResult(@NonNull GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            loginWithAuthCredential(acct.getEmail(), acct.getDisplayName(), acct.getPhotoUrl() + "", credential);
            //--------------------------------------------------------------------------------------

            String idToken = acct.getIdToken();
            String authCode = acct.getServerAuthCode();
            //  now you can send this auth code to server and exchange for access/refresh/ID tokens.
            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
        } else {
            //Toast.makeText(LoginActivity.this, "auth_failed", Toast.LENGTH_SHORT).show();
            CustomView.makeSnackBar(mMainContent, "Sorry, Google log in failed\n", CustomView.SnackBarStyle.WARNING).show();
        }
    }


    private void facebookSignOut() {
        mFirebaseAuth.signOut();
        LoginManager.getInstance().logOut();
        updateUserStatusUi(null);
    }

    private void updateUserStatusUi(@Nullable FirebaseUser user) {
        if (user != null) {
            Log.e(TAG, "updateFacebookUI: " + " USER NOT NULLLLLL, so whay??? ");
            //mFacebookStatusTextView.setText((user.getDisplayName()));
            mStatusTextView.setText(user.getUid() + "\n" + user.getDisplayName());

            mLoadToast.error();
        } else {
            mLoadToast.success();
            mStatusTextView.setText(R.string.signed_out);

            Log.e(TAG, "updateFacebookUI: " + "Ohhhh USER is  NULLLLLL,  whay??? ");
        }
    }


    @NonNull
    GoogleApiClient.OnConnectionFailedListener connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            //Unresolvable error occurred and Google APIs (including Sign-In) will not be available.
        }
    };


    private void loginWithAuthCredential(final String userName, final String displayName, final String profilePicUrl, @NonNull final AuthCredential credential) {
        SettingsManager.setEmail(userName);
        SettingsManager.setDisplayName(displayName);


        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    registerAppUserIfNeeded(userUid, userName, displayName, profilePicUrl, credential.getProvider());

                } else {
                    FirebaseCrash.report(task.getException());
                    FirebaseCrash.log("Just to see why it failed to log");
                    mLoadToast.error();
                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void registerAppUserIfNeeded(@NonNull final String userUid, final String userName, final String displayName, final String profilePicUrl, final String providerName) {
        SettingsManager.setUserUid(userUid);
        SettingsManager.setEmail(userName);
        SettingsManager.setDisplayName(displayName);
        final DatabaseReference appUserReference = AppUser.getDatabaseReference(userUid);

        mLoadToast.show();
        appUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AppUser userFromServer = dataSnapshot.getValue(AppUser.class);
                if (userFromServer == null) {//the user logged in for the first time, so register her
                    AppUser appUser = new AppUser(userUid, userName, displayName, profilePicUrl, providerName);

                    appUserReference.setValue(appUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {//by default user is guest
                                SettingsManager.setUserRole(AppUser.USER_ROLE_APP_USER);
                                onBackPressed();
                            } else {
                                FirebaseCrash.report(new Exception("App user task registration failed!!!!"));
                                //we have to recheck this method every time login required
                            }
                        }
                    });
                } else {//user already registered and has role
                    onBackPressed();
                    SettingsManager.setUserRole(userFromServer.getRole());
                    Toast.makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mLoadToast.error();
            }
        });
    }


    public void googleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                // updateUI(false);
            }
        });
    }

    private void emailSignUp(@NonNull final String email, @NonNull String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateEmailPassword()) {
            CustomView.makeSnackBar(mMainContent, mSignUpErrorMessage, CustomView.SnackBarStyle.WARNING).show();
            return;
        }
        mLoadToast.show();

        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            mLoadToast.error();
                            if (task.getException() != null) {
                                CustomView.makeSnackBar(mMainContent, "Sorry, there is problem!\n" + task.getException().getMessage(), CustomView.SnackBarStyle.WARNING).show();
                            } else {
                                CustomView.makeSnackBar(mMainContent, getString(R.string.auth_unknown_problem_occurred), CustomView.SnackBarStyle.WARNING).show();
                                FirebaseCrash.report(new Exception("task.getException() is null. " + getString(R.string.auth_unknown_problem_occurred)));
                            }
                        } else {
                            registerAppUserIfNeeded(task.getResult().getUser().getUid(), email, email, DEFAULT_PROFILE_PIC_URL, AppUser.AUTH_PROVIDER_EMAIL);
                            mLoadToast.success();
                        }
                    }
                });
    }

    private void emailSignIn(@NonNull final String email, @NonNull String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateEmailPassword()) {
            CustomView.makeSnackBar(mMainContent, mSignUpErrorMessage, CustomView.SnackBarStyle.WARNING).show();
            return;
        }

        mLoadToast.show();

        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            mLoadToast.error();
                            mStatusTextView.setText("auth_failed");
                            Log.e(TAG, "signInWithEmail:failed", task.getException());
                            if (task.getException() != null) {
                                CustomView.makeSnackBar(mMainContent, "Sorry, there is problem!\n" + task.getException().getMessage(), CustomView.SnackBarStyle.WARNING).show();
                            } else {
                                CustomView.makeSnackBar(mMainContent, getString(R.string.auth_unknown_problem_occurred), CustomView.SnackBarStyle.WARNING).show();
                                FirebaseCrash.report(new Exception("task.getException() is null during login. " + getString(R.string.auth_unknown_problem_occurred)));
                            }
                            //
                        } else {
                            String userUid = task.getResult().getUser().getUid();
                            registerAppUserIfNeeded(userUid, email, email, DEFAULT_PROFILE_PIC_URL, AppUser.AUTH_PROVIDER_EMAIL);
                            mLoadToast.success();
                            onBackPressed();
                        }
                    }
                });
    }

    String mSignUpErrorMessage;

    private boolean validateEmailPassword() {
        boolean valid = true;
        mSignUpErrorMessage = "";
        String email = mTxteEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mSignUpErrorMessage = ("Empty email.");
            return false;
        } else {
            //mEmailField.setError(null);
        }

        String password = mTxtePasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mSignUpErrorMessage = ("Password is empty.");
            return false;
        } else {
            //mPasswordField.setError(null);
        }
        if (mUserActionLoginOrSignUp == ACTION_SIGN_UP) {
            String passwordRepeat = mTxtePasswordRepeatField.getText().toString();
            if (TextUtils.isEmpty(passwordRepeat) || !password.equals(passwordRepeat)) {
                mSignUpErrorMessage = ("Passwords do not match");
                return false;
            }
        }
        return valid;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    private static class ResetPasswordBottomSheetDialog {
        private static final String TAG = "ContactBottomSheetDialo";

        private ResetPasswordBottomSheetDialog(@NonNull final Context context, final LoadToast loadToast, final View mainContent) {
            final BottomSheetDialog dialog = new BottomSheetDialog(context);
            final View viewGroup = LayoutInflater.from(context).inflate(R.layout.dialog_reset_password, null);
            final EditText txteEmail = (EditText) viewGroup.findViewById(R.id.txte_reset_email);
            Button btnnReset = (Button) viewGroup.findViewById(R.id.btnn_reset_password);
            btnnReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = txteEmail.getText().toString().trim();
                    if (!StringUtil.isNullOrEmpty(email) && StringUtil.isValidEmail(email)) {
                        resetPassWord(context, email, loadToast, mainContent);
                        dialog.hide();
                    }
                }
            });
            dialog.setContentView(viewGroup);
            dialog.show();
        }

        public static void createAndShow(@NonNull Context context, final LoadToast loadToast, final View mainContent) {
            new ResetPasswordBottomSheetDialog(context, loadToast, mainContent);
        }

        private void resetPassWord(@NonNull final Context context, String email, final LoadToast loadToast, final View mainContent) {
            loadToast.show();
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        loadToast.success();
                        CustomView.makeSnackBar(mainContent, context.getString(R.string.password_reset_instruction_sent_via_email), CustomView.SnackBarStyle.SUCCESS).show();
                    } else {
                        Exception ex = task.getException();
                        loadToast.error();
                        CustomView.makeSnackBar(mainContent, context.getString(R.string.problem_resetting_password), CustomView.SnackBarStyle.ERROR).show();
                    }
                }
            });
        }
    }
}
