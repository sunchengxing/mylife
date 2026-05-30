-- xiangha recipe database schema
-- Import data: gunzip < xiangha_data.sql.gz | mysql -u root -p xiangha

CREATE DATABASE IF NOT EXISTS xiangha CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE xiangha;

CREATE TABLE IF NOT EXISTS categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    slug VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50),
    total_pages INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS recipes (
    id BIGINT PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    author VARCHAR(200),
    author_url VARCHAR(500),
    cover_img VARCHAR(500),
    views INT DEFAULT 0,
    favorites INT DEFAULT 0,
    category VARCHAR(100),
    tip TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_views (views)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS ingredients (
    id INT PRIMARY KEY AUTO_INCREMENT,
    recipe_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    amount VARCHAR(200),
    type ENUM('main','sub') DEFAULT 'main',
    INDEX idx_recipe (recipe_id),
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS steps (
    id INT PRIMARY KEY AUTO_INCREMENT,
    recipe_id BIGINT NOT NULL,
    step_no INT NOT NULL,
    description TEXT NOT NULL,
    image_url VARCHAR(500),
    INDEX idx_recipe (recipe_id),
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
