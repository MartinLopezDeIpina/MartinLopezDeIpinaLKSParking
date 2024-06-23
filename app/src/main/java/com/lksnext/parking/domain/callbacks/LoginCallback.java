package com.lksnext.parking.domain.callbacks;

import com.lksnext.parking.data.LoginErrorType;

public interface LoginCallback {
    void onSuccess();
    void onFailure(LoginErrorType error);
}
