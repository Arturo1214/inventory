package com.example.auth_service.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import javax.annotation.Nullable;
import java.util.Map;

public class BaseAlertException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    private final String errorKey;

    public BaseAlertException(String errorKey, String defaultMessage, Status status) {
        super(null, defaultMessage, status, null, null, null, null);
        this.errorKey = errorKey;
    }

    public BaseAlertException(String errorKey, String defaultMessage, Status status, @Nullable final Map<String, Object> parameters) {
        super(null, defaultMessage, status, null, null, null, parameters);
        this.errorKey = errorKey;
    }

    public String getErrorKey() {
        return errorKey;
    }
}
