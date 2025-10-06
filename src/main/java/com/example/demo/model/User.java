package com.example.demo.model;

import java.time.Instant;

/**
 * Represents a user in the system.
 * * <p>This record is a transparent carrier for user data, including
 * identification details and timestamps, making it ideal for use as a
 * Data Transfer Object (DTO) or domain model entity.</p>
 * * @param id The unique identifier for the user.
 * @param name The full name or display name of the user.
 * @param email The primary email address of the user.
 * @param createdAt The timestamp indicating when the user record was created,
 * stored as a {@link java.time.Instant} to ensure precision and time-zone independence.
 */
public record User(Long id, String name, String email, Instant createdAt) {}