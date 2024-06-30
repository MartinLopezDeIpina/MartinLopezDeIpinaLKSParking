package com.lksnext.parking.view.activity;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import android.Manifest;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.lksnext.parking.data.DataBaseManager;
import com.lksnext.parking.data.DataRepository;
import com.lksnext.parking.data.callbacks.RegisterCallback;
import com.lksnext.parking.domain.Usuario;
import com.lksnext.parking.util.DataBaseFiller;
import com.lksnext.parking.util.notifications.NotificationsManager;
import com.lksnext.parking.view.fragment.LoginFragment;
import com.lksnext.parking.viewmodel.LoginViewModel;
import com.lksnext.parking.R;
import com.lksnext.parking.databinding.ActivityLoginBinding;

import java.io.Console;

public class LoginActivity extends BaseActivity implements LoginFragment.SignInHandler{

    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;

    private FirebaseAuth mAuth;
    private SignInClient oneTapClient;

    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataBaseFiller dataBaseFiller = new DataBaseFiller();
        //dataBaseFiller.fillReservas();
        //dataBaseFiller.fillPlaza98();
        //dataBaseFiller.fillPlazas75_94();
        //dataBaseFiller.fillPlazasPruebasHoras();
        //dataBaseFiller.fillReservasPasadas();

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.setIsLogged(false);


        mAuth = FirebaseAuth.getInstance();

        oneTapClient = Identity.getSignInClient(this);
    }


    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                    SignInCredential googleCredential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = googleCredential.getGoogleIdToken();
                    if (idToken != null) {
                        // Got an ID token from Google. Use it to authenticate
                        // with Firebase.
                        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                        mAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                                            if (acct != null) {

                                                String emailUsuario = acct.getEmail();
                                                String uid = mAuth.getUid();
                                                LiveData<Boolean> exists = DataBaseManager.getInstance().getUserExists(emailUsuario);
                                                exists.observe(LoginActivity.this, userExists -> {
                                                    if (!userExists) {
                                                        //Para tener los datos de los usuarios de firebase en la base de datos
                                                        String name = acct.getDisplayName();
                                                        Usuario usuario = new Usuario(uid, name, emailUsuario, "");

                                                        LiveData<Boolean> registered = DataBaseManager.getInstance().addUserToDB(usuario);
                                                        registered.observe(LoginActivity.this, registeredUser -> {
                                                            if (registeredUser) {
                                                                loginViewModel.setIsLogged(true);
                                                            }
                                                        });
                                                    }else{
                                                        loginViewModel.setIsLogged(true);
                                                    }
                                                });

                                            }else{
                                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                binding.getRoot().findViewById(R.id.login_top_progressbar).setVisibility(View.VISIBLE);
                            }
                        }, 100);
                    }
                } catch (ApiException e) {
                    Log.e(TAG, "Error with Google Sign In API", e);
                }
                break;
        }
    }
    @Override
    public void startOneTapSignIn(BeginSignInRequest signInRequest) {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        binding.getRoot().findViewById(R.id.login_top_progressbar).setVisibility(View.GONE);
                        startIntentSenderForResult(result.getPendingIntent().getIntentSender(), REQ_ONE_TAP, null, 0, 0, 0, null);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e("YourActivity", "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.d("YourActivity", e.getLocalizedMessage());
                    Toast.makeText(this, "Couldn't start One Tap UI", Toast.LENGTH_SHORT).show();
                });
    }
}