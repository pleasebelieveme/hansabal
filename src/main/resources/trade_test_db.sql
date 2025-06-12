SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS trade;


CREATE TABLE trade (
                         id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                         title VARCHAR(30) NOT NULL ,
                         contents TEXT ,
                         trader BIGINT NOT NULL ,
                         price BIGINT NOT NULL ,
                         isOccupied tinyint NOT NULL,
                         FOREIGN KEY (trader) REFERENCES users(id)
) ENGINE=InnoDB;

INSERT INTO trade (id, title, contents, trader, price,isOccupied)
VALUES(1,'test1','testcontents',1,25000,0),
      (2,'test2','testcontents',1,15000,1),
      (3,'test3','testcontents',2,33000,1),
      (4,'test4','testcontents',3,8500,1),
      (5,'generic1','testcontents',4,30500,1),
      (6,'generic2','testcontents',4,30500,0),
      (7,'generic3','testcontents',4,30500,0);

SET FOREIGN_KEY_CHECKS = 1;
