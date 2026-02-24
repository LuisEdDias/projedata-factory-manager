package lat.luisdias.factory.manager.exeption;

import jakarta.servlet.http.HttpServletRequest;
import lat.luisdias.factory.manager.exeption.exceptions.DomainInvariantViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.Arrays;
import java.util.Locale;

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
        log.warn("Domain exception: {} - Args: {}", ex.getMessageKey(), Arrays.toString(ex.getArgs()));
        return buildProblem(
                HttpStatus.BAD_REQUEST,
                ex.getMessageKey(),
                ex.getArgs(),
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
