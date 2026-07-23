package com.ganesh.creatorflow.service.impl;

import com.ganesh.creatorflow.config.GoogleOAuthConfig;
import com.ganesh.creatorflow.service.YouTubeOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.ganesh.creatorflow.entity.User;
import com.ganesh.creatorflow.entity.YouTubeAccount;
import com.ganesh.creatorflow.repository.UserRepository;
import com.ganesh.creatorflow.repository.YouTubeAccountRepository;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import java.util.List;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class YouTubeOAuthServiceImpl implements YouTubeOAuthService {

    private final GoogleOAuthConfig config;
    private final UserRepository userRepository;
    private final YouTubeAccountRepository youtubeAccountRepository;
    @Override
    public String generateAuthorizationUrl() {

        String scope =
                "openid email profile https://www.googleapis.com/auth/youtube.upload https://www.googleapis.com/auth/youtube.readonly";

        return "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + config.getClientId()
                + "&redirect_uri=" + java.net.URLEncoder.encode(
                config.getRedirectUri(),
                java.nio.charset.StandardCharsets.UTF_8)
                + "&response_type=code"
                + "&access_type=offline"
                + "&prompt=consent"
                + "&scope=" + java.net.URLEncoder.encode(
                scope,
                java.nio.charset.StandardCharsets.UTF_8);
    }

    @Override
    public void connectAccount(String authorizationCode,
                               String creatorEmail) throws Exception {

        GoogleTokenResponse tokenResponse =
                new GoogleAuthorizationCodeTokenRequest(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        GsonFactory.getDefaultInstance(),
                        config.getClientId(),
                        config.getClientSecret(),
                        authorizationCode,
                        config.getRedirectUri()
                ).execute();

        String accessToken = tokenResponse.getAccessToken();
        String refreshToken = tokenResponse.getRefreshToken();

        YouTube youtube =
                new YouTube.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        GsonFactory.getDefaultInstance(),
                        request -> request.getHeaders().setAuthorization(
                                "Bearer " + accessToken))
                        .setApplicationName("CreatorFlow")
                        .build();

        ChannelListResponse response =
                youtube.channels()
                        .list(List.of("snippet"))
                        .setMine(true)
                        .execute();

        if (response.getItems().isEmpty()) {
            throw new RuntimeException("No YouTube channel found.");
        }

                String channelId =
                response.getItems().get(0).getId();

        String channelTitle =
                response.getItems().get(0)
                        .getSnippet()
                        .getTitle();

        User creator =
                userRepository.findByEmail(creatorEmail)
                        .orElseThrow(() ->
                                new RuntimeException("Creator not found"));

        YouTubeAccount account =
                youtubeAccountRepository
                        .findByCreator(creator)
                        .orElse(new YouTubeAccount());

        account.setCreator(creator);
        account.setChannelId(channelId);
        account.setChannelTitle(channelTitle);
        account.setAccessToken(accessToken);
        account.setRefreshToken(refreshToken);

        if (tokenResponse.getExpiresInSeconds() != null) {

            account.setTokenExpiry(
                    LocalDateTime.now()
                            .plusSeconds(tokenResponse.getExpiresInSeconds())
            );

        }

        youtubeAccountRepository.save(account);
    }
}