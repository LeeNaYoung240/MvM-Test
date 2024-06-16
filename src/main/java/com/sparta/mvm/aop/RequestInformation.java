package com.sparta.mvm.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j(topic = "RequestInformation")
@Aspect
@Component
@RequiredArgsConstructor
public class RequestInformation {

    @Pointcut("execution(* com.sparta.mvm.controller..*(..))")
    private void allControllers() {}

    @Before("allControllers()")
    public void requestLogInformation() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            log.info("HTTP Method : {}, Request URI : {}", request.getMethod(), request.getRequestURI());
        } catch (IllegalStateException error) {
            log.warn("요청을 찾을 수 없습니다.", error);
        }
    }
}
