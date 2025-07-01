SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS history;


CREATE TABLE history
(
    id         BIGINT     NOT NULL AUTO_INCREMENT PRIMARY KEY,
    wallet     BIGINT     NOT NULL,
    type       varchar(6) NOT NULL,
    trade      BIGINT     NOT NULL,
    payment    BIGINT NULL,
    price      BIGINT     NOT NULL,
    remain     BIGINT     NOT NULL,
    FOREIGN KEY (wallet) REFERENCES wallet (id),
    FOREIGN KEY (payment) REFERENCES Payment (id),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    deleted_at DATETIME(6)
) ENGINE=InnoDB;

INSERT INTO history (id, wallet, type, trade, payment, price, remain, created_at, updated_at, deleted_at)
VALUES (1, 8, '구매', 4, null,8500, 39000, now(), now(), null),
       (2, 9, '구매', 5, null,30500, 63000, now(), now(), null);
SET FOREIGN_KEY_CHECKS = 1;
