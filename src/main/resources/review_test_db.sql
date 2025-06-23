SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS reviews;


CREATE TABLE reviews (
                       id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                       content TEXT NOT NULL ,
                       rating BIGINT NOT NULL ,
                       user_id BIGINT NOT NULL ,
                       product_id BIGINT NOT NULL ,
                       FOREIGN KEY (user_id) REFERENCES users (id),
                       FOREIGN KEY (product_id) REFERENCES product (id),
                       created_at DATETIME(6),
                       updated_at DATETIME(6),
                       deleted_at DATETIME(6)
) ENGINE=InnoDB;

INSERT INTO reviews (id, content, rating, user_id, product_id, created_at, updated_at, deleted_at)
VALUES (1,'test review',5,1,1,NOW(),NOW(),NULL);

    SET FOREIGN_KEY_CHECKS = 0;