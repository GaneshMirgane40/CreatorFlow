package com.ganesh.creatorflow.controller;

import com.ganesh.creatorflow.dto.YouTubeConnectRequest;
import com.ganesh.creatorflow.repository.YouTubeAccountRepository;
import com.ganesh.creatorflow.service.YouTubeOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/youtube/oauth")
@RequiredArgsConstructor
public class YouTubeOAuthController {

    private final YouTubeOAuthService youTubeOAuthService;
    private final YouTubeAccountRepository youTubeAccountRepository;

    @GetMapping("/connect")
    public String getAuthorizationUrl() {

        return youTubeOAuthService.generateAuthorizationUrl();

    }

    @PostMapping("/connect")
    public String connectYouTube(
            @RequestBody YouTubeConnectRequest request,
            Authentication authentication
    ) throws Exception {

        String creatorEmail = authentication.getName();

        youTubeOAuthService.connectAccount(
                request.getCode(),
                creatorEmail
        );

        return "YouTube connected successfully.";

    }

    /** Whether the logged-in creator has a connected YouTube channel. */
    @GetMapping("/status")
    public Map<String, Object> getConnectionStatus(Authentication authentication) {
        Map<String, Object> status = new LinkedHashMap<>();

        youTubeAccountRepository.findByCreatorEmail(authentication.getName())
                .ifPresentOrElse(account -> {
                    status.put("connected", true);
                    status.put("channelTitle", account.getChannelTitle());
                    status.put("channelId", account.getChannelId());
                }, () -> status.put("connected", false));

        return status;
    }
}
