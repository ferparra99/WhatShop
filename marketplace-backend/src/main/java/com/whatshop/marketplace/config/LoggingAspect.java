package com.whatshop.marketplace.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    private static final List<String> SENSITIVE_FIELDS = List.of("password", "token");

    @Around("execution(* com.tuapp.marketplace..controller..*.*(..))")
    public Object logControllerCall(ProceedingJoinPoint joinPoint) throws Throwable {
        var signature = (MethodSignature) joinPoint.getSignature();
        var method = signature.getMethod();
        var httpMethod = resolveHttpMethod(method);
        var path = resolvePath(method, joinPoint.getArgs(), signature);
        var args = filterSensitiveArgs(signature.getParameterNames(), joinPoint.getArgs());

        log.info(">>> {} {} | args: {}", httpMethod, path, args);

        var start = Instant.now();
        try {
            var result = joinPoint.proceed();
            var elapsed = Duration.between(start, Instant.now()).toMillis();

            if (result instanceof ResponseEntity<?> entity) {
                var statusCode = entity.getStatusCode().value();
                log.info("<<< {} {} | {} {} ({}ms)", httpMethod, path, statusCode, describeStatus(statusCode), elapsed);
            } else {
                log.info("<<< {} {} | OK ({}ms)", httpMethod, path, elapsed);
            }
            return result;
        } catch (Exception e) {
            var elapsed = Duration.between(start, Instant.now()).toMillis();
            log.error("xxx {} {} | ERROR {} ({}ms): {}", httpMethod, path, e.getClass().getSimpleName(), elapsed, e.getMessage());
            throw e;
        }
    }

    private String resolveHttpMethod(Method method) {
        if (method.isAnnotationPresent(PostMapping.class)) return "POST";
        if (method.isAnnotationPresent(GetMapping.class)) return "GET";
        if (method.isAnnotationPresent(PutMapping.class)) return "PUT";
        if (method.isAnnotationPresent(DeleteMapping.class)) return "DELETE";
        if (method.isAnnotationPresent(PatchMapping.class)) return "PATCH";
        return "???";
    }

    private String resolvePath(Method method, Object[] args, MethodSignature signature) {
        var classPath = method.getDeclaringClass().getAnnotation(RequestMapping.class);
        var basePath = (classPath != null) ? classPath.value()[0] : "";

        var methodPath = "";
        if (method.isAnnotationPresent(PostMapping.class)) methodPath = method.getAnnotation(PostMapping.class).value().length > 0 ? method.getAnnotation(PostMapping.class).value()[0] : "";
        else if (method.isAnnotationPresent(GetMapping.class)) methodPath = method.getAnnotation(GetMapping.class).value().length > 0 ? method.getAnnotation(GetMapping.class).value()[0] : "";
        else if (method.isAnnotationPresent(PutMapping.class)) methodPath = method.getAnnotation(PutMapping.class).value().length > 0 ? method.getAnnotation(PutMapping.class).value()[0] : "";
        else if (method.isAnnotationPresent(DeleteMapping.class)) methodPath = method.getAnnotation(DeleteMapping.class).value().length > 0 ? method.getAnnotation(DeleteMapping.class).value()[0] : "";
        else return basePath;

        var fullPath = basePath + (methodPath.startsWith("/") ? "" : "/") + methodPath;

        var paramNames = signature.getParameterNames();
        var paramAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < paramAnnotations.length; i++) {
            for (var ann : paramAnnotations[i]) {
                if (ann instanceof PathVariable pv) {
                    var placeholder = "{" + (pv.value().isEmpty() ? paramNames[i] : pv.value()) + "}";
                    var value = args[i] != null ? args[i].toString() : "?";
                    fullPath = fullPath.replace(placeholder, value);
                }
                if (ann instanceof RequestParam rp) {
                    var name = rp.value().isEmpty() ? paramNames[i] : rp.value();
                    var value = args[i] != null ? args[i].toString() : "null";
                    fullPath += (fullPath.contains("?") ? "&" : "?") + name + "=" + value;
                }
            }
        }

        if (fullPath.endsWith("/")) fullPath = fullPath.substring(0, fullPath.length() - 1);
        return fullPath;
    }

    private String filterSensitiveArgs(String[] paramNames, Object[] args) {
        if (paramNames == null || args == null) return "[]";

        var filtered = new java.util.StringJoiner(", ", "[", "]");
        for (int i = 0; i < paramNames.length && i < args.length; i++) {
            var isSensitive = SENSITIVE_FIELDS.contains(paramNames[i].toLowerCase());
            var value = isSensitive ? "****" : (args[i] != null ? args[i].toString() : "null");
            filtered.add(paramNames[i] + "=" + value);
        }
        return filtered.toString();
    }

    private String describeStatus(int code) {
        if (code >= 200 && code < 300) return "OK";
        if (code == 400) return "BAD_REQUEST";
        if (code == 401) return "UNAUTHORIZED";
        if (code == 403) return "FORBIDDEN";
        if (code == 404) return "NOT_FOUND";
        if (code >= 500) return "SERVER_ERROR";
        return "OTHER";
    }
}
