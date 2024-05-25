package com.lksnext.parking.data;

import android.app.Activity;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.lksnext.parking.domain.Callback;

public class DataRepository {

    private static DataRepository instance;
    private FirebaseAuth mAuth;
    private DataRepository(){
        mAuth = FirebaseManager.getInstance().getFirebaseAuth();
    }

    //Creación de la instancia en caso de que no exista.
    public static synchronized DataRepository getInstance(){
        if (instance==null){
            instance = new DataRepository();
        }
        return instance;
    }

    //Petición del login.
    public void login( String email, String pass, Callback callback){
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
                    callback.onSuccess();
                } else {
                    LoginErrorType error = getErrorMessage(task.getException());
                    callback.onFailure(error);
                }
            }
        });
    }

    private LoginErrorType getErrorMessage(Exception exception) {
        LoginErrorType message;
        if (exception instanceof FirebaseAuthInvalidUserException) {
            message = LoginErrorType.USER_NOT_FOUND;
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            message = LoginErrorType.WRONG_PASSWORD;
        } else {
            message = LoginErrorType.UNKNOWN_ERROR;
        }
        return message;
    }
}
