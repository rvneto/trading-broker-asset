CREATE TABLE assets
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticker        VARCHAR(20) NOT NULL UNIQUE,
    name          VARCHAR(100),
    current_price DECIMAL(19, 4),
    last_update   TIMESTAMP,
    status        ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_at    TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_assets_ticker ON assets (ticker);