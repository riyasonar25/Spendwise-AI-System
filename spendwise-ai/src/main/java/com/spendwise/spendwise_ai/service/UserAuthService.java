package com.spendwise.spendwise_ai.service;

public interface UserAuthService {

    String register(String name, String email, String password);

    String login(String email, String password);
}
