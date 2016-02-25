package com.example.vasia.mytask8;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vasia.mytask8.entity.User;
import com.example.vasia.mytask8.service.AccelerometerService;
import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CompoundButton.OnCheckedChangeListener,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    public static final String MAIN_TAG = "my_app";
    public static final int TIME_INTERVAL = 1000;

    private static final int NAV_HEADER_MAIN = 0;
    private static final int RC_SIGN_IN = 9001;
    private static final int REQ_SIGN_IN_REQUIRED = 55664;
    private static final String FIREBASE_URL = "https://boostboost.firebaseio.com/";


    private Firebase mFirebaseRef = new Firebase(FIREBASE_URL);
    private Firebase mUsersRef = new Firebase(FIREBASE_URL).child("Users");
    private Firebase mSamplesRef;

    private boolean mIsBound;
    private Context mContext = this;
    private String mAccountName;
    private Menu mNavigationMenu;
    private TextView mStatusTextView;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private ServiceConnection mConnection = new ServiceConnection() {
        private AccelerometerService mAccBoundService;

        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mAccBoundService = ((AccelerometerService.AccelerometerBinder) service).getService();

            // Tell the user about this.
            Toast.makeText(mContext, R.string.local_service_connected,
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mAccBoundService = null;

            // Tell the user about this.
            Toast.makeText(mContext, R.string.local_service_disconnected,
                    Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setNavigationMenu(navigationView.getMenu());

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Customize sign-in button.
        SignInButton signInButton = (SignInButton) findViewById(R.id.btn_sign_in);
        signInButton.setOnClickListener(this);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

        // Build a GoogleApiClient with access to the Google Sign-In API
        // and the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mStatusTextView = (TextView) findViewById(R.id.tv_status);
    }

    public void setNavigationMenu(Menu navigationMenu) {
        this.mNavigationMenu = navigationMenu;

        mUsersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                mNavigationMenu.add(R.id.nav_users_group,
                        0,
                        mNavigationMenu.size(),
                        user.getDisplayName());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(MAIN_TAG, "MainActivity: onStart");

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid,
            // the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(MAIN_TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        SwitchCompat switchCompat = (SwitchCompat) menu.getItem(0)
                .getActionView().findViewById(R.id.switch_control);
        switchCompat.setOnCheckedChangeListener(this);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(MAIN_TAG, "MainActivity: onActivityResult");

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        if (requestCode == REQ_SIGN_IN_REQUIRED && resultCode == RESULT_OK) {
            // We had to sign in - now we can finish off the token request.
            new RetrieveTokenTask().execute(mAccountName);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(MAIN_TAG, "MainActivity: handleSignInResult - " + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                mAccountName = acct.getEmail();
                // run an async task to get an OAuth2 token for the account
                new RetrieveTokenTask().execute(mAccountName);
            }

            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In)
        // will not be available.
        Log.d(MAIN_TAG, "MainActivity: onConnectionFailed - " + connectionResult);
        Toast.makeText(this, "ConnectionFailed; " + connectionResult, Toast.LENGTH_SHORT);
    }

    private void showProgressDialog() {
        Log.d(MAIN_TAG, "MainActivity: showProgressDialog");
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        Log.d(MAIN_TAG, "MainActivity: hideProgressDialog");
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            Log.d(MAIN_TAG, "MainActivity: updateUI -- true");

            findViewById(R.id.btn_sign_in).setVisibility(View.GONE);

            if(mFirebaseRef.getAuth() != null) {
                setNavigationHeader(mFirebaseRef.getAuth());
            }

//            findViewById(R.id.btn_sign_out).setVisibility(View.VISIBLE);
        } else {
            Log.d(MAIN_TAG, "MainActivity: updateUI -- false");
            findViewById(R.id.btn_sign_in).setVisibility(View.VISIBLE);

            setNavigationHeader(null);
//            findViewById(R.id.btn_sign_out).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                signIn();
                break;
        }
    }

    private void signIn() {
        Log.d(MAIN_TAG, "MainActivity: signIn");
        if(mFirebaseRef.getAuth() == null){
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    private void signOut() {
        Log.d(MAIN_TAG, "MainActivity: signOut");
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        updateUI(false);

                    }
                });

        new Firebase(FIREBASE_URL).unauth();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        for (int i = 0; i < mNavigationMenu.size(); i++) {
            if (id == mNavigationMenu.getItem(i).getItemId()) {
                Log.d(MAIN_TAG, "NavMenuItem: " + mNavigationMenu.getItem(i).getItemId()
                        + " selected");
            }
        }
        if (id == R.id.nav_sign_in) {
            signIn();
        } else if (id == R.id.nav_sign_out) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(MAIN_TAG, "MainActivity: onCheckedChanged " + isChecked);
        if (isChecked) {
            doBindService();
        } else {
            doUnbindService();
        }
    }

    void doBindService() {
        Log.d(MAIN_TAG, "MainActivity: doBindService");
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        Intent intent = new Intent(this, AccelerometerService.class);
        intent.putExtra("samplesRef", String.valueOf(mSamplesRef));
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        Log.d(MAIN_TAG, "MainActivity: doUnbindService");
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(MAIN_TAG, "MainActivity: onDestroy");
        super.onDestroy();
        doUnbindService();
    }

    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:profile email";
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
            } catch (IOException e) {
                Log.e(MAIN_TAG, e.getMessage());
            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
            } catch (GoogleAuthException e) {
                Log.e(MAIN_TAG, e.getMessage());
            }
            return token;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            authFireBase(s);
        }
    }

    private void authFireBase(String token) {
        Log.d(MAIN_TAG, "TOKEN IS " + token);

        if (token != null) {
            // Successfully got OAuth token, now login with Google
            mFirebaseRef.authWithOAuthToken("google", token, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    // Save authenticated user to FireBase
                    saveUserToFirebase(authData);

                    setNavigationHeader(mFirebaseRef.getAuth());
                }

                private void saveUserToFirebase(AuthData authData) {
                    Log.d(MAIN_TAG, "MainActivity; saveUserToFirebase");
                    Map<String, Object> providerData = authData.getProviderData();
                    User user = new User((String) providerData.get("displayName"),
                            (String) providerData.get("provider"));
                    mUsersRef.child((String) providerData.get("id")).setValue(user);
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {

                }
            });
        }
    }

    private void setNavigationHeader(AuthData auth) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (auth != null) {
            View navView = navigationView.getHeaderView(NAV_HEADER_MAIN);
            ((TextView) navView.findViewById(R.id.tv_nav_header_name))
                    .setText((String) auth.getProviderData().get("displayName"));
            ((TextView) navView.findViewById(R.id.tv_nav_header_email))
                    .setText((String) auth.getProviderData().get("email"));
        } else {
            Log.d(MAIN_TAG, "MainActivity; setNavigationHeader -- null");
            navigationView.removeHeaderView(navigationView.getHeaderView(NAV_HEADER_MAIN));
            navigationView.inflateHeaderView(R.layout.nav_header_main);
        }
    }
}

