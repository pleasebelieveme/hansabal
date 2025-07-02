SET
FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS user;


CREATE TABLE user
(
    id            BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(50)  NOT NULL,
    password      VARCHAR(100) NOT NULL,
    name          VARCHAR(20)  NOT NULL,
    nickname      VARCHAR(20)  NOT NULL,
    user_role     VARCHAR(20)  NOT NULL,
    user_status   VARCHAR(20)  NOT NULL,
    last_login_at DATETIME(6),
    created_at    DATETIME(6),
    updated_at    DATETIME(6),
    deleted_at    DATETIME(6)
) ENGINE=InnoDB;

INSERT INTO user (id, email, password, name, nickname, user_role, user_status, last_login_at, created_at, updated_at,
                   deleted_at)
VALUES (1, 'test@email.com', '!Aa123456', 'testname', 'testnickname1', 'USER', 'ACTIVE', NOW(), NOW(), NOW(), NULL);

SET
FOREIGN_KEY_CHECKS = 1;
