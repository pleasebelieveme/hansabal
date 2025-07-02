SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS wallet;


CREATE TABLE wallet
(
    id         BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    users_id    BIGINT NOT NULL,
    cash       BIGINT NOT NULL DEFAULT 0,
    FOREIGN KEY (users_id) REFERENCES user (id),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    deleted_at DATETIME(6)
) ENGINE=InnoDB;

INSERT INTO wallet (id, users_id, cash, created_at, updated_at, deleted_at)
VALUES(1,1,5000, now(), now(), null),
      (2,2,25000, now(), now(), null),
      (3,3,40000, now(), now(), null),--수익자1
      (4,4,85000, now(), now(), null),--수익자2
      (5,5,35000, now(), now(), null),
      (6,6,50000, now(), now(), null),
      (7,7,48000, now(), now(), null),
      (8,8,39000, now(), now(), null),--지불자1
      (9,9,63000, now(), now(), null);--지불자2

SET FOREIGN_KEY_CHECKS = 1;
