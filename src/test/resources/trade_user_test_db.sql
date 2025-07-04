SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS users;


CREATE TABLE users (
                       id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(50) NOT NULL ,
                       password VARCHAR(100) NOT NULL ,
                       name VARCHAR(20) NOT NULL ,
                       nickname VARCHAR(20) NOT NULL ,
                       user_role VARCHAR(20) NOT NULL,
                       user_status VARCHAR(20) NOT NULL ,
                       last_login_at DATETIME(6),
                       created_at DATETIME(6),
                       updated_at DATETIME(6),
                       deleted_at DATETIME(6)
) ENGINE=InnoDB;

INSERT INTO users (id, email, password, name, nickname, user_role, user_status, last_login_at,created_at, updated_at, deleted_at)
VALUES (1,'test@email.com','!Aa123456','testname','testnickname1','USER', 'ACTIVE', Now(),NOW(),NOW(),NULL),
       (2,'test2@email.com','!Aa123456','testname2','testnickname2','USER', 'ACTIVE', Now(),NOW(),NOW(),NULL),
       (3,'test3@email.com','!Aa123456','testname3','testnickname3','USER', 'ACTIVE', Now(),NOW(),NOW(),NULL),
       (4,'test4@email.com','!Aa123456','testname4','testnickname4','USER', 'ACTIVE', Now(),NOW(),NOW(),NULL),
       (5,'test5@email.com','!Aa123456','testname5','testnickname5','USER', 'ACTIVE', Now(),NOW(),NOW(),NULL),
       (6,'test6@email.com','!Aa123456','testname6','testnickname6','USER', 'ACTIVE', Now(),NOW(),NOW(),NULL),
       (7,'test7@email.com','!Aa123456','testname7','testnickname7','USER', 'ACTIVE', Now(),NOW(),NOW(),NULL),
       (8,'test8@email.com','!Aa123456','testname8','testnickname8','USER', 'ACTIVE', Now(),NOW(),NOW(),NULL),
       (9,'test9@email.com','!Aa123456','testname9','testnickname9','USER', 'ACTIVE', Now(),NOW(),NOW(),NULL),
       (10,'test10@email.com','!Aa123456','testname10','testnickname10','USER', 'ACTIVE', Now(),NOW(),NOW(),NULL);

SET FOREIGN_KEY_CHECKS = 1;
