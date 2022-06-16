package com.lianzheng.core.exceptionhandling;

import com.lianzheng.core.exceptionhandling.exception.COREException;
import com.lianzheng.core.server.ResponseBase;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * @Description: Globally handle exception in REST service
 * @author: 何江雁
 * @date: 2021年09月30日 10:10
 */
@Log4j2
@RestControllerAdvice
public class ExceptionRestControllerAdvice {

    @ExceptionHandler(COREException.class)
    public ResponseBase handleCOREException(COREException ex){
        log.error(ex.getMessage(), ex);
        return  ResponseBase.error(ex.getCode(),ex.getMsg());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handle(Exception ex, WebRequest request) throws Exception {
        ex.printStackTrace();
        return this.handleException(ex, request);
    }



    @ExceptionHandler(IncorrectCredentialsException.class)
    public ResponseEntity<Object> IncorrectCredentialsException(Exception ex, WebRequest request) throws Exception {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return this.handleException(ex, request,null,status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(Exception ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<String> errors = ((MethodArgumentNotValidException)ex).getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());

        body.put("errors", errors);
        return this.handleException(ex, request, body, status);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(Exception ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        HttpStatus status = HttpStatus.OK;

        return this.handleException(ex, request, body, status);
    }

    protected ResponseEntity<Object> handleException(Exception ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        HttpHeaders headers = new HttpHeaders();

        return this.handleException(ex, request, body, status, headers);
    }
    protected ResponseEntity<Object> handleException(Exception ex, WebRequest request, Map<String, Object> body, HttpStatus status) {

        HttpHeaders headers = new HttpHeaders();
        return this.handleException(ex, request, body, status, headers);
    }

    protected ResponseEntity<Object> handleException(Exception ex, WebRequest request, Map<String, Object> body, HttpStatus status, HttpHeaders headers) {
        //anyway, always to print stacktrace first
        ex.printStackTrace();

        if(body==null){
            body = new LinkedHashMap<>();
        }

        body.put("timestamp", LocalDate.now());
        body.put("code", status.value());

        //todo, 根据环境变量获取是否是开发环境
        final Boolean IS_DEV = true;
        if(!body.containsKey("msg")){
            if(IS_DEV){
                body.put("msg", ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : ex.toString());
            }
            else{
                body.put("msg", status.getReasonPhrase());
            }

        }

        if(IS_DEV){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String msg=sw.toString();

            body.put("trace", msg);
        }

        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            String errorMessage = "javax.servlet.error.exception:"+ExceptionFormatter.Format(ex);
            log.fatal(errorMessage);
            request.setAttribute("javax.servlet.error.exception", ex, 0);
        }

        return new ResponseEntity(body, headers, status);
    }


}
