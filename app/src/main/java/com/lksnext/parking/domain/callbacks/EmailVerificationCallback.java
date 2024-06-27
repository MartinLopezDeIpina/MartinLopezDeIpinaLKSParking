package com.lksnext.parking.domain.callbacks;

import com.lksnext.parking.data.RegisterErrorType;

public interface EmailVerificationCallback {
    void onSuccess();
    void onFailure(RegisterErrorType error);
}
