SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS requests;


CREATE TABLE requests (
                          id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          status VARCHAR(30) NOT NULL ,
                          trade_id BIGINT NOT NULL ,
                          user_id BIGINT NOT NULL ,
                          FOREIGN KEY (trade_id) REFERENCES trade(id),
                          FOREIGN KEY (user_id) REFERENCES users(id),
                          created_at DATETIME(6),
                          updated_at DATETIME(6),
                          deleted_at DATETIME(6)
) ENGINE=InnoDB;

INSERT INTO requests (id, status, trade_id, user_id, created_at, updated_at, deleted_at)
VALUES(1,'AVAILABLE',1,5,now(),now(),null),
      (2,'PENDING',2,6,now(),now(),null),
      (3,'PAID',3,7,now(),now(),null),
      (4,'SHIPPING',4,8,now(),now(),null),
      (5,'DONE',5,9,now(),now(),null),
      (6,'AVAILABLE',6,9,now(),now(),null),
      (7,'AVAILABLE',5,5,now(),now(),null),
      (8,'AVAILABLE',6,9,now(),now(),null);
SET FOREIGN_KEY_CHECKS = 1;
