SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS requests;


CREATE TABLE requests (
                       id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                       status VARCHAR(30) NOT NULL ,
                       trade BIGINT NOT NULL ,
                       requester BIGINT NOT NULL ,
                       FOREIGN KEY (trade) REFERENCES trade(id),
                       FOREIGN KEY (requester) REFERENCES users(id),
                       created_at DATETIME(6),
                       updated_at DATETIME(6),
                       deleted_at DATETIME(6)
) ENGINE=InnoDB;

INSERT INTO requests (id, status, trade, requester,created_at, updated_at, deleted_at)
VALUES(1,'AVAILABLE',1,5,now(),now(),null),
      (2,'PENDING',2,6,now(),now(),null),
      (3,'PAID',3,7,now(),now(),null),
      (4,'SHIPPING',4,8,now(),now(),null),
      (5,'DONE',5,9,now(),now(),null),
      (6,'AVAILABLE',6,9,now(),now(),null),
      (7,'AVAILABLE',6,5,now(),now(),null);
SET FOREIGN_KEY_CHECKS = 1;
