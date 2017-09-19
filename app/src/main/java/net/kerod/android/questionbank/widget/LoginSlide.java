package net.kerod.android.questionbank.widget;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
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
import com.google.firebase.database.DatabaseReference;

import net.kerod.android.questionbank.R;
import net.kerod.android.questionbank.manager.SettingsManager;
import net.kerod.android.questionbank.model.AppUser;

import agency.tango.materialintroscreen.SlideFragment;

public class LoginSlide extends SlideFragment {
    //private CheckBox mCkbxAcceptTerms;
    ///
    private static final int RESULT_CODE_FACEBOOK_SIGN_IN = 777;
    private static final int RESULT_CODE_GOOGLE_SIGN_IN = 999;
    private static final int ACTION_LOGIN = 1;
    private static final int ACTION_SIGN_UP = 2;
    private static int mUserActionLoginOrSignUp = ACTION_LOGIN;
    private TextView mTxtvLoginOrRegister;
    private TextView mTxtvDontHaveAccount;
    private Button mBtnnEmailLogin;
    private TextView mBtnnForgotPassword;
    //
    private static final String TAG = "LoginActivity";
    private View mParentView;
    private TextView mStatusTextView;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager mFacebookCallbackManager;
    private LoginButton mFacebookLoginButton;
    private SignInButton mGoogleLoginButton;
    @VisibleForTesting
    public ProgressDialog mProgressDialog;
    //
    private GoogleApiClient mGoogleApiClient;

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mPasswordRepeatField;

//    @Override
    public int backgroundColor() {
        return R.color.custom_slide_background;
    }
//
    @Override
    public int buttonsColor() {
        return R.color.custom_slide_buttons;
    }

    @Override
    public boolean canMoveFurther() {
       return  FirebaseAuth.getInstance().getCurrentUser()!=null;//
//       return true;//mCkbxAcceptTerms.isChecked();
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return "Please login to continue";
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.content_login, container, false);
       // mCkbxAcceptTerms = (CheckBox) view.findViewById(R.id.checkBox);
        mStatusTextView = (TextView) view.findViewById(R.id.txtv_logged_in_user);
        mParentView = view.findViewById(R.id.content_login);
        //
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.e(TAG, "\n\n\n>>>>>>>>onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.e(TAG, "\n\n\n>>>>>>>>onAuthStateChanged:signed_out");
                }
                updateUserStatusUi(user);
            }
        };
//        initFacebookLogin(view);
//        initGoogleLogin(view);
//        initEmailLogin(view);
        return view;
    }

//    //http://stackoverflow.com/questions/16767672/key-hash-doesnt-match-while-facebook-login-in-android
//    void logDigest() {
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo("net.kerod.android.questionbank", PackageManager.GET_SIGNATURES);
//
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String ccc = new String(Base64.encode(md.digest(), 0));
//                Log.e(TAG, "\n\nlogDigests: >>>> " + ccc);
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//    }

    private void initGoogleLogin(View view) {

        // Configure sign-in to request the user's ID, email address, and basic  profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), connectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();


        // It is hardly possible to customize SignInButton to our design, so use fake button for UI.
        mGoogleLoginButton = new SignInButton(getActivity());
        view.findViewById(R.id.btnn_fake_google_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "\n\n\n>>>>>>>>onClick: " + "\n\n>>>>>>> :::: btnn_google_login clicked ");

                googleSignIn();
            }
        });

//        mGoogleLoginButton=(SignInButton) findViewById(R.id.btnn_google_login);
//        mGoogleLoginButton.setSize(SignInButton.SIZE_STANDARD);
        //mGoogleLoginButton.setScopes(signInOptions.getScopeArray());
        //
        // Button listeners
        mGoogleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        Log.e(TAG, "\n\n\n>>>>>>>>signIn: " + "\n::::: >>>>>>>>>>>>>>>>> 11111 signin clicked before startActivityForResult");
        startActivityForResult(signInIntent, RESULT_CODE_GOOGLE_SIGN_IN);
        Log.e(TAG, "\n\n\n>>>>>>>>signIn: " + "\n::::: >>>>>>>>>>>>>>>>> 22222 signin clicked after calling startActivityForResult");

    }

    private void initFacebookLogin(View view) {
        // It is hardly possible to customize LoginButton to our design, so use fake button for UI.
        mFacebookLoginButton = new LoginButton(getActivity());
        view. findViewById(R.id.btnn_fake_facebook_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFacebookLoginButton.performClick();
            }
        });
        mFacebookCallbackManager = CallbackManager.Factory.create();
        mFacebookLoginButton.setBackgroundResource(R.drawable.bg_login_google);
