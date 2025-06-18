SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS trade;


CREATE TABLE trade (
                       id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(30) NOT NULL ,
                       contents TEXT ,
                       user_id BIGINT NOT NULL ,
                       price BIGINT NOT NULL ,
                       is_occupied tinyint NOT NULL,
                       created_at DATETIME(6),
                       updated_at DATETIME(6),
                       deleted_at DATETIME(6),
                       FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

INSERT INTO trade (id, title, contents, user_id, price,is_occupied, created_at, updated_at, deleted_at)
VALUES(1,'test1','testcontents',1,25000,0,now(),now(),null),
      (2,'test2','testcontents',1,15000,1,now(),now(),null),
      (3,'test3','testcontents',2,33000,1,now(),now(),null),
      (4,'test4','testcontents',3,8500,1,now(),now(),null),
      (5,'generic1','testcontents',4,30500,1,now(),now(),null),
      (6,'generic2','testcontents',4,30500,0,now(),now(),null),
      (7,'generic3','testcontents',4,30500,0,now(),now(),null),
      (8,'generic4','testcontents',4,30500,0,now(),now(),null);

SET FOREIGN_KEY_CHECKS = 1;
