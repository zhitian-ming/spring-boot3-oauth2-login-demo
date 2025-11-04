package com.oauth2.exception;

import com.oauth2.exception.code.BaseErrorCode;
import com.oauth2.model.entity.RspEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler  {

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<RspEntity<?>> handleRuntimeException(CustomException e) {
        log.error("error：", e);
        return ResponseEntity.ok(new RspEntity<>(e.getCode(), e.getDesc()));
    }

    /**
     * 处理参数异常
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class, BindException.class})
    public ResponseEntity<RspEntity<?>> argumentExceptionHandler(Exception e, HttpServletRequest request) {

        String message = "";
        if (e instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
            List<ObjectError> errors = bindingResult.getAllErrors();
            if (!errors.isEmpty()) {
                message = errors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("; "));
            }
        } else if (e instanceof ConstraintViolationException) {
            Set<ConstraintViolation<?>> violations = ((ConstraintViolationException) e).getConstraintViolations();
            if (violations != null && !violations.isEmpty()) {
                message = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("; "));
            }
        } else {
            BindException bindException = (BindException) e;
            List<ObjectError> errors = bindException.getAllErrors();
            if (!errors.isEmpty()) {
                message = errors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("; "));
            }
        }

        log.info("request uri: " + request.getRequestURI() + " " + request.getMethod() + ", error_msg: "
                + message);
        return ResponseEntity.ok(new RspEntity<>(2, message));
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RspEntity<?>> handleException(Exception e) {
        log.error("error：", e);
        return ResponseEntity.ok(new RspEntity<>(BaseErrorCode.SYSTEM_ERROR));
    }
}