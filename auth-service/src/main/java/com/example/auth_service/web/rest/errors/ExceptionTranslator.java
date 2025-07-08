package bo.vulcan.krtigomsreport.web.rest.errors;

import static bo.vulcan.krtigomsreport.web.rest.errors.ErrorConstants.ENTITY_NOT_FOUND;

import bo.vulcan.krtigomsreport.web.rest.errors.my.BadRequestException;
import bo.vulcan.krtigomsreport.web.rest.errors.my.NotFoundException;
import bo.vulcan.krtigomsreport.web.rest.util.HeaderUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.DefaultProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.violations.ConstraintViolationProblem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 * The error response follows RFC7807 - Problem Details for HTTP APIs (https://tools.ietf.org/html/rfc7807)
 */
@ControllerAdvice
@Slf4j
public class ExceptionTranslator implements ProblemHandling {

    private static final String ERROR_KEY = "errorKey";
    private static final String MESSAGE_KEY = "message";
    private static final String FIELD_ERRORS_KEY = "fieldErrors";
    private static final String VIOLATIONS_KEY = "violations";
    private static final String PATH_KEY = "path";


    /**
     * Post-process the Problem payload to add the message key for the front-end if needed
     */
    @Override
    public ResponseEntity<Problem> process(@Nullable ResponseEntity<Problem> entity, NativeWebRequest request) {
        if (entity == null) {
            return entity;
        }
        Problem problem = entity.getBody();
        if (!(problem instanceof ConstraintViolationProblem || problem instanceof DefaultProblem)) {
            return entity;
        }
        ProblemBuilder builder = Problem.builder()
            .withStatus(problem.getStatus())
            .with(PATH_KEY, getPathNativeRequest(request));

        if (problem instanceof ConstraintViolationProblem) {
            builder
                .with(VIOLATIONS_KEY, ((ConstraintViolationProblem) problem).getViolations())
                .with(MESSAGE_KEY, ErrorConstants.ERR_VALIDATION);
        } else {
            builder
                .with(ERROR_KEY, ErrorConstants.INTERNAL_SERVER_ERROR)
                .with(MESSAGE_KEY, problem.getDetail());
            problem.getParameters().forEach(builder::with);
        }
        return new ResponseEntity<>(builder.build(), entity.getHeaders(), entity.getStatusCode());
    }

    @Override
    public ResponseEntity<Problem> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @Nonnull NativeWebRequest request) {
        BindingResult result = ex.getBindingResult();
        List<FieldErrorVM> fieldErrors = result.getFieldErrors().stream()
            .map(f -> new FieldErrorVM(f.getObjectName(), f.getField(), f.getCode()))
            .collect(Collectors.toList());

        Problem problem = Problem.builder()
            .withTitle("Method argument not valid")
            .with(MESSAGE_KEY, ErrorConstants.ERR_VALIDATION)
            .with(FIELD_ERRORS_KEY, fieldErrors)
            .withStatus(defaultConstraintViolationStatus())
            .build();

        log.debug("PROBLEM body handleMethodArgumentNotValid: {}", problem);
        return create(ex, problem, request);
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleNoSuchElementException(NoSuchElementException ex, NativeWebRequest request) {
        Problem problem = Problem.builder()
            .with(MESSAGE_KEY, ENTITY_NOT_FOUND)
            .withStatus(Status.NOT_FOUND)
            .build();

        log.debug("PROBLEM body handleNoSuchElementException: {}", problem);
        return create(ex, problem, request);
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleBadRequestAlertException(BadRequestAlertException ex, NativeWebRequest request) {
        String  message = ex.getMessage();
        ProblemBuilder builder = Problem.builder()
            .with(ERROR_KEY, ex.getErrorKey())
            .withStatus(Status.BAD_REQUEST)
            .with(MESSAGE_KEY, message)
            .with(PATH_KEY, getPathNativeRequest(request));
        log.debug("PROBLEM body handleBadRequestAlertException");
        return create(ex, builder.build(), request);
    }

    @ExceptionHandler
    public ResponseEntity<Problem> handleConcurrencyFailure(ConcurrencyFailureException ex, NativeWebRequest request) {
        Problem problem = Problem.builder()
            .withStatus(Status.CONFLICT)
            .with(MESSAGE_KEY, ErrorConstants.ERR_CONCURRENCY_FAILURE)
            .build();
        return create(ex, problem, request);
    }

    /* Additional methods */

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Problem> handleBadRequestException(BadRequestException ex, NativeWebRequest request) {
        Problem problem = Problem.builder()
            .withStatus(ex.getStatus())
            .with(PATH_KEY,getPathNativeRequest(request))
            .with(ERROR_KEY, ex.getErrorKey())
            .withStatus(Status.BAD_REQUEST)
            .with(MESSAGE_KEY, ex.getMessage())
            .withTitle(ex.getTitle())
            .build();
        return create(ex, problem, request, HeaderUtil.createFailureAlert(ex.getErrorKey(), ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Problem> handleNotFoundException(NotFoundException ex, NativeWebRequest request) {
        Problem problem = Problem.builder()
            .withStatus(ex.getStatus())
            .with(PATH_KEY, getPathNativeRequest(request))
            .with(ERROR_KEY, ErrorConstants.ENTITY_NOT_FOUND)
            .withStatus(Status.NOT_FOUND)
            .with(MESSAGE_KEY, "Not found entity.")
            .withTitle(ex.getTitle())
            .build();
        return create(ex, problem, request, HeaderUtil.createFailureAlert(null, ex.getErrorKey(), ex.getMessage()));
    }

    private String getPathNativeRequest(NativeWebRequest request) {
        HttpServletRequest nativeRequest = request.getNativeRequest(HttpServletRequest.class);
        return nativeRequest != null ? nativeRequest.getRequestURI() : "";
    }
}
