SET
FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS product;


CREATE TABLE product
(
    id             BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(20) NOT NULL,
    quantity       INT         NOT NULL,
    product_status VARCHAR(30) NOT NULL,
    created_at     DATETIME(6),
    updated_at     DATETIME(6),
    deleted_at     DATETIME(6)
) ENGINE=InnoDB;

INSERT INTO product (id, name, quantity, product_status, created_at, updated_at, deleted_at)
VALUES (1, 'test product', 5, 'FOR_SALE', NOW(), NULL, NULL),
       (2, 'test product', 5, 'FOR_SALE', NOW(), NULL, NULL);

SET
FOREIGN_KEY_CHECKS = 0;