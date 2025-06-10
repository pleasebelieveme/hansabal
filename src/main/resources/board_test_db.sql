SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS boards;


CREATE TABLE boards (
                        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        category VARCHAR(30) NOT NULL,
                        title VARCHAR(100) NOT NULL,
                        content TEXT NOT NULL,
                        view_count INT NOT NULL DEFAULT 0,
                        dib_count INT NOT NULL DEFAULT 0,
                        created_at DATETIME(6),
                        updated_at DATETIME(6),
                        deleted_at DATETIME(6),
                        FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

INSERT INTO boards (id, user_id, category, title, content, view_count, dib_count, created_at, updated_at, deleted_at)
VALUES (1, 1, 'INFO', 'test title', 'test content', 2, 0, NOW(), NOW(),NULL),
       (2, 1, 'INFO', 'test title', 'test content', 2, 1000, NOW(), NOW(),NULL);

SET FOREIGN_KEY_CHECKS = 1;