package com.ganesh.creatorflow.service;

import com.ganesh.creatorflow.entity.User;

public interface YouTubeOAuthService {

    String generateAuthorizationUrl();

    void connectAccount(String authorizationCode, String creatorEmail) throws Exception;

    String getValidAccessToken(User creator);
}
