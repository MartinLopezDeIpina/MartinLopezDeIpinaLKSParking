package com.lksnext.parking.domain;

import com.lksnext.parking.data.LoginErrorType;

public interface Callback {
    void onSuccess();
    void onFailure(LoginErrorType error);
}
