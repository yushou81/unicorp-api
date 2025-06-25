DROP TABLE IF EXISTS user;
CREATE TABLE user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  nickname VARCHAR(50),
  avatar_url VARCHAR(255),
  phone VARCHAR(20),
  role VARCHAR(20) DEFAULT 'USER',
  status INT DEFAULT 0,
  last_login_time DATETIME,
  is_deleted INT DEFAULT 0,
  create_time DATETIME,
  update_time DATETIME
);

DROP TABLE IF EXISTS merchant;
CREATE TABLE merchant (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  address VARCHAR(255),
  phone VARCHAR(20),
  description TEXT,
  logo_url VARCHAR(255),
  business_hours VARCHAR(50),
  average_rating DECIMAL(2,1) DEFAULT 0.0,
  latitude DECIMAL(10,6),
  longitude DECIMAL(10,6),
  status VARCHAR(20) DEFAULT 'PENDING_REVIEW',
  is_deleted INT DEFAULT 0,
  create_time DATETIME,
  update_time DATETIME
);

DROP TABLE IF EXISTS product;
CREATE TABLE product (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  merchant_id BIGINT NOT NULL,
  category_id BIGINT,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  price DECIMAL(10,2) NOT NULL,
  image_url VARCHAR(255),
  stock INT DEFAULT 0,
  sales_count INT DEFAULT 0,
  status VARCHAR(20) DEFAULT 'ON_SALE',
  is_deleted INT DEFAULT 0,
  create_time DATETIME,
  update_time DATETIME
); 