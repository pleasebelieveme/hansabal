SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS requests;


CREATE TABLE requests (
                       id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                       status VARCHAR(30) NOT NULL ,
                       trade BIGINT NOT NULL ,
                       requester BIGINT NOT NULL ,
                       FOREIGN KEY (trade) REFERENCES trade(id),
                       FOREIGN KEY (requester) REFERENCES users(id)
) ENGINE=InnoDB;

INSERT INTO requests (id, status, trade, requester)
VALUES(1,'AVAILABLE',1,5),
      (2,'PENDING',2,6),
      (3,'PAID',3,7),
      (4,'SHIPPING',4,8),
      (5,'DONE',5,9),
      (6,'AVAILABLE',6,9),
      (7,'AVAILABLE',6,5);
SET FOREIGN_KEY_CHECKS = 1;
