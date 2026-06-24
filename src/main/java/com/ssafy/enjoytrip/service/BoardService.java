package com.ssafy.enjoytrip.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.enjoytrip.dto.request.BoardCreateRequest;
import com.ssafy.enjoytrip.dto.request.BoardUpdateRequest;
import com.ssafy.enjoytrip.dto.response.BoardLikeResponse;
import com.ssafy.enjoytrip.dto.response.BoardResponse;
import com.ssafy.enjoytrip.dto.response.CommentResponse;
import com.ssafy.enjoytrip.mapper.BoardMapper;
import com.ssafy.enjoytrip.mapper.CommentMapper;
import com.ssafy.enjoytrip.model.Board;
import com.ssafy.enjoytrip.model.Hashtag;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardMapper boardMapper;
    private final CommentMapper commentMapper;

    public List<BoardResponse> getAllBoards(
            String region,
            String orderBy,
            boolean myPostsOnly,
            Long userId,
            List<String> tags,
            boolean myTags
    ) {
        Long filterUserId = myPostsOnly ? userId : null;
        List<BoardResponse> boards = boardMapper.selectAllBoards(region, orderBy, filterUserId, tags, myTags, userId);
        for (BoardResponse board : boards) {
            board.setTags(boardMapper.selectHashtagsByBoardId(board.getBoardId()));
        }
        return boards;
    }

    public BoardResponse getBoardById(Long boardId, Long userId) {
        boardMapper.updateHit(boardId);

        BoardResponse boardResponse = boardMapper.selectBoardResponseById(boardId, userId);
        if (boardResponse != null) {
            boardResponse.setTags(boardMapper.selectHashtagsByBoardId(boardId));
            List<CommentResponse> comments = commentMapper.selectCommentsByBoardId(boardId);
            boardResponse.setComments(comments);
        }

        return boardResponse;
    }

    @Transactional
    public BoardLikeResponse toggleLike(Long boardId, Long userId) {
        int likeCount = boardMapper.checkLikeExists(boardId, userId);

        boolean isLiked;
        if (likeCount > 0) {
            boardMapper.deleteLike(boardId, userId);
            boardMapper.updateLikeCount(boardId, -1);
            isLiked = false;
        } else {
            boardMapper.insertLike(boardId, userId);
            boardMapper.updateLikeCount(boardId, 1);
            isLiked = true;
        }

        return new BoardLikeResponse(isLiked, boardMapper.selectLikeCount(boardId));
    }

    @Transactional
    public Long createBoard(BoardCreateRequest dto, Long loginUserId) {
        Board board = new Board();
        board.setUserId(loginUserId);
        board.setPlanId(dto.getPlanId());
        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
        board.setStartDate(dto.getStartDate());
        board.setEndDate(dto.getEndDate());
        board.setRegion(dto.getRegion());

        int result = boardMapper.insertBoard(board);
        if (result == 0) {
            throw new IllegalStateException("게시글 등록에 실패했습니다.");
        }

        saveBoardTags(board.getId(), dto.getTags());
        return board.getId();
    }

    public Board getBoardEntityById(Long id) {
        return boardMapper.selectBoardById(id);
    }

    @Transactional
    public boolean deleteBoard(Long id, Long userId) {
        Board board = boardMapper.selectBoardById(id);
        if (board == null) {
            return false;
        }
        if (!board.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인이 작성한 게시글만 삭제할 수 있습니다.");
        }

        return boardMapper.deleteBoard(id) > 0;
    }

    @Transactional
    public boolean updateBoard(Long boardId, BoardUpdateRequest dto, Long userId) {
        Board board = boardMapper.selectBoardById(boardId);
        if (board == null) {
            return false;
        }
        if (!userId.equals(board.getUserId())) {
            throw new IllegalArgumentException("본인이 작성한 게시글만 수정할 수 있습니다.");
        }

        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());

        int updateResult = boardMapper.updateBoard(board);
        if (updateResult == 0) {
            return false;
        }

        boardMapper.deleteBoardHashtags(boardId);
        saveBoardTags(boardId, dto.getTags());
        return true;
    }

    public List<Hashtag> getAllHashtags() {
        return boardMapper.selectAllHashtags();
    }

    private void saveBoardTags(Long boardId, List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return;
        }

        for (String tagName : tags) {
            Integer tagId = boardMapper.selectHashtagIdByName(tagName);
            if (tagId == null) {
                throw new IllegalArgumentException("존재하지 않는 해시태그입니다: " + tagName);
            }
            boardMapper.insertBoardHashtags(boardId, tagId);
        }
    }
}
