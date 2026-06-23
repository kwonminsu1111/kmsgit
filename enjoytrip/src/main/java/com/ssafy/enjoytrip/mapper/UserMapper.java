package com.ssafy.enjoytrip.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.enjoytrip.dto.response.UserCommentResponse;
import com.ssafy.enjoytrip.dto.response.UserLikedBoardResponse;
import com.ssafy.enjoytrip.model.User;

@Mapper // 스프링이 MyBatis 매퍼로 인식하도록 붙임
public interface UserMapper {
    int insertUser(User user);
    User selectUserById(Long id);
    User selectUserByEmail(String email);
    List<User> selectAllUsers();
    int updateUser(User user);
    int deleteUser(Long id);
    
    List<UserCommentResponse> selectUserComments(Long userId);
    List<UserLikedBoardResponse> selectUserLikedBoards(Long userId);
    
    // =====================================
    
    // 사용자 id로 해시태그 이름 목록을 조회
    List<String> selectUserHashtagNames(Long userId);
    // 특정 유저의 기존 해시태그 매핑을 싹 다 비우는 도구
    int deleteUserHashtags(Long userId);
    // 유저 ID와 해시태그 ID를 1:1로 매핑 테이블에 꽂는 도구
    int insertUserHashtag(@Param("userId") Long userId, @Param("hashtagId") Integer hashtagId);
}