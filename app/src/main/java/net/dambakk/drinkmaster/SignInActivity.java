package net.dambakk.drinkmaster;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.dambakk.drinkmaster.util.Keys;

/**
 * Created by chris on 01.08.2016.
 */
public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {


    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private SignInButton mSignInButton;
    private LoginButton mLoginButton;
    private Button mLoginLocalButton;
    private Button mSignUpButton;

    private EditText txtEmail;
    private EditText txtPsw;
    private TextView txtWarningPsw;

    private CallbackManager callbackManager;

    private GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference ref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Basic initialisation:

        setContentView(R.layout.activity_sign_in);
        getSupportActionBar().hide();
        ref = FirebaseDatabase.getInstance().getReference();

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        txtEmail = (EditText) findViewById(R.id.txt_login_email);
        txtPsw = (EditText) findViewById(R.id.txt_login_psw);
        txtWarningPsw = (TextView) findViewById(R.id.txt_warning_weak_psw);
        txtWarningPsw.setVisibility(View.INVISIBLE);

        //End of initialisation.


        //Local login logic:

        mLoginLocalButton = (Button) findViewById(R.id.btn_login);
        mSignUpButton = (Button) findViewById(R.id.btn_signup);
        mLoginLocalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = txtEmail.getText().toString();
                final String psw = txtPsw.getText().toString();

                txtWarningPsw.setVisibility(View.INVISIBLE);


                if(!email.isEmpty() && !psw.isEmpty()) {

                    mFirebaseAuth.signInWithEmailAndPassword(email, psw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                //Log in failed
                                Toast.makeText(getApplicationContext(), R.string.txtLoginFailed, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = txtEmail.getText().toString();
                final String psw = txtPsw.getText().toString();

                if(!email.isEmpty() && !psw.isEmpty()){
                    if(!isGoodPsw(psw)){
                        txtWarningPsw.setVisibility(View.VISIBLE);
                        return;
                    }
                    else{
                        txtWarningPsw.setVisibility(View.INVISIBLE);
                    }
                    mFirebaseAuth.createUserWithEmailAndPassword(email, psw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //I AM HERE!
                                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), R.string.txtSignUpFailedUserExists, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        //End of local login


        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button_google);
        mSignInButton.setOnClickListener(this);


        //Facebook login:
        mLoginButton = (LoginButton) findViewById(R.id.login_button_facebook);
        mLoginButton.setEnabled(false);
        mLoginButton.setReadPermissions("email", "public_profile");
        mLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(), "FB-login success!", Toast.LENGTH_LONG).show();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "FB-login canceled!", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "FB-login error!", Toast.LENGTH_LONG).show();

            }
        });

        //End of Facebook login


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if(user != null){
                    //User is signed in
                    Log.d(TAG, "User is signed in.");
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    finish();
                }else{
                    //User us NOT signed in
                    Log.d(TAG, "User is NOT signed in");
                }
            }
        };


    }

    private void handleFacebookAccessToken(final AccessToken accessToken) {
        Log.d(TAG, "handleFacebookAccessToken. Token: " + accessToken);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    //Create user in database:
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    ref.child(Keys.DB_USER).child(accessToken.getUserId()).child(Keys.DB_USER_EMAIL).setValue("email");
                    ref.child(Keys.DB_USER).child(accessToken.getUserId()).child(Keys.DB_USER_NAME).setValue("name");
                    ref.child(Keys.DB_USER).child(accessToken.getUserId()).child(Keys.DB_USER_SHOW_TERMS).setValue(true);


                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                    finish(); //Close current screen.
                }else{
                    Log.w(TAG, "Log in with Facebook failed. Exception: " + task.getException());
                    Toast.makeText(getApplicationContext(), "Log in with Facebook failed. Try again. ", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.sign_in_button_google:
                signInGoogle();
                break;
        }

    }

    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount acc = result.getSignInAccount();
                firebaseAuthWithGoogle(acc);
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acc) {
        Log.d(TAG, "FirebaseAuthWithGoogle: " + acc.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Log.d(TAG, "Log in with Google complete: " + task.isComplete() + ", Success: " + task.isSuccessful());
                        if(task.isSuccessful()){

                            //Create user in database:
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            ref.child(Keys.DB_USER).child(acc.getId()).child(Keys.DB_USER_EMAIL).setValue(acc.getEmail());
                            ref.child(Keys.DB_USER).child(acc.getId()).child(Keys.DB_USER_NAME).setValue(acc.getDisplayName());
                            ref.child(Keys.DB_USER).child(acc.getId()).child(Keys.DB_USER_SHOW_TERMS).setValue(true);


                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                            finish(); //Close current screen.
                        }else{
                            Log.w(TAG, "Log in with google failed. Exception: " + task.getException());
                            Toast.makeText(getApplicationContext(), "Log in with Google failed. Try again. ", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }


    private boolean isGoodPsw(String p){
        if(p.length() >= 8){
            return true;
        }
        return false;
    }
}
