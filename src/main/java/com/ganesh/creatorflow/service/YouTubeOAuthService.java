package com.ganesh.creatorflow.service;

public interface YouTubeOAuthService {

    String generateAuthorizationUrl();

    void connectAccount(String authorizationCode, String creatorEmail) throws Exception;

}