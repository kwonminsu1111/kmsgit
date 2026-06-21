SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS enjoytrip;

USE enjoytrip;

-- 기존 테이블이 존재할 경우 안전하게 삭제 (초기화 목적)
DROP TABLE IF EXISTS Board_Hashtag;
DROP TABLE IF EXISTS Board_Like;
DROP TABLE IF EXISTS Comments;
DROP TABLE IF EXISTS Reviews;
DROP TABLE IF EXISTS User_Hashtag;
DROP TABLE IF EXISTS Hashtag;
DROP TABLE IF EXISTS Boards;
DROP TABLE IF EXISTS Plans_Details;
DROP TABLE IF EXISTS Plans;
DROP TABLE IF EXISTS Attractions;
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

CREATE TABLE Attractions (
    id              BIGINT        NOT NULL,
    PRIMARY KEY (id)
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
    status       ENUM('ONGOING', 'COMPLETED') NOT NULL DEFAULT 'ONGOING',
    created_at   DATETIME      NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Plans_Details (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    place_id     BIGINT        NOT NULL,
    plan_id      BIGINT        NOT NULL, -- Attractions.no 참조용
    sequence     INT           NOT NULL,
    day		     INT	       NOT NULL,
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

CREATE TABLE Board_Hashtag (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    hashtag_id   BIGINT        NOT NULL,
    board_id     BIGINT        NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Reviews (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    place_id     BIGINT        NOT NULL, -- Attractions.id 참조
    user_id      VARCHAR(20)   NOT NULL, -- Users.id 참조
    rate         INT           NOT NULL, -- 개별 평점 정수 고정 (1~5)
    content      TEXT          NOT NULL, -- 후기 본문
    created_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT CK_Review_Rate CHECK (rate BETWEEN 1 AND 5)
);

-- ==========================================
-- 6. 외래키(FK) 제약조건 연결 일괄 추가
-- ==========================================
ALTER TABLE User_Hashtag ADD CONSTRAINT FK_UserHashtag_User FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE;
ALTER TABLE User_Hashtag ADD CONSTRAINT FK_UserHashtag_Tag FOREIGN KEY (hashtag_id) REFERENCES Hashtag (id) ON DELETE CASCADE;

ALTER TABLE Plans ADD CONSTRAINT FK_Plans_User FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE;

ALTER TABLE Plans_Details ADD CONSTRAINT FK_Details_Plan FOREIGN KEY (plan_id) REFERENCES Plans (id) ON DELETE CASCADE;
ALTER TABLE Plans_Details ADD CONSTRAINT FK_Details_Attraction FOREIGN KEY (place_id) REFERENCES Attractions (id) ON DELETE CASCADE;

ALTER TABLE Boards ADD CONSTRAINT FK_Boards_User FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE;
ALTER TABLE Boards ADD CONSTRAINT FK_Boards_Plan FOREIGN KEY (plan_id) REFERENCES Plans (id) ON DELETE CASCADE;

ALTER TABLE Comments ADD CONSTRAINT FK_Comments_Board FOREIGN KEY (board_id) REFERENCES Boards (id) ON DELETE CASCADE;
ALTER TABLE Comments ADD CONSTRAINT FK_Comments_User FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE;

ALTER TABLE Board_Like ADD CONSTRAINT FK_Like_Board FOREIGN KEY (board_id) REFERENCES Boards (id) ON DELETE CASCADE;
ALTER TABLE Board_Like ADD CONSTRAINT FK_Like_User FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE;

ALTER TABLE board_hashtag ADD CONSTRAINT FK_BoardTag_Hashtag FOREIGN KEY (hashtag_id) REFERENCES Hashtag (id) ON DELETE CASCADE;
ALTER TABLE board_hashtag ADD CONSTRAINT FK_BoardTag_Board FOREIGN KEY (board_id) REFERENCES Boards (id) ON DELETE CASCADE;

ALTER TABLE Reviews ADD CONSTRAINT FK_Review_Place FOREIGN KEY (place_id) REFERENCES Attractions (id) ON DELETE CASCADE;
ALTER TABLE Reviews ADD CONSTRAINT FK_Review_User FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE;

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
VALUES ('admin', '강릉 커피거리 추천', '안목해변에서 마시는 커피 최고에요.', '2026-09-05', '2026-09-05', 102, NOW(), 5, '강원특별자치도');

-- 1. 방금 만든 1번 게시글에 내가 작성한 가짜 댓글 2개 달기
INSERT INTO `Comments` (`board_id`, `user_id`, `content`, `created_at`)
VALUES (1, 'ssafy', '와 제가 찾던 코스네요! 저장해갑니다 ㅎㅎ', NOW());

INSERT INTO `Comments` (`board_id`, `user_id`, `content`, `created_at`)
VALUES (1, 'ssafy', '혹시 두 번째 날에 갔던 카페  수 있을까요?', NOW());

-- 2. ssafy 유저가 '서울 여행 꿀팁' 글(1번글)에 '좋아요' 누르기
-- (Board_Like 테이블에 데이터가 들어가야 좋아요 목록 조회 기능이 테스트됨)
INSERT INTO `Board_Like` (`board_id`, `user_id`) 
VALUES (1, 'ssafy');

-- ssafy, admin이 테스터의 글에 좋아요 누름
INSERT INTO `Board_Like` (board_id, user_id) VALUES (2, 'ssafy');
INSERT INTO `Board_Like` (board_id, user_id) VALUES (2, 'admin');

-- 3. testuser가 작성한 글(2번글)에도 ssafy 유저가 댓글 달아보기
INSERT INTO `Comments` (board_id, user_id, content, created_at) VALUES 
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



-- 여행지 데이터
INSERT INTO Attractions (id) VALUES
-- [서울 권역]
(101),
(102),
(103),

-- [제주도 권역]
(201),
(202),
(203),

-- [경북 권역]
(301),
(302),

-- [강원 권역]
(401),
(402);



-- 유저 해시태그 데이터
INSERT INTO User_Hashtag (user_id, hashtag_id) VALUES
('ssafy', 3),
('ssafy', 10),
('testuser', 1),
('testuser', 9);

-- 게시물 해시태그 데이터
INSERT INTO Board_Hashtag (board_id, hashtag_id) VALUES
(1, 1),
(1, 3),
(2, 7),
(2, 3);



-- 여행 계획 (Plans) 데이터
INSERT INTO Plans (id, user_id, title, start_date, end_date, status, created_at) VALUES
(1, 'ssafy', '서울 먹부림 여행', '2026-07-10', '2026-07-11', 'ONGOING', NOW()),
(2, 'ssafy', '제주 호캉스&힐링', '2026-05-01', '2026-05-03', 'COMPLETED', '2026-05-01 10:00:00'),
(3, 'testuser', '강릉 바다 혼행', '2026-08-20', '2026-08-20', 'ONGOING', NOW());



-- 여행 세부 계획 (Plans_Details) 데이터
-- [1번 플랜: 서울 1박 2일]
-- 1일차: 경복궁(101, 순서1) ➔ 우래옥(102, 순서2)
-- 2일차: 국립중앙박물관(103, 순서1)
INSERT INTO Plans_Details (plan_id, place_id, day, sequence) VALUES
(1, 101, 1, 1),
(1, 102, 1, 2),
(1, 103, 2, 1);

-- [2번 플랜: 제주도 2박 3일]
-- 1일차: 성산일출봉(201, 순서1)
-- 2일차: 춘심이네(202, 순서1) ➔ 신라호텔(203, 순서2)
INSERT INTO Plans_Details (plan_id, place_id, day, sequence) VALUES
(2, 201, 1, 1),
(2, 202, 2, 1),
(2, 203, 2, 2);

-- [3번 플랜: 강릉 당일치기]
-- 1일차: 안목해변(401, 순서1) ➔ 엄지네포장마차(402, 순서2)
INSERT INTO Plans_Details (plan_id, place_id, day, sequence) VALUES
(3, 401, 1, 1),
(3, 402, 1, 2);



-- 장소 리뷰 데이터
INSERT INTO Reviews (place_id, user_id, rate, content, created_at) VALUES
(101, 'ssafy', 5, '경복궁 야간개장은 진짜 무조건 가야합니다. 한복 입으면 무료라 너무 좋았어요!', '2026-06-10 14:00:00'),
(101, 'testuser', 4, '낮에 가도 웅장하고 산책하기 딱 좋습니다. 외국인 친구 데려가기 좋아요.', '2026-06-11 11:20:00'),
(101, 'admin', 4, '관리가 아주 잘 되어 있어서 서울 중심 명소답네요.', '2026-06-12 09:15:00'), -- ➔ 경복궁 평균 별점: 4.3점 완성!

(102, 'ssafy', 5, '평양냉면의 바이블! 고기 육수 향이 아주 진하고 만두도 최고입니다.', '2026-06-13 18:30:00'),
(102, 'testuser', 5, '웨이팅은 길지만 회전율 빠르고 진짜 맛있어요. 인생 평냉집 등극.', '2026-06-14 13:00:00'), -- ➔ 우래옥 평균 별점: 5.0점 완성!

(201, 'ssafy', 5, '새벽에 일출 보러 올라갔는데 계단은 힘들었지만 풍경이 예술입니다.', '2026-06-12 06:00:00'),
(201, 'admin', 5, '제주 정취를 제대로 느낄 수 있는 유네스코 명소 대강추.', '2026-06-13 15:40:00'),
(201, 'testuser', 4, '바람이 많이 불긴 하지만 탁 트인 바다 전망이 가슴을 뻥 뚫어주네요.', '2026-06-14 16:20:00'), -- ➔ 성산일출봉 평균 별점: 4.7점 완성!

(202, 'ssafy', 4, '통갈치구이 비주얼 압도적이고 직원분이 살을 다 발라주셔서 편하게 먹음!', '2026-06-14 12:00:00'),
(202, 'testuser', 3, '맛은 훌륭한데 밑반찬 종류에 비해 가격대가 조금 나가는 편입니다.', '2026-06-15 19:10:00'); -- ➔ 춘심이네 평균 별점: 3.5점 완성!