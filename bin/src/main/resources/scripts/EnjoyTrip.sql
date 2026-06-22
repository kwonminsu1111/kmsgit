SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS enjoytrip;

USE enjoytrip;

-- 기존 테이블이 존재할 경우 안전하게 삭제 (초기화 목적)
DROP TABLE IF EXISTS board_hashtag;
DROP TABLE IF EXISTS Board_Like;
DROP TABLE IF EXISTS Comments;
DROP TABLE IF EXISTS Review;
DROP TABLE IF EXISTS User_Hashtag;
DROP TABLE IF EXISTS Hashtag;
DROP TABLE IF EXISTS Boards;
DROP TABLE IF EXISTS Plans_Details;
DROP TABLE IF EXISTS Plans;
DROP TABLE IF EXISTS Attractions;
DROP TABLE IF EXISTS contenttypes;
DROP TABLE IF EXISTS guguns;
DROP TABLE IF EXISTS sidos;
DROP TABLE IF EXISTS Users;

-- ==========================================
-- 1. 유저 (Users)
-- ==========================================
CREATE TABLE Users (
    id           VARCHAR(20)   NOT NULL,
    nickname     VARCHAR(20)   NOT NULL,
    email        VARCHAR(50)   NOT NULL,
    password     VARCHAR(255)  NOT NULL,
    profile_path TEXT          NOT NULL,
    reg_date     DATETIME      NOT NULL,
    role         ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    PRIMARY KEY (id)
);

