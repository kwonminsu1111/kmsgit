package com.ssafy.enjoytrip.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.enjoytrip.dto.request.BoardCreateRequest;
import com.ssafy.enjoytrip.dto.response.BoardResponse;
import com.ssafy.enjoytrip.mapper.BoardMapper;
import com.ssafy.enjoytrip.model.Board;
import com.ssafy.enjoytrip.model.Hashtag;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {
 
    private final BoardMapper boardMapper;

    // 전체 조회 비즈니스 로직
    public List<BoardResponse> getAllBoards(String region, String orderBy, 
    		boolean myPostsOnly, Long userId, List<String> tags, boolean myTags) {
        Long filterUserId = myPostsOnly ? userId : null;
    	
    	return boardMapper.selectAllBoards(region, orderBy, filterUserId, tags, myTags, userId);
    }

    // 상세 조회 비즈니스 로직
    public BoardResponse getBoardById(Long boardId, Long userId) {
    	// 조회수 + 1
    	boardMapper.updateHit(boardId);
    	
    	// 1. 게시글 기본 정보 + 좋아요 여부(isLiked) 한 번에 가져오기
        BoardResponse boardResponse = boardMapper.selectBoardResponseById(boardId, userId);
        
        if (boardResponse != null) {
            // 2. 해당 게시글의 해시태그 목록을 DB에서 별도로 조회
            List<String> hashtags = boardMapper.selectHashtagsByBoardId(boardId);
            
            // 3. 응답 DTO 객체에 조회된 해시태그 리스트 주입하기
            boardResponse.setHashtags(hashtags);
        }
        
        return boardResponse;
    }
    
    // 좋아요 토글 로직
    @Transactional
    public boolean toggleLike(Long boardId, Long userId) {
        // 1. 이미 좋아요를 누른 상태인지 DB 체크
        int likeCount = boardMapper.checkLikeExists(boardId, userId);
        
        if (likeCount > 0) {
            // 이미 눌렀다면 -> 좋아요 취소 처리
            boardMapper.deleteLike(boardId, userId);
            boardMapper.updateLikeCount(boardId, -1); // Boards 테이블 하트 수 -1
            return false; // 최종 상태가 '안 누름' 상태임을 리턴
        } else {
            // 누른 적이 없다면 -> 좋아요 신규 등록 처리
            boardMapper.insertLike(boardId, userId);
            boardMapper.updateLikeCount(boardId, 1);  // Boards 테이블 하트 수 +1
            return true;  // 최종 상태가 '누름' 상태임을 리턴
        }
    }
    
    // ==========================================================
    // 자유게시판 CRUD 비즈니스 로직
    // ==========================================================
    @Transactional
    public boolean createBoard(BoardCreateRequest dto,
            Long loginUserId) {
        // 프론트에서 받은 DTO를 Entity로 변환
        Board board = new Board();
        board.setUserId(loginUserId);
        board.setPlanId(dto.getPlanId()); // 여행 계획 첨부 안 했으면 null
        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
        board.setStartDate(dto.getStartDate());
        board.setEndDate(dto.getEndDate());
        
        // 프론트에서 선택한 대표지역
        board.setRegion(dto.getRegion());

        int result = boardMapper.insertBoard(board);
        
        if (result > 0) {
            // 게시글 해시태그 매핑
            if (dto.getHashtags() != null && !dto.getHashtags().isEmpty()) {
                
                for (Integer tagId : dto.getHashtags()) {
                    // board.getId()로 방금 생성된 글 번호를 꺼내서 매핑 테이블에 꽂습니다.
                    boardMapper.insertBoardHashtags(board.getId(), tagId);
                }
                System.out.println("📢 [Service 로그] 게시글 번호 [" + board.getId() + "]에 해시태그 매핑 완료!");
            }
            return true;
        }
        
        return false;
    }

    // 원본 데이터 조회 (Read Entity)
    // 나중에 글을 수정/삭제할 때, "현재 로그인한 사람이 진짜 글 작성자가 맞는지" 비교하려면
    // DTO가 아니라 DB 원본 데이터인 Entity가 필요
    public Board getBoardEntityById(Long id) {
        return boardMapper.selectBoardById(id);
    }

    // 게시글 삭제 (Delete)
    @Transactional
    public boolean deleteBoard(Long id, Long userId) {
        Board board = boardMapper.selectBoardById(id);
        if (board == null) return false;

        // 본인 확인 (본인 체크)
        if (!board.getUserId().equals(userId)) {
            throw new RuntimeException("본인의 글만 삭제할 수 있습니다.");
        }

        return boardMapper.deleteBoard(id) > 0;
    }
    
    // 게시글 수정 (Update)
    @Transactional
    public boolean updateBoard(Long boardId, BoardCreateRequest dto, Long userId) {
    	// 1. DB에서 수정할 게시글 원본 가져오기
        Board board = boardMapper.selectBoardById(boardId);
        if (board == null) {
            return false;
        }

        // 2. 권한 검증: 현재 로그인한 유저가 글 작성자인지 확인
        if (!userId.equals(board.getUserId())) {
            throw new IllegalArgumentException("본인이 작성한 게시글만 수정할 수 있습니다.");
        }

        // 3. 자바 객체(Entity)에 수정된 데이터 덮어쓰기
        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
        board.setStartDate(dto.getStartDate());
        board.setEndDate(dto.getEndDate());
        board.setRegion(dto.getRegion());
        board.setPlanId(dto.getPlanId()); // 변경된 여행 계획 맵핑 (없으면 null)

        // 4. Boards 테이블 수정 실행
        int updateResult = boardMapper.updateBoard(board);

        if (updateResult > 0) {
            // 5. 해시태그 수정 처리 (Clear & Insert 전략)
            // 기존에 이 글에 달려있던 해시태그 매핑을 싹 비우기
            boardMapper.deleteBoardHashtags(boardId);

            // 프론트엔드가 보낸 새로운 해시태그 리스트가 있다면 다시 인서트
            if (dto.getHashtags() != null && !dto.getHashtags().isEmpty()) {
                for (Integer hashtagId : dto.getHashtags()) {
                    boardMapper.insertBoardHashtags(boardId, hashtagId);
                }
            }
            
            return true;
        }

        return false;
    }
    
    // 수정 시, 해시태그 불러오기
    public List<Hashtag> getAllHashtags() {
        return boardMapper.selectAllHashtags();
    }
}