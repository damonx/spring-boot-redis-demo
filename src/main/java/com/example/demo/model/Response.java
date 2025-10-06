package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A generic response object, containing just a list of messages.
 */
public class Response
{
    private List<Message> messages;

    /**
     * @return a non-null list of messages (may be empty, though).
     */
    public List<Message> getMessages()
    {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        return messages;
    }

    public void setMessages(final List<Message> messages)
    {
        this.messages = messages;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) {
            return true;
        }
        if (o instanceof Response response) {
            return Objects.equals(this.messages, response.messages);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(messages);
    }
}
