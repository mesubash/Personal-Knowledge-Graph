package com.developer.pkg.controller;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.ErrorType;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Arrays;

@ControllerAdvice
public class GraphQlExceptionAdvice {

    @GraphQlExceptionHandler
    public GraphQLError handleBindException(BindException ex, DataFetchingEnvironment env) {
        FieldError fieldError = ex.getFieldError();
        String fieldName = fieldError != null ? fieldError.getField() : "argument";
        if ("$".equals(fieldName)) {
            fieldName = "argument";
        }

        String rejectedValue = fieldError != null ? String.valueOf(fieldError.getRejectedValue()) : "null";
        String message = "Invalid value for " + fieldName + ": " + rejectedValue + ".";

        if (fieldError != null && hasCode(fieldError, "typeMismatch.UUID")) {
            message = message + " Expected UUID.";
        }

        return GraphqlErrorBuilder.newError(env)
                .message(message)
                .errorType(ErrorType.ValidationError)
                .build();
    }

    private boolean hasCode(FieldError error, String code) {
        String[] codes = error.getCodes();
        return codes != null && Arrays.asList(codes).contains(code);
    }
}
