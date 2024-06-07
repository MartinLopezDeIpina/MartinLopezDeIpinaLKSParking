package com.lksnext.parking.data;

import android.util.Patterns;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lksnext.parking.domain.LoginCallback;
import com.lksnext.parking.domain.RegisterCallback;
import com.lksnext.parking.domain.Usuario;

public class DataRepository {

    private static DataRepository instance;
    private FirebaseAuth mAuth;
    private DataBaseManager dbManager;
    private DataRepository(){
        mAuth = FirebaseAuth.getInstance();
        dbManager = DataBaseManager.getInstance();
    }

    //Creación de la instancia en caso de que no exista.
    public static synchronized DataRepository getInstance(){
        if (instance==null){
            instance = new DataRepository();
        }
        return instance;
    }

    //Petición del login.
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
                    callback.onSuccess();
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

    //Todo: gestionar como añadir esto a la base de datos
    public void register(String email, String password, String name, String phone, RegisterCallback callback){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            Usuario usuario = new Usuario(user.getUid(), name, email, phone);
                            dbManager.addUserToDB(usuario);
                            callback.onSuccess();

                        } else {
                            RegisterErrorType error = getRegisterErrorMessage(task.getException());
                            callback.onRegisterFailure(error);
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
}
