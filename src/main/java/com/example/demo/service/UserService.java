package com.example.demo.service;

import com.example.demo.model.User;
import java.util.Map;

/**
 * Internal service which is responsible for CRUD operations on a given user.
 */
public interface UserService
{

    /**
     * Retrieves a user by its ID.
     *
     * @param id the unique ID of the user.
     * @return the matching {@link User}, or {@code null} if not found.
     */
    User getUserById(Long id);

    /**
     * Retrieves a user by its ID bypassing the cache.
     *
     * @param id the unique ID of the user.
     * @return the matching {@link User}, or {@code null} if not found.
     */
    User getUserByIdBypassCache(Long id);

    /**
     * Refreshes the given {@link User} in the cache.
     * <p>
     * This method forces the cache to be updated with the latest data for the specified user.
     * It is typically used in a refresh-ahead pattern to keep frequently accessed users in cache.
     * </p>
     *
     * @param id the user id.
     */
    void refreshUser(Long id);


    /**
     * Adds a new user to the underlying data store and updates the cache.
     *
     * @param user the {@link User} instance to be added.
     * @return the newly added {@link User}.
     */
    User addUser(User user);

    /**
     * Removes a user with the specified ID from both the data store and the cache.
     *
     * @param id the unique ID of the user to remove.
     */
    void removeUser(Long id);

    /**
     * Updates an existing user in the data store and refreshes the corresponding cache entry.
     *
     * @param user the {@link User} instance with updated information.
     * @return the updated {@link User}, or {@code null} if the user does not exist.
     */
    User updateUser(User user);

    /**
     * Removes all users from both the cache and the underlying data store.
     */
    void removeAllUsers();

    /**
     * Retrieves an immutable view of all users currently in the data store.
     *
     * @return a {@link Map} of user IDs to {@link User} objects.
     */
    Map<Long, User> getAllUsers();
}
