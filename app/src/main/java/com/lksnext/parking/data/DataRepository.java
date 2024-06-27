package com.lksnext.parking.data;

import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.lksnext.parking.data.callbacks.EmailVerificationCallback;
import com.lksnext.parking.data.callbacks.LoginCallback;
import com.lksnext.parking.data.callbacks.RegisterCallback;
import com.lksnext.parking.domain.Usuario;

public class DataRepository {

    private static DataRepository instance;
    private FirebaseAuth mAuth;
    private FirebaseUser pendingFirebaseUser;
    private Usuario pendingUser;
    private DataBaseManager dbManager;
    private DataRepository(){
        mAuth = FirebaseAuth.getInstance();
        dbManager = DataBaseManager.getInstance();
    }

    public static synchronized DataRepository getInstance(){
        if (instance==null){
            instance = new DataRepository();
        }
        return instance;
    }

    public void login( String email, String pass, LoginCallback callback){
        if(email.isEmpty()){
            callback.onFailure(LoginErrorType.EMPTY_EMAIL);
            return;
        }
        if(pass.isEmpty()){
            callback.onFailure(LoginErrorType.EMPTY_PASSWORD);
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            callback.onFailure(LoginErrorType.INVALID_EMAIL);
            return;
        }
        if(pass.length()<6){
            callback.onFailure(LoginErrorType.INVALID_PASSWORD);
            return;
        }

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        if (user.isEmailVerified()) {
                            callback.onSuccess();
                        } else {
                            callback.onFailure(LoginErrorType.EMAIL_NOT_VERIFIED);
                        }
                    }
                } else {
                    LoginErrorType error = getLoginErrorMessage(task.getException());
                    callback.onFailure(error);
                }
            }
        });
    }

    private LoginErrorType getLoginErrorMessage(Exception exception) {
        LoginErrorType message;
        if (exception instanceof FirebaseAuthInvalidCredentialsException ) {
            message = LoginErrorType.WRONG_PASSWORD;
        } else {
            message = LoginErrorType.UNKNOWN_ERROR;
        }
        return message;
    }

    public void register(String email, String password, String name, String phone, RegisterCallback callback){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            pendingFirebaseUser = mAuth.getCurrentUser();
                            pendingUser = new Usuario(pendingFirebaseUser.getUid(), name, email, phone);

                            callback.onSuccess();

                        } else {
                            RegisterErrorType error = getRegisterErrorMessage(task.getException());
                            callback.onRegisterFailure(error);
                        }
                    }
                });
    }

    public void sendVerificationEmail(EmailVerificationCallback callback){
        pendingFirebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dbManager.addUserToDB(pendingUser);
                    callback.onSuccess();
                } else {
                    callback.onFailure(RegisterErrorType.UNKNOWN_ERROR);
                }
            }
        });
    }

    private RegisterErrorType getRegisterErrorMessage(Exception exception) {
        RegisterErrorType message;
        if (exception instanceof FirebaseAuthUserCollisionException) {
            message = RegisterErrorType.EMAIL_ALREADY_REGISTERED;
        } else {
            message = RegisterErrorType.UNKNOWN_ERROR;
        }
        return message;
    }

    public void emailValid(String email, LoginCallback loginCallback) {
        if(email.isEmpty()){
            loginCallback.onFailure(LoginErrorType.EMPTY_EMAIL);
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginCallback.onFailure(LoginErrorType.INVALID_EMAIL);
            return;
        }
        LiveData<Boolean> userExists = dbManager.getUserExists(email);
        userExists.observeForever(exists -> {
            if(exists){
                loginCallback.onSuccess();
            }else{
                loginCallback.onFailure(LoginErrorType.USER_NOT_FOUND);
            }
        });
    }

    public void sendPasswordResetEmail(String email, OnCompleteListener<Void> onCompleteListener) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(onCompleteListener);
    }
}
