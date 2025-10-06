package com.example.demo.service;

/**
 * Interface for service responsible for asynchronously refreshing cached user data.
 */
public interface CacheRefreshService {

    /**
     * Asynchronously refreshes a user in the cache.
     * The implementation is typically responsible for calling a service method
     * that uses @CachePut to update the cache entry.
     * * @param id the user ID to refresh
     */
    void refreshUserAhead(Long id);
}