-- ==========================================
-- 2. 해시태그 및 유저매핑
-- ==========================================
CREATE TABLE Hashtag (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    tag_name     VARCHAR(255)  NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE User_Hashtag (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    user_id      VARCHAR(20)   NOT NULL, -- Users.id 참조를 위해 VARCHAR(20)로 매칭
    hashtag_id   BIGINT        NOT NULL,
    PRIMARY KEY (id)
);

-- ==========================================
-- 3. 지역 및 여행지 마스터 데이터
-- ==========================================
CREATE TABLE sidos (
    sido_id      BIGINT        NOT NULL,
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    sido_name    VARCHAR(255)  NOT NULL,
    PRIMARY KEY (sido_id), -- 구군에서 외래키로 엮기 편하게 sido_id를 PK로 지정
    UNIQUE KEY (id)
);

CREATE TABLE guguns (
    gugun_id     BIGINT        NOT NULL,
    sido_id      BIGINT        NOT NULL,
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    gugun_name   VARCHAR(255)  NOT NULL,
    PRIMARY KEY (gugun_id),
    UNIQUE KEY (id)
);

CREATE TABLE contenttypes (
    no           BIGINT        NOT NULL AUTO_INCREMENT,
    content_type_name VARCHAR(255) NULL,
    PRIMARY KEY (no)
);

CREATE TABLE Attractions (
    no              BIGINT        NOT NULL AUTO_INCREMENT,
    content_type_id BIGINT        NOT NULL,
    sido_id         BIGINT        NOT NULL,
    gugun_id        BIGINT        NOT NULL,
    title           VARCHAR(255)  NULL,
    description     VARCHAR(255)  NULL,
    first_image1    VARCHAR(255)  NULL,
    addr1           VARCHAR(255)  NULL,
    longitude       DOUBLE        NULL,
    latitude        DOUBLE        NULL,
    PRIMARY KEY (no)
);

-- ==========================================
-- 4. 여행 계획 (Plans)
-- ==========================================
CREATE TABLE Plans (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    user_id      VARCHAR(20)   NOT NULL, -- Users.id 참조용
    title        VARCHAR(20)   NOT NULL,
    start_date   DATE          NULL,
    end_date     DATE          NULL,
    status       ENUM('PLANNING', 'COMPLETED') NOT NULL DEFAULT 'PLANNING',
    created_at   DATETIME      NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Plans_Details (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    plan_id      BIGINT        NOT NULL,
    id2          BIGINT        NOT NULL, -- Attractions.no 참조용
    sequence     INT           NOT NULL,
    visit_time   DATETIME      NULL,
    PRIMARY KEY (id)
);

-- ==========================================
-- 5. 커뮤니티 (게시판, 댓글, 좋아요, 리뷰)
-- ==========================================
CREATE TABLE Boards (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    user_id      VARCHAR(20)   NOT NULL, -- Users.id 참조용
    plan_id      BIGINT        NULL,
    title        VARCHAR(100)  NOT NULL,
    content      TEXT          NOT NULL,
    start_date   DATE          NULL,     -- ⭕ [추가됨] 포스팅에 보여질 여행 시작일
    end_date     DATE          NULL,     -- ⭕ [추가됨] 포스팅에 보여질 여행 종료일
    hit          INT           NOT NULL DEFAULT 0,
    created_at   DATETIME      NOT NULL,
    like_count   INT           NOT NULL DEFAULT 0,
    region		 VARCHAR(20)   NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Comments (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    board_id     BIGINT        NOT NULL,
    user_id      VARCHAR(20)   NOT NULL, -- Users.id 참조용
    content      TEXT          NOT NULL,
    created_at   DATETIME      NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Board_Like (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    board_id     BIGINT        NOT NULL,
    user_id      VARCHAR(20)   NOT NULL, -- Users.id 참조용
    PRIMARY KEY (id),
    UNIQUE KEY unique_like (board_id, user_id)
);

CREATE TABLE board_hashtag (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    hashtag_id   BIGINT        NOT NULL,
    board_id     BIGINT        NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Review (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    field_id     BIGINT        NOT NULL, -- Attractions.no 참조용
    user_id      VARCHAR(20)   NOT NULL, -- Users.id 참조용
    created_at   DATETIME      NOT NULL,
    content      TEXT          NOT NULL,
    rate         DOUBLE        NOT NULL DEFAULT 0.0,
    PRIMARY KEY (id)
);

-- ==========================================
-- 6. 외래키(FK) 제약조건 연결 일괄 추가
-- ==========================================
ALTER TABLE User_Hashtag ADD CONSTRAINT FK_UserHashtag_User FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE;
ALTER TABLE User_Hashtag ADD CONSTRAINT FK_UserHashtag_Tag FOREIGN KEY (hashtag_id) REFERENCES Hashtag (id) ON DELETE CASCADE;

ALTER TABLE guguns ADD CONSTRAINT FK_Guguns_Sido FOREIGN KEY (sido_id) REFERENCES sidos (sido_id) ON DELETE CASCADE;

ALTER TABLE Attractions ADD CONSTRAINT FK_Attractions_Type FOREIGN KEY (content_type_id) REFERENCES contenttypes (no);
ALTER TABLE Attractions ADD CONSTRAINT FK_Attractions_Sido FOREIGN KEY (sido_id) REFERENCES sidos (sido_id);
ALTER TABLE Attractions ADD CONSTRAINT FK_Attractions_Gugun FOREIGN KEY (gugun_id) REFERENCES guguns (gugun_id);

ALTER TABLE Plans ADD CONSTRAINT FK_Plans_User FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE;

ALTER TABLE Plans_Details ADD CONSTRAINT FK_Details_Plan FOREIGN KEY (plan_id) REFERENCES Plans (id) ON DELETE CASCADE;
ALTER TABLE Plans_Details ADD CONSTRAINT FK_Details_Attraction FOREIGN KEY (id2) REFERENCES Attractions (no) ON DELETE CASCADE;

ALTER TABLE Boards ADD CONSTRAINT FK_Boards_User FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE;
ALTER TABLE Boards ADD CONSTRAINT FK_Boards_Plan FOREIGN KEY (plan_id) REFERENCES Plans (id) ON DELETE CASCADE;

ALTER TABLE Comments ADD CONSTRAINT FK_Comments_Board FOREIGN KEY (board_id) REFERENCES Boards (id) ON DELETE CASCADE;
ALTER TABLE Comments ADD CONSTRAINT FK_Comments_User FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE;

ALTER TABLE Board_Like ADD CONSTRAINT FK_Like_Board FOREIGN KEY (board_id) REFERENCES Boards (id) ON DELETE CASCADE;
ALTER TABLE Board_Like ADD CONSTRAINT FK_Like_User FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE;

ALTER TABLE board_hashtag ADD CONSTRAINT FK_BoardTag_Hashtag FOREIGN KEY (hashtag_id) REFERENCES Hashtag (id) ON DELETE CASCADE;
ALTER TABLE board_hashtag ADD CONSTRAINT FK_BoardTag_Board FOREIGN KEY (board_id) REFERENCES Boards (id) ON DELETE CASCADE;

ALTER TABLE Review ADD CONSTRAINT FK_Review_Attraction FOREIGN KEY (field_id) REFERENCES Attractions (no) ON DELETE CASCADE;
ALTER TABLE Review ADD CONSTRAINT FK_Review_User FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE;

-- ⚠️ 외래키 체크 설정을 다시 원상복구 시킵니다.
SET FOREIGN_KEY_CHECKS = 1;

-- 기존 데이터 청소 (선택 사항)
DELETE FROM Users;

INSERT INTO Users (
    id, 
    nickname, 
    email, 
    password, 
    profile_path, 
    reg_date, 
    role
) VALUES 
(
    'admin', 
    '최고관리자', 
    'admin@ssafy.com', 
    '1234', 
    '/images/profile/admin.png', 
    NOW(), 
    'ADMIN'
),
(
    'ssafy', 
    '김싸피', 
    'ssafy@ssafy.com', 
    '1234', 
    '/images/profile/default.png', 
    NOW(), 
    'USER'
),
(
    'testuser', 
    '일반테스터', 
    'test@ssafy.com', 
    '1234', 
    '/images/profile/default.png', 
    NOW(), 
    'USER'
);



-- 1. 테스트용 게시글 1개 넣기 (제주도 여행: 2026-07-01 ~ 2026-07-04)
INSERT INTO `Boards` (`user_id`, `title`, `content`, `start_date`, `end_date`, `hit`, `created_at`, `like_count`, `region`)
VALUES ('ssafy', '제주도 3박 4일 꿀코스 공유합니다', '진짜 역대급 힐링 여행이었습니다. 꼭 가보세요!', '2026-07-01', '2026-07-04', 0, NOW(), 0, '제주도');

-- 2. 다른 유저가 쓴 게시글 추가 (서울 여행: 2026-08-10 ~ 2026-08-11)
INSERT INTO `Boards` (`user_id`, `title`, `content`, `start_date`, `end_date`, `hit`, `created_at`, `like_count`, `region`)
VALUES ('testuser', '서울 경복궁 야간개장 후기', '진짜 야경이 끝내줍니다. 꼭 가보세요!', '2026-08-10', '2026-08-11', 15, NOW(), 1, '서울');

-- 3. 다른 유저가 쓴 게시글 추가 (강릉 여행: 당일치기)
INSERT INTO `Boards` (`user_id`, `title`, `content`, `start_date`, `end_date`, `hit`, `created_at`, `like_count`, `region`)
VALUES ('admin', '강릉 커피거리 추천', '안목해변에서 마시는 커피 최고에요.', '2026-09-05', '2026-09-05', 102, NOW(), 5, '강릉');

-- 1. 방금 만든 1번 게시글에 내가 작성한 가짜 댓글 2개 달기
INSERT INTO `Comments` (`board_id`, `user_id`, `content`, `created_at`)
VALUES (1, 'ssafy', '와 제가 찾던 코스네요! 저장해갑니다 ㅎㅎ', NOW());

INSERT INTO `Comments` (`board_id`, `user_id`, `content`, `created_at`)
VALUES (1, 'ssafy', '혹시 두 번째 날에 갔던 카페 이름이 뭔지 알 수 있을까요?', NOW());

-- 2. ssafy 유저가 '서울 여행 꿀팁' 글(1번글)에 '좋아요' 누르기
-- (Board_Like 테이블에 데이터가 들어가야 좋아요 목록 조회 기능이 테스트됨)
INSERT INTO `Board_Like` (`board_id`, `user_id`) 
VALUES (1, 'ssafy');

-- ssafy, admin이 테스터의 글에 좋아요 누름
INSERT INTO `Board_Like` (board_id, user_id) VALUES (2, 'ssafy');
INSERT INTO `Board_Like` (board_id, user_id) VALUES (2, 'admin');

-- 3. testuser가 작성한 글(2번글)에도 ssafy 유저가 댓글 달아보기
INSERT INTO Comments (board_id, user_id, content, created_at) VALUES 
(1, 'testuser', '와 제가 찾던 코스네요! 저장해갑니다 ㅎㅎ', NOW()),
(1, 'admin', '혹시 두 번째 날에 갔던 카페 이름이 뭔지 알 수 있을까요?', NOW()),
(2, 'ssafy', '저도 지난주에 다녀왔는데 야경 진짜 대박이더라고요!', NOW());



-- 1번부터 10번까지의 기본 해시태그 생성
INSERT INTO Hashtag (id, tag_name) VALUES 
(1, '힐링'),
(2, '액티비티'),
(3, '맛집투어'),
(4, '카페투어'),
(5, '호캉스'),
(6, '자연경관'),
(7, '역사유적'),
(8, '쇼핑'),
(9, '혼자여행'),
(10, '가족여행');