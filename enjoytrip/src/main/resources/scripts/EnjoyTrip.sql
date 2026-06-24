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
(101), (102), (103), (201), (202), (203), (301), (302), (303), (401), (402);

INSERT INTO Plans (id, user_id, title, start_date, end_date, status, created_at) VALUES
(1, 2, '?쒖슱 癒밸?由??ы뻾', '2026-07-10', '2026-07-11', 'ONGOING', NOW()),
(2, 2, '?쒖＜ ?몄틝???먮쭅', '2026-05-01', '2026-05-03', 'COMPLETED', NOW()),
(3, 3, '媛뺣쫱 諛붾떎 ?쇳뻾', '2026-08-20', '2026-08-20', 'ONGOING', NOW()),
(101, 4, '?곷뜒 ?寃?留쏆쭛 1諛?2???뚯뒪??肄붿뒪', '2026-07-15', '2026-07-17', 'COMPLETED', NOW()),
(102, 4, '?ъ닔 諛ㅻ컮???먮쭅 ?뚯뒪???ы뻾', '2026-08-01', '2026-08-03', 'COMPLETED', NOW());

INSERT INTO Plans_Details (place_id, plan_id, sequence, day) VALUES
(201, 101, 1, 1),
(202, 101, 2, 1),
(203, 101, 1, 2),
(301, 102, 1, 1),
(302, 102, 2, 1),
(303, 102, 1, 2);

INSERT INTO Attractions (id) VALUES
(501), (502), (503), (504), (505), (506);

INSERT INTO Plans (id, user_id, title, start_date, end_date, status, created_at) VALUES
(201, 2, '寃뚯떆湲 ?곕룞 ?뚯뒪?몄슜 ?꾨즺 ?ы뻾', '2026-06-01', '2026-06-03', 'COMPLETED', NOW());

INSERT INTO Plans_Details (place_id, plan_id, sequence, day) VALUES
(501, 201, 1, 1),
(502, 201, 2, 1),
(503, 201, 3, 1),
(504, 201, 1, 2),
(505, 201, 2, 2),
(506, 201, 1, 3);

INSERT INTO Boards (id, user_id, plan_id, title, content, start_date, end_date, hit, created_at, like_count, region) VALUES
(1, 2, 2, '?쒖＜??3諛?4??轅肄붿뒪 怨듭쑀?⑸땲??, '吏꾩쭨 ?먮쭅 ?ы뻾?댁뿀?듬땲?? 瑗?媛蹂댁꽭??', '2026-05-01', '2026-05-03', 0, NOW(), 0, '?쒖＜'),
(2, 3, NULL, '?쒖슱 寃쎈났沅??쇨컙媛쒖옣 ?꾧린', '?쇨꼍???뺣쭚 ?덉겑?덈떎. 瑗?媛蹂댁꽭??', '2026-08-10', '2026-08-11', 15, NOW(), 1, '?쒖슱'),
(3, 1, NULL, '媛뺣쫱 而ㅽ뵾嫄곕━ 異붿쿇', '?덈ぉ?대??먯꽌 留덉떆??而ㅽ뵾 理쒓퀬?덉슂.', '2026-09-05', '2026-09-05', 102, NOW(), 5, '媛뺤썝');

INSERT INTO Comments (board_id, user_id, content, created_at) VALUES
(1, 2, '?쒓? 李얜뜕 肄붿뒪?덉슂. 怨듭쑀 媛먯궗?⑸땲??', NOW()),
(1, 3, '?ㅼ쓬 ?ы뻾 ??李멸퀬?좉쾶??', NOW()),
(1, 1, '移댄럹 ?대쫫??沅곴툑?⑸땲??', NOW()),
(2, 2, '?쇨꼍 吏꾩쭨 醫뗫뜑?쇨퀬??', NOW());

INSERT INTO Board_Like (board_id, user_id) VALUES
(1, 2),
(2, 2),
(2, 1);

INSERT INTO User_Hashtag (user_id, hashtag_id) VALUES
(2, 1), (2, 4), (2, 7),
(3, 7), (3, 8),
(4, 1), (4, 2), (4, 6);

INSERT INTO board_hashtag (board_id, hashtag_id) VALUES
(1, 1), (1, 2), (1, 6),
(2, 7), (2, 8),
(3, 2), (3, 5);

INSERT INTO Reviews (place_id, user_id, rate, content, created_at) VALUES
(101, 2, 5, '寃쎈났沅??쇨컙媛쒖옣? 臾댁“嫄?媛???⑸땲??', '2026-06-10 14:00:00'),
(101, 3, 4, '??뿉 媛???낆옣?섍퀬 ?곗콉?섍린 醫뗭뒿?덈떎.', '2026-06-11 11:20:00'),
(201, 2, 5, '?덈꼍 ?쇱텧 ?띻꼍???뺣쭚 醫뗭븯?듬땲??', '2026-06-12 06:00:00'),
(202, 3, 4, '吏곸썝遺꾨뱾??移쒖젅?섍퀬 ?묎렐?깆씠 醫뗭븯?듬땲??', '2026-06-14 12:00:00');