//        mFacebookLoginButton.setReadPermissions("email", "public_profile");
        mFacebookLoginButton.setReadPermissions("public_profile");
        mFacebookLoginButton.registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "\n\n\n>>>>>>>>facebook:onSuccess:" + loginResult);
                AccessToken token = loginResult.getAccessToken();
                //loginResult.getRecentlyGrantedPermissions();
                AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

                loginWithAuthCredential(credential);
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "\n\n\n>>>>>>>>facebook:onCancel");
                updateUserStatusUi(null);
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "\n\n\n>>>>>>>>facebook:onError" + error);
                updateUserStatusUi(null);

            }
        });
    }

    private void initEmailLogin(View view) {
        mTxtvLoginOrRegister = null;//(TextView) view.findViewById(R.id.txtv_register_login);
        mTxtvDontHaveAccount = (TextView) view.findViewById(R.id.txtv_dont_have_account);
        mEmailField = (EditText) view.findViewById(R.id.txtv_login_email);
        mPasswordField = (EditText) view.findViewById(R.id.txtv_login_password);
        mPasswordRepeatField = (EditText) view.findViewById(R.id.txtv_login_repeat_password);
        mBtnnEmailLogin = (Button) view.findViewById(R.id.btnn_email_login);
        mBtnnForgotPassword = (TextView) view.findViewById(R.id.btnn_forgot_password);
        mBtnnEmailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserActionLoginOrSignUp == ACTION_LOGIN) {
                    emailSignIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
                } else {
                    emailRegister(mEmailField.getText().toString(), mPasswordField.getText().toString());
                }

            }
        });
        mTxtvLoginOrRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserActionLoginOrSignUp == ACTION_LOGIN) {
                    mUserActionLoginOrSignUp = ACTION_SIGN_UP;
                    //now, it is changed it to sign up
                    mPasswordRepeatField.setVisibility(View.VISIBLE);
                    mBtnnForgotPassword.setVisibility(View.GONE);
                    mTxtvDontHaveAccount.setText("Already registered? ");
                    mTxtvLoginOrRegister.setText("Log in Now");
                    mBtnnEmailLogin.setText("Sign up Now");
                } else {
                    mUserActionLoginOrSignUp = ACTION_LOGIN;
                    //now, it is changed it to log in
                    mPasswordRepeatField.setVisibility(View.GONE);
                    mBtnnForgotPassword.setVisibility(View.VISIBLE);
                    mTxtvDontHaveAccount.setText("Don't have account? ");
                    mTxtvLoginOrRegister.setText("Sign up Now");
                    mBtnnEmailLogin.setText("Log in Now");

                }
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RESULT_CODE_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);

            //} else if (requestCode == RESULT_CODE_FACEBOOK_SIGN_IN) {
        } else {// if (requestCode == RESULT_CODE_FACEBOOK_SIGN_IN) {
            // Pass the activity result back to the Facebook SDK
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {


        Log.e(TAG, "\n\n\n>>>>>>>>handleSignInResult>>>>>>>>>>>>> 1111:" + result.isSuccess());
        if (result.isSuccess()) {
            Log.e(TAG, "\n\n\n>>>>>>>>handleSignInResult>>>>>>>>>>>>> 2222:" + result.isSuccess());
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            loginWithAuthCredential(credential);
            //--------------------------------------------------------------------------------------

            String idToken = acct.getIdToken();
            String authCode = acct.getServerAuthCode();
            //  now you can send this auth code to server and exchange for access/refresh/ID tokens.
            String displayName = acct.getGivenName() + " " + acct.getFamilyName();
            Log.e(TAG, "\n\n\n>>>>>>>>handleSignInResult>>>>>>>>>>>>> 3333:" +
                    "\n acct ::" + acct + "" +
                    "\n authCode ::" + authCode + "" +
                    "\n idToken ::" + idToken + "" +
                    "\n acct.getEmail() ::" + acct.getEmail() + "" +
                    "");

            mStatusTextView.setText(getString(R.string.signed_in_fmt, displayName));
            // updateUI(true);
            Log.e(TAG, "\n\n\n>>>>>>>>handleSignInResult>>>>>>>>>>>>> 4444:" + result.isSuccess());
            //now we set
            SettingsManager.setEmail(acct.getEmail());
            SettingsManager.setDisplayName(acct.getGivenName());
            SettingsManager.setFirstName(acct.getGivenName());
            SettingsManager.setLastName(acct.getFamilyName());
            //


        } else {
            //Toast.makeText(getActivity(), "auth_failed", Toast.LENGTH_SHORT).show();
            CustomView.makeSnackBar(mParentView, "Sorry, Google log in failed", CustomView.SnackBarStyle.WARNING).show();
        }
    }


    private void facebookSignOut() {
        mFirebaseAuth.signOut();
        LoginManager.getInstance().logOut();
        updateUserStatusUi(null);
    }

    private void updateUserStatusUi(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            Log.e(TAG, "updateFacebookUI: " + " USER NOT NULLLLLL, so whay??? ");
            //mFacebookStatusTextView.setText((user.getDisplayName()));
            mStatusTextView.setText(user.getUid() + "\n" + user.getDisplayName());

            //mFacebookLoginButton.setVisibility(View.GONE);
            //mFacebookLogoutButton.setVisibility(View.VISIBLE);
        } else {
            // mFacebookStatusTextView.setText(R.string.signed_out);
            mStatusTextView.setText(R.string.signed_out);

            //mFacebookLoginButton.setVisibility(View.VISIBLE);
            Log.e(TAG, "updateFacebookUI: " + "Ohhhh USER is  NULLLLLL,  whay??? ");
            //mFacebookLogoutButton.setVisibility(View.GONE);
        }
    }


    GoogleApiClient.OnConnectionFailedListener connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            //Unresolvable error occurred and Google APIs (including Sign-In) will not be available.
            Log.e(TAG, "\n\n\n>>>>>>>>onConnectionFailed:" + connectionResult);
        }
    };


    private void loginWithAuthCredential(final AuthCredential credential) {
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                hideProgressDialog();
                Log.d(TAG, "\n\n\n>>>>>>>>signInWithCredential:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.w(TAG, "\n\n\n>>>>>>>>signInWithCredential", task.getException());
                    Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_LONG).show();
                    //finish();
                } else {
                    Toast.makeText(getActivity(), "Nice.", Toast.LENGTH_SHORT).show();
                    //registerUserProfile( credential.getemail,   credential.getProvider(), String fullName)
                    //finish();
                }
            }
        });
    }

    private void registerUserProfile(String email, String authProvider, String fullName, int grade) {
//        save the user to our custom table so we may manipulate her/his role
        DatabaseReference mPostTableRef = AppUser.getDatabaseReference();
        AppUser appUser = new AppUser(email, authProvider);
        mPostTableRef/*.push()*/.setValue(appUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(GoogleSignInActivity.this, "", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void googleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                // updateUI(false);
            }
        });
    }

    private void emailRegister(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateEmailPassword()) {
            CustomView.makeSnackBar(mParentView, mSignUpErrorMessage, CustomView.SnackBarStyle.WARNING).show();
            return;
        }

        showProgressDialog();

        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "\n\n\n\nonComplete::: email login failed:::: " + task.getException());
                            //Toast.makeText(getActivity(), "auth_failed", Toast.LENGTH_SHORT).show();
                            CustomView.makeSnackBar(
                                    mParentView, "Auth failed", CustomView.SnackBarStyle.ERROR).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void emailSignIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateEmailPassword()) {
            CustomView.makeSnackBar(mParentView, mSignUpErrorMessage, CustomView.SnackBarStyle.WARNING).show();
            return;
        }

        showProgressDialog();

        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            mStatusTextView.setText("auth_failed");
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(getActivity(), "auth_failed", Toast.LENGTH_SHORT).show();
                            CustomView.makeSnackBar(mParentView, "Auth failed", CustomView.SnackBarStyle.WARNING).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

    String mSignUpErrorMessage;

    private boolean validateEmailPassword() {
        boolean valid = true;
        mSignUpErrorMessage = "";
        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mSignUpErrorMessage = ("Empty email.");
            return false;
        } else {
            //mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mSignUpErrorMessage = ("Password is empty.");
            return false;
        } else {
            //mPasswordField.setError(null);
        }
        if (mUserActionLoginOrSignUp == ACTION_SIGN_UP) {
            String passwordRepeat = mPasswordRepeatField.getText().toString();
            if (TextUtils.isEmpty(passwordRepeat) || !password.equals(passwordRepeat)) {
                mSignUpErrorMessage = ("Passwords do not match");
                return false;
            } else {
                //mPasswordField.setError(null);
            }
        }
        return valid;
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}