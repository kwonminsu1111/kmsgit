package com.ssafy.enjoytrip.service;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.enjoytrip.dto.request.UserSignupRequest;
import com.ssafy.enjoytrip.dto.request.UserUpdateRequest;
import com.ssafy.enjoytrip.dto.response.UserCommentResponse;
import com.ssafy.enjoytrip.dto.response.UserLikedBoardResponse;
import com.ssafy.enjoytrip.mapper.UserMapper; // ⚠️ 매퍼 인터페이스 임포트 필요!
import com.ssafy.enjoytrip.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // Mapper 주입을 위해 추가합니다.
public class UserService {

    // 진짜 MySQL DB를 호출해 줄 매퍼를 주입받습니다.
    private final UserMapper userMapper;

    // 1. 회원가입
    @Transactional
    public boolean signup(UserSignupRequest dto) {
        if (userMapper.selectUserById(dto.getId()) != null) {
            return false; // 이미 존재하는 아이디면 실패
        }
        
        String encryptedPassword = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());
        dto.setPassword(encryptedPassword); // 암호화된 비밀번호로 바꾸기
        
        User newUser = dto.toEntity();
        int result = userMapper.insertUser(newUser);
        return result > 0;
    }

    // 2. 로그인
    @Transactional(readOnly = true)
    public User login(String email, String password) {
        User user = userMapper.selectUserByEmail(email);
        
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            return null; // 로그인 실패
        }
        
        return user; // 로그인 성공
    }

    // 3. 유저 단건 조회 (Read), 토큰에서 해독된 userId를 받음
    @Transactional(readOnly = true)
    public User getUserInfo(String userId) {
    	User user = userMapper.selectUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 회원 정보입니다.");
        }
        return user;
    }

    // 4. 유저 정보 수정 (Update)
    @Transactional
    public boolean updateUser(String userId, UserUpdateRequest dto) {
    	// 1) DB에서 진짜 회원 정보 스캔
        User user = userMapper.selectUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 회원 정보입니다.");
        }
        
        // 본인 확인을 위해 입력한 '현재 비밀번호'가 DB 장부와 다른 경우 차단!
        if (dto.getCurrentPassword() == null || !user.getPassword().equals(dto.getCurrentPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        
        // 2) 데이터 정제 및 덮어쓰기
        user.setNickname(dto.getNickname());
        
        // 새로운 비밀번호를 바꾸겠다고 폼에 적었을 때만 교체 집도 (dto 필드명 일치 완료)
        if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
            user.setPassword(dto.getNewPassword());
        }
        
        // 프로필 사진을 이 양식 가방에서 꺼내 한방에 수정합니다.
        // 사진경로에 대한 값이 들어왔을 때만 변경을 집도합니다.
        if (dto.getProfilePath() != null && !dto.getProfilePath().isBlank()) {
            user.setProfilePath(dto.getProfilePath());
        }
        
        // 3) 해시태그 ID 배열 동기화
        userMapper.deleteUserHashtags(userId); // 기존에 골라놨던 옛날 태그 싹 청소
        if (dto.getHashtags() != null) {
            for (Integer tagId : dto.getHashtags()) {
                userMapper.insertUserHashtag(userId, tagId); // 새로운 취향 태그 리스트 인서트
            }
        }
        
        int affectedRows = userMapper.updateUser(user);
        return affectedRows > 0;
    }
    
    // 5. 유저 프로필 수정 (?)
    @Transactional
    public boolean updateProfileBase64(String userId, String base64Image) {
        // 1) DB에서 이 유저가 진짜 존재하는지 확인
        User user = userMapper.selectUserById(userId);
        
        if (user == null) {
            return false;
        }

        // 2) 엔티티 객체의 profilePath 변수에 긴 Base64 텍스트 문자열을 세팅
        user.setProfilePath(base64Image);

        // 3) 기존에 잘 돌아가던 유저 업데이트 매퍼(XML)를 재활용해서 SQL 실행!
        int affectedRows = userMapper.updateUser(user);
        return affectedRows > 0;
    }

    // 6. 회원 탈퇴 및 유저 삭제 (Delete)
    @Transactional
    public boolean deleteUser(String userId) {
    	if (userMapper.selectUserById(userId) == null) {
            return false;
        }
    	
        int result = userMapper.deleteUser(userId); // 기존 매퍼의 SQL 문 그대로 재활용!
        
        return result > 0;
    }
    
    
    // ====================================================================================================
    // [유저 활동 조회]
    // ====================================================================================================

    // 7. 내가 작성한 댓글 목록 조회
    public List<UserCommentResponse> getUserComments(String userId) {
        return userMapper.selectUserComments(userId);
    }

    // 9. 내가 좋아요 한 게시글 목록 조회
    public List<UserLikedBoardResponse> getUserLikedBoards(String userId) {
        return userMapper.selectUserLikedBoards(userId);
    }
}