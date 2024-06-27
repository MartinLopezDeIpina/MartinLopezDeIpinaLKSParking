package com.lksnext.parking.data.callbacks;

import com.lksnext.parking.data.LoginErrorType;

public interface LoginCallback {
    void onSuccess();
    void onFailure(LoginErrorType error);
}
