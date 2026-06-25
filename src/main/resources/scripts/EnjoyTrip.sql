DROP DATABASE IF EXISTS enjoytrip;

CREATE DATABASE enjoytrip
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE enjoytrip;

CREATE TABLE Users (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    nickname      VARCHAR(20)  NOT NULL,
    email         VARCHAR(50)  NOT NULL,
    password      VARCHAR(255) NOT NULL,
    profile_path  TEXT         NOT NULL,
    reg_date      DATETIME     NOT NULL,
    role          ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    PRIMARY KEY (id),
    UNIQUE KEY unique_user_email (email)
);

CREATE TABLE Hashtag (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    tag_name  VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE User_Hashtag (
    id          BIGINT NOT NULL AUTO_INCREMENT,
    user_id     BIGINT NOT NULL,
    hashtag_id  BIGINT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Attractions (
    id BIGINT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Plans (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    title       VARCHAR(100) NOT NULL,
    start_date  DATE         NULL,
    end_date    DATE         NULL,
    status      ENUM('ONGOING', 'COMPLETED') NOT NULL DEFAULT 'ONGOING',
    created_at  DATETIME     NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Plans_Details (
    id        BIGINT NOT NULL AUTO_INCREMENT,
    place_id  BIGINT NOT NULL,
    plan_id   BIGINT NOT NULL,
    sequence  INT    NOT NULL,
    day       INT    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Boards (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    plan_id     BIGINT       NULL,
    title       VARCHAR(100) NOT NULL,
    content     TEXT         NOT NULL,
    start_date  DATE         NULL,
    end_date    DATE         NULL,
    hit         INT          NOT NULL DEFAULT 0,
    created_at  DATETIME     NOT NULL,
    like_count  INT          NOT NULL DEFAULT 0,
    region      VARCHAR(20)  NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Comments (
    id          BIGINT   NOT NULL AUTO_INCREMENT,
    board_id    BIGINT   NOT NULL,
    user_id     BIGINT   NOT NULL,
    content     TEXT     NOT NULL,
    created_at  DATETIME NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Board_Like (
    id        BIGINT NOT NULL AUTO_INCREMENT,
    board_id  BIGINT NOT NULL,
    user_id   BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY unique_like (board_id, user_id)
);

CREATE TABLE board_hashtag (
    id          BIGINT NOT NULL AUTO_INCREMENT,
    hashtag_id  BIGINT NOT NULL,
    board_id    BIGINT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE Reviews (
    id          BIGINT   NOT NULL AUTO_INCREMENT,
    place_id    BIGINT   NOT NULL,
    user_id     BIGINT   NOT NULL,
    rate        INT      NOT NULL,
    content     TEXT     NOT NULL,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT CK_Review_Rate CHECK (rate BETWEEN 1 AND 5)
);

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

INSERT INTO Users (id, nickname, email, password, profile_path, reg_date, role) VALUES
(1, '테스트유저', 'ssafy@ssafy.com', '$2a$10$Yt6N.gfTzN0D16u4W5X7guS5Zaus2m51vmn9ZfyI2j9Jg5FMyB68O', '', NOW(), 'ADMIN'),
(2, '닉네임', 'abc@ssafy.com', '$2a$10$1YVp7TkMSbAUmMEwPq5Khu43HbN7sJ0Lefwt/yPNFmjPRs6S55pP2', '', '2026-06-25 02:26:56', 'USER');
 
INSERT INTO Hashtag (id, tag_name) VALUES
(1, '힐링'),
(2, '맛집탐방'),
(3, '인생샷'),
(4, '액티비티'),
(5, '가성비'),
(6, '가족여행'),
(7, '바다'),
(8, '혼여행'),
(9, '자연'),
(10, '도시여행'),
(11, '역사문화'),
(12, '카페투어'),
(13, '야경'),
(14, '드라이브'),
(15, '캠핑'),
(16, '트레킹'),
(17, '온천'),
(18, '쇼핑'),
(19, '데이트'),
(20, '반려동물');

INSERT INTO Attractions (id) VALUES
(7987002), (8199114), (8325573), (11018167), (11164998), (25042489), (25754890);

INSERT INTO Plans (id, user_id, title, start_date, end_date, status, created_at) VALUES
(1, 1, '서울 먹부림 여행', '2026-07-10', '2026-07-11', 'ONGOING', NOW()),
(2, 1, '제주 호캉스 힐링', '2026-05-01', '2026-05-03', 'COMPLETED', NOW()),
(3, 1, '강릉 바다 혼행', '2026-08-20', '2026-08-20', 'ONGOING', NOW()),
(4, 1, '영덕 대게 맛집 1박 2일 테스트 코스', '2025-07-15', '2025-07-17', 'COMPLETED', NOW());

INSERT INTO Plans_Details (place_id, plan_id, sequence, day) VALUES
(7987002, 2, 1, 1),
(8199114, 2, 2, 1),
(8325573, 2, 3, 1),
(11018167, 4, 1, 2),
(11164998, 4, 2, 2),
(25042489, 4, 3, 2);

INSERT INTO Boards (id, user_id, plan_id, title, content, start_date, end_date, hit, created_at, like_count, region) VALUES
(1, 1, 2, '제주도 3박 4일 꿀코스 공유합니다', '진짜 힐링 여행이었습니다. 꼭 가보세요.', '2026-05-01', '2026-05-03', 0, NOW(), 0, '제주'),
(2, 1, 1, '서울 경복궁 야간개장 후기', '야경이 정말 예쁩니다. 꼭 가보세요.', '2026-07-10', '2026-07-11', 15, NOW(), 1, '서울'),
(3, 1, NULL, '강릉 커피거리 추천', '안목해변에서 마시는 커피 최고예요.', '2026-09-05', '2026-09-05', 102, NOW(), 5, '강원');

INSERT INTO Comments (board_id, user_id, content, created_at) VALUES
(1, 1, '제가 찾던 코스예요. 공유 감사합니다.', NOW()),
(1, 1, '다음 여행 때 참고할게요.', NOW()),
(1, 1, '카페 이름도 궁금합니다.', NOW()),
(2, 1, '야경 진짜 좋더라고요.', NOW());

INSERT INTO Board_Like (board_id, user_id) VALUES
(1, 1),
(2, 1);

INSERT INTO User_Hashtag (user_id, hashtag_id) VALUES
(1, 1), (1, 4), (1, 7);

INSERT INTO board_hashtag (board_id, hashtag_id) VALUES
(1, 1), (1, 2), (1, 6);

INSERT INTO Reviews (place_id, user_id, rate, content, created_at) VALUES
(7987002, 1, 5, '경복궁 야간개장은 무조건 가야 합니다.', '2026-06-10 14:00:00'),
(7987002, 1, 4, '낮에 가도 웅장하고 산책하기 좋습니다.', '2026-06-11 11:20:00'),
(8199114, 1, 5, '새벽 일출 풍경이 정말 좋았습니다.', '2026-06-12 06:00:00'),
(8199114, 1, 4, '직원분들이 친절하고 접근성이 좋았습니다.', '2026-06-14 12:00:00');
