package com.example.demo.model;

import java.util.Objects;

/**
 * A response class, containing a useful message.
 */
public class Message
{
    private String message;
    private int code;

    public Message(final String message, final int code)
    {
        this.message = message;
        this.code = code;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(final String message)
    {
        this.message = message;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(final int code)
    {
        this.code = code;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) {
            return true;
        }
        if (o instanceof Message otherMessage) {
            return Objects.equals(this.message, otherMessage.message) &&
                (this.code == otherMessage.code);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(message, code);
    }
}