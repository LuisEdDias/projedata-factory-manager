package lat.luisdias.factory.manager.exeption;

import jakarta.servlet.http.HttpServletRequest;
import lat.luisdias.factory.manager.exeption.exceptions.DomainInvariantViolationException;
import lat.luisdias.factory.manager.exeption.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(DomainInvariantViolationException.class)
    public ProblemDetail handleDomainInvariantViolation(
            DomainInvariantViolationException ex,
            HttpServletRequest request
    ) {
        log.warn("Domain exception: {} - Args: {}", ex.getMessageKey(), Arrays.toString(ex.getArgs()), ex.getCause());

        return buildProblem(
                HttpStatus.BAD_REQUEST,
                ex.getMessageKey(),
                ex.getArgs(),
                request
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(
            NotFoundException ex,
            HttpServletRequest request
    ) {
        log.warn("Not found: {} - Args: {}", ex.getMessageKey(), Arrays.toString(ex.getArgs()), ex.getCause());
        return buildProblem(
                HttpStatus.NOT_FOUND,
                ex.getMessageKey(),
                ex.getArgs(),
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        log.warn("Validation error on endpoint: {}", request.getRequestURI());

        ProblemDetail problemDetail = buildProblem(
                HttpStatus.BAD_REQUEST,
                "exception.validation.failed",
                null,
                request
        );

        List<Map<String, String>> invalidParams = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "reason", error.getDefaultMessage() != null ?
                                error.getDefaultMessage() :
                                messageSource.getMessage(
                                        "exception.validation.invalid_value",
                                        null,
                                        LocaleContextHolder.getLocale()
                                )
                ))
                .toList();

        problemDetail.setProperty("invalid_params", invalidParams);

        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        log.warn("Malformed JSON request: {}", ex.getMessage());

        return buildProblem(
                HttpStatus.BAD_REQUEST,
                "exception.request.malformed_json",
                null,
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unexpected error occurred during request: {}", request.getRequestURI(), ex);

        return buildProblem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "exception.internal_error",
                null,
                request
        );
    }

    private ProblemDetail buildProblem(
            HttpStatus status,
            String messageKey,
            Object[] args,
            HttpServletRequest request
    ) {
        Locale locale = LocaleContextHolder.getLocale();
        String localized = messageSource.getMessage(messageKey, args, locale);

        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle(status.getReasonPhrase());
        problemDetail.setDetail(localized);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }
}
