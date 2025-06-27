SET
FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS boards;


CREATE TABLE boards
(
    id         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    category   VARCHAR(30)  NOT NULL,
    title      VARCHAR(100) NOT NULL,
    content    TEXT         NOT NULL,
    view_count INT          NOT NULL DEFAULT 0,
    dib_count  INT          NOT NULL DEFAULT 0,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    deleted_at DATETIME(6),
    FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;

INSERT INTO boards (id, user_id, category, title, content, view_count, dib_count, created_at, updated_at, deleted_at)
VALUES (1, 1, 'DAILY', '제목', '내용', 0, 0, NOW(), NOW(), NULL),
       (2, 1, 'DAILY', '제목', '내용', 0, 0, NOW(), NOW(), NULL),
       (3, 1, 'DAILY', '제목', '내용', 0, 0, NOW(), NOW(), NULL),
       (4, 1, 'DAILY', '제목', '내용', 0, 0, NOW(), NOW(), NULL),
       (5, 1, 'DAILY', '제목', '내용', 0, 0, NOW(), NOW(), NULL),
       (6, 1, 'DAILY', '제목', '내용', 0, 0, NOW(), NOW(), NULL),
       (7, 1, 'DAILY', '제목', '내용', 0, 0, NOW(), NOW(), NULL),
       (8, 1, 'DAILY', '제목', '내용', 0, 0, NOW(), NOW(), NULL),
       (9, 1, 'DAILY', '제목', '내용', 0, 0, NOW(), NOW(), NULL),
       (10, 1, 'DAILY', '제목', '내용', 0, 0, NOW(), NOW(), NULL),
       (11, 1, 'DAILY', '제목', '내용', 0, 0, NOW(), NOW(), NULL);

SET
FOREIGN_KEY_CHECKS = 1;