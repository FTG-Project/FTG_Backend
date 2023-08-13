package com.trip.triptogether.service.user;

import com.trip.triptogether.domain.User;
import com.trip.triptogether.dto.request.user.UserSaveRequest;
import com.trip.triptogether.dto.response.user.UserSaveResponse;
import com.trip.triptogether.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserSaveResponse signUp(String userEmail, UserSaveRequest userSaveDto) {
        User user = userRepository.findByEmail(userEmail).get();
        user.signUp(userSaveDto.getNickname(), userSaveDto.getLanguage());

        return UserSaveResponse.builder().userId(user.getId()).build();
    }

}
