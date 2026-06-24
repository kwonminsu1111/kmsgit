package com.ssafy.enjoytrip.service;

import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.enjoytrip.dto.request.UserSignupRequest;
import com.ssafy.enjoytrip.dto.request.UserUpdateRequest;
import com.ssafy.enjoytrip.dto.response.UserCommentResponse;
import com.ssafy.enjoytrip.dto.response.UserLikedBoardResponse;
import com.ssafy.enjoytrip.dto.response.UserProfileResponse;
import com.ssafy.enjoytrip.mapper.UserMapper;
import com.ssafy.enjoytrip.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final UserMapper userMapper;

    @Transactional
    public boolean signup(UserSignupRequest dto) {
        validateSignupRequest(dto);

        if (userMapper.selectUserByEmail(dto.getEmail()) != null) {
            throw new IllegalArgumentException("이미 존재하는 Email입니다.");
        }

        String encryptedPassword = passwordEncoder.encode(dto.getPassword());
        User newUser = dto.toEntity(encryptedPassword);
        int result;
        
        try {
            result = userMapper.insertUser(newUser);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("이미 존재하는 Email입니다.");
        }
        return result > 0;
    }

    @Transactional(readOnly = true)
    public User login(String email, String password) {
        User user = userMapper.selectUserByEmail(email);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }

        return user;
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long userId) {
        return userMapper.selectUserById(userId) != null;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserInfo(Long userId) {
        User user = userMapper.selectUserById(userId);

        if (user == null) {
            throw new IllegalArgumentException("찾을 수 없는 User입니다.");
        }

        List<String> hashtagNames = userMapper.selectUserHashtagNames(userId);
        return UserProfileResponse.from(user, hashtagNames);
    }

    @Transactional
    public boolean updateUser(Long userId, UserUpdateRequest dto) {
        User user = userMapper.selectUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("찾을 수 없는 User입니다.");
        }

        if (dto.getCurrentPassword() == null || !passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        user.setNickname(dto.getNickname());

        if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        if (dto.getProfilePath() != null && !dto.getProfilePath().isBlank()) {
            user.setProfilePath(dto.getProfilePath());
        }

        if (dto.getHashtags() != null) {
            userMapper.deleteUserHashtags(userId);

            for (String tagName : dto.getHashtags()) {
                Integer tagId = userMapper.selectHashtagIdByName(tagName);
                if (tagId == null) {
                    throw new IllegalArgumentException("존재하지 않는 해시태그입니다: " + tagName);
                }
                userMapper.insertUserHashtag(userId, tagId);
            }
        }

        int affectedRows = userMapper.updateUser(user);
        return affectedRows > 0;
    }

    @Transactional
    public boolean updateProfileBase64(Long userId, String base64Image) {
        User user = userMapper.selectUserById(userId);

        if (user == null) {
            return false;
        }

        user.setProfilePath(base64Image);

        int affectedRows = userMapper.updateUser(user);
        return affectedRows > 0;
    }

    @Transactional
    public boolean deleteUser(Long userId) {
        if (userMapper.selectUserById(userId) == null) {
            return false;
        }

        int result = userMapper.deleteUser(userId);
        return result > 0;
    }

    public List<UserCommentResponse> getUserComments(Long userId) {
        return userMapper.selectUserComments(userId);
    }

    public List<UserLikedBoardResponse> getUserLikedBoards(Long userId) {
        return userMapper.selectUserLikedBoards(userId);
    }

    private void validateSignupRequest(UserSignupRequest dto) {
        if (dto == null || isBlank(dto.getEmail()) || isBlank(dto.getPassword()) || isBlank(dto.getNickname())) {
            throw new IllegalArgumentException("이메일, 패스워드, 닉네임은 필수 입력 항목입니다.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
