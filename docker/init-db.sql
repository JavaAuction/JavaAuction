-- User Service Database
CREATE DATABASE user_db;

-- Product Service Database
CREATE DATABASE product_db;

-- Auction Service Database
CREATE DATABASE auction_db;

-- Payment Service Database
CREATE DATABASE payment_db;

-- Chat Service Database
CREATE DATABASE chat_db;

-- Alert Service Database
CREATE DATABASE alert_db;

-- 권한 부여
-- Docker-compose: POSTGRES_USER = javaauction
GRANT ALL PRIVILEGES ON DATABASE user_db TO javaauction;
GRANT ALL PRIVILEGES ON DATABASE product_db TO javaauction;
GRANT ALL PRIVILEGES ON DATABASE auction_db TO javaauction;
GRANT ALL PRIVILEGES ON DATABASE payment_db TO javaauction;
GRANT ALL PRIVILEGES ON DATABASE chat_db TO javaauction;
GRANT ALL PRIVILEGES ON DATABASE alert_db TO javaauction;
