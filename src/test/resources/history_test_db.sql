SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS history;


CREATE TABLE history
(
    id         BIGINT     NOT NULL AUTO_INCREMENT PRIMARY KEY,
    wallet_id     BIGINT     NOT NULL,
    type       varchar(6) NOT NULL,
    trade_id      BIGINT     NOT NULL,
    payment    BIGINT NULL,
    price      BIGINT     NOT NULL,
    remain     BIGINT     NOT NULL,
    uuid       VARCHAR(40) NULL,
    FOREIGN KEY (wallet_id) REFERENCES wallet (id),
    FOREIGN KEY (payment) REFERENCES payment (id),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    deleted_at DATETIME(6)
) ENGINE=InnoDB;

INSERT INTO history (id, wallet_id, type, trade_id, payment, price, remain,uuid ,created_at, updated_at, deleted_at)
VALUES (1, 8, '구매', 4, null,8500, 39000, null,now(), now(), null),
       (2, 9, '구매', 5, null,30500, 63000, null,now(), now(), null);
SET FOREIGN_KEY_CHECKS = 1;
