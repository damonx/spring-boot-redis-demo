package com.example.demo.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link CacheRefreshService}.
 */
@Service
public class CacheRefreshServiceImpl implements CacheRefreshService
{
    private final UserService userService;

    public CacheRefreshServiceImpl(UserService userService)
    {
        this.userService = userService;
    }

    /**
     * Asynchronously refreshes a user in the cache.
     *
     * @param id the user ID to refresh
     */
    @Async
    public void refreshUserAhead(Long id)
    {
        userService.refreshUser(id); // dedicated refresh method in UserService
    }
}
