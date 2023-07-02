package sk.avo.chatapi.presentation.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import sk.avo.chatapi.presentation.exceptions.dto.ErrorResponse;

@RestControllerAdvice
public class ErrorResolver {
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoHandlerFound(NoHandlerFoundException e, WebRequest request) {
        String url = request.getDescription(false);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("'" + url + "' not found");
        errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
        return errorResponse;
    }
}