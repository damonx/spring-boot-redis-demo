package com.example.demo.model;

import java.time.Instant;

public record User(Long id, String name, String email, Instant createdAt) {}