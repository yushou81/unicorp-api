-- 作品项目表
CREATE TABLE portfolio_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    project_url VARCHAR(255),
    cover_image_url VARCHAR(255),
    category VARCHAR(50),
    tags VARCHAR(255),
    team_members VARCHAR(255),
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    view_count INT NOT NULL DEFAULT 0,
    like_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_portfolio_user_id ON portfolio_items(user_id);
CREATE INDEX idx_portfolio_category ON portfolio_items(category);
CREATE INDEX idx_portfolio_is_public ON portfolio_items(is_public);
CREATE INDEX idx_portfolio_created_at ON portfolio_items(created_at);

-- 作品资源表
CREATE TABLE portfolio_resources (
    id INT PRIMARY KEY AUTO_INCREMENT,
    portfolio_item_id INT NOT NULL,
    resource_type VARCHAR(20) NOT NULL,
    resource_url VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    display_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (portfolio_item_id) REFERENCES portfolio_items(id)
);

-- 创建索引
CREATE INDEX idx_portfolio_resource_item_id ON portfolio_resources(portfolio_item_id);
CREATE INDEX idx_portfolio_resource_type ON portfolio_resources(resource_type);

-- 竞赛获奖表
CREATE TABLE competition_awards (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    competition_name VARCHAR(100) NOT NULL,
    award_level VARCHAR(50) NOT NULL,
    award_date DATE NOT NULL,
    organizer VARCHAR(100),
    description TEXT,
    certificate_url VARCHAR(255),
    team_members VARCHAR(255),
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    verifier_id INT,
    verifier_name VARCHAR(50),
    verify_date DATETIME,
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (verifier_id) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_award_user_id ON competition_awards(user_id);
CREATE INDEX idx_award_is_verified ON competition_awards(is_verified);
CREATE INDEX idx_award_is_public ON competition_awards(is_public);
CREATE INDEX idx_award_date ON competition_awards(award_date);

-- 科研成果表
CREATE TABLE research_achievements (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    authors VARCHAR(255) NOT NULL,
    publication_date DATE,
    publisher VARCHAR(100),
    description TEXT,
    file_url VARCHAR(255),
    cover_image_url VARCHAR(255),
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    verifier_id INT,
    verifier_name VARCHAR(50),
    verify_date DATETIME,
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (verifier_id) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_research_user_id ON research_achievements(user_id);
CREATE INDEX idx_research_type ON research_achievements(type);
CREATE INDEX idx_research_is_verified ON research_achievements(is_verified);
CREATE INDEX idx_research_is_public ON research_achievements(is_public);
CREATE INDEX idx_research_publication_date ON research_achievements(publication_date);

-- 成果访问记录表
CREATE TABLE achievement_views (
    id INT PRIMARY KEY AUTO_INCREMENT,
    achievement_type VARCHAR(20) NOT NULL,
    achievement_id INT NOT NULL,
    viewer_id INT,
    viewer_ip VARCHAR(50) NOT NULL,
    view_time DATETIME NOT NULL,
    FOREIGN KEY (viewer_id) REFERENCES users(id)
);

-- 创建索引
CREATE INDEX idx_achievement_view_type_id ON achievement_views(achievement_type, achievement_id);
CREATE INDEX idx_achievement_viewer_id ON achievement_views(viewer_id);
CREATE INDEX idx_achievement_view_time ON achievement_views(view_time); 