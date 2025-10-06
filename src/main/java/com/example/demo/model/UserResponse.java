package com.example.demo.model;

/**
 * The POJO of {@link User} response.
 */
public class UserResponse extends Response {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }
}
