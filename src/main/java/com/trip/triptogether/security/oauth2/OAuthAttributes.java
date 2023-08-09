package com.trip.triptogether.security.oauth2;

import com.trip.triptogether.constant.Role;
import com.trip.triptogether.domain.User;
import com.trip.triptogether.security.oauth2.userinfo.GoogleOAuth2UserInfo;
import com.trip.triptogether.security.oauth2.userinfo.OAuth2UserInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
public class OAuthAttributes {

    private String nameAttributeKey;
    private OAuth2UserInfo oauth2UserInfo;

    @Builder
    public OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oauth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    // 우리 서비스에서는 구글 로그인만 있음.
    public static OAuthAttributes of(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                .build();
    }

    public User toEntity(OAuth2UserInfo oauth2UserInfo) {
        return User.builder()
                .socialId(oauth2UserInfo.getId())
                .email(UUID.randomUUID() + "@trip-together.com")
                .nickname(oauth2UserInfo.getNickname())
                .role(Role.GUEST)
                .build();

    }
}
