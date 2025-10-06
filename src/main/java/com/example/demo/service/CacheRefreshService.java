package com.example.demo.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class CacheRefreshService {
    private final UserService userService;

    public CacheRefreshService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Asynchronously refreshes a user in the cache.
     * @param id the user ID to refresh
     */
    @Async
    public void refreshUserAhead(Long id) {
        userService.refreshUser(id); // dedicated refresh method in UserService
    }
}
