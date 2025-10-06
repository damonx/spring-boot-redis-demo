package com.example.demo.exception;

import com.example.demo.model.Message;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generic redis demo exception.
 */
public class SpringRedisDemoException extends Exception {
    @Serial
    private static final long serialVersionUID = 92318938217L;
    private final HttpStatus statusCode;
    private final transient List<Message> messages = new ArrayList<>();

    /**
     * @param message    The exception's message.
     * @param statusCode The http status that this exception will map to.  May be null (in which case, the messages
     *                   will be ignored).
     * @param messages   A non-null List of Messages, indicating what went wrong. May be empty.
     */
    public SpringRedisDemoException(final String message, final HttpStatus statusCode, final List<Message> messages) {
        super(message);
        this.statusCode = statusCode;
        this.messages.addAll(messages);
    }

    /**
     * @param message       The exception's message.
     * @param statusCode    The http status that this exception will map to.  May be null (in which case, the message
     *                      will be ignored).
     * @param messageObject A non-null Message, indicating what went wrong.
     */
    public SpringRedisDemoException(final String message, final HttpStatus statusCode, final Message messageObject) {
        super(message);
        this.statusCode = statusCode;
        this.messages.add(messageObject);
    }

    /**
     * @param message    The exception's message.
     * @param statusCode The http status that this exception will map to.  May be null (in which case, the messages
     *                   will be ignored).
     * @param messages   A non-null List of Messages, indicating what went wrong. May be empty.
     * @param cause      The cause of this exception.
     */
    public SpringRedisDemoException(final String message, final HttpStatus statusCode, final List<Message> messages, final Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.messages.addAll(messages);
    }

    /**
     * @param message       The exception's message.
     * @param statusCode    The http status that this exception will map to.  May be null (in which case, the message
     *                      will be ignored).
     * @param messageObject A non-null Message, indicating what went wrong.
     * @param cause         The cause of this exception.
     */
    public SpringRedisDemoException(final String message, final HttpStatus statusCode, final Message messageObject, final Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.messages.add(messageObject);
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }
}
