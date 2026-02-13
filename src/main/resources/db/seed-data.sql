-- ============================================
-- DATABASE SEED FILE
-- Rate Limiter Application
-- ============================================
-- This file contains initial data for tiers and users
-- Run this after the application creates the schema
-- ============================================

-- Clean existing data (optional - only for development)
-- TRUNCATE TABLE notification_log CASCADE;
-- TRUNCATE TABLE rate_limit_usage CASCADE;
-- TRUNCATE TABLE users CASCADE;
-- TRUNCATE TABLE tier CASCADE;

-- ============================================
-- TIER DATA
-- ============================================

INSERT INTO tier (id, name, description, requests_per_minute, requests_per_month, price_per_month, throttle_mode, is_active, created_at, updated_at)
VALUES
    ('tier_free', 'FREE', 'Free tier with basic limits', 10, 1000, 0.00, 'HARD', true, NOW(), NOW()),
    ('tier_basic', 'BASIC', 'Basic tier for small businesses', 100, 10000, 29.00, 'SOFT', true, NOW(), NOW()),
    ('tier_professional', 'PROFESSIONAL', 'Professional tier for growing companies', 1000, 100000, 99.00, 'SOFT', true, NOW(), NOW()),
    ('tier_enterprise', 'ENTERPRISE', 'Enterprise tier with maximum limits', 5000, 1000000, 499.00, 'SOFT', true, NOW(), NOW())
    ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
                            description = EXCLUDED.description,
                            requests_per_minute = EXCLUDED.requests_per_minute,
                            requests_per_month = EXCLUDED.requests_per_month,
                            price_per_month = EXCLUDED.price_per_month,
                            throttle_mode = EXCLUDED.throttle_mode,
                            is_active = EXCLUDED.is_active,
                            updated_at = NOW();

-- ============================================
-- USER DATA
-- ============================================
-- Note: Passwords are BCrypt hashed
-- admin123 -> $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYKVg3oP6X2
-- company123 -> $2a$12$8ZHI2qQPvz8pTJN6VqWXzeDQs2kY3jGvTxYxKCZN3T4Hc5Uqz8Yde
-- user123 -> $2a$12$VcZF3aPj6FN8Qn0oJ7Y2yO4K8LZQ9xV2hY1LN3H6F5K8V9N3X2V1Y

INSERT INTO users (id, username, email, password, role, tier_id, api_key, is_active, created_at, updated_at)
VALUES
    -- Admin User
    (
        'user_admin_001',
        'admin',
        'admin@ratelimiter.com',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYKVg3oP6X2',
        'ADMIN',
        'tier_enterprise',
        'sk_live_admin_6AvEYRw5vefcuSHsbbai83H3IAmQYHuu',
        true,
        NOW(),
        NOW()
    ),

    -- Company User (for testing)
    (
        'user_company_001',
        'company',
        'company@test.com',
        '$2a$12$8ZHI2qQPvz8pTJN6VqWXzeDQs2kY3jGvTxYxKCZN3T4Hc5Uqz8Yde',
        'COMPANY',
        'tier_professional',
        'sk_live_company_7BwFZSx6wfgdvTIstccbj94I4JBnRZIvv',
        true,
        NOW(),
        NOW()
    ),

    -- Demo User (FREE tier)
    (
        'user_demo_001',
        'demo',
        'demo@example.com',
        '$2a$12$VcZF3aPj6FN8Qn0oJ7Y2yO4K8LZQ9xV2hY1LN3H6F5K8V9N3X2V1Y',
        'COMPANY',
        'tier_free',
        'sk_live_demo_8CxGaTy7xghewUJtudddck05J5KCoaKJww',
        true,
        NOW(),
        NOW()
    ),

    -- Test User (BASIC tier)
    (
        'user_test_001',
        'testuser',
        'test@example.com',
        '$2a$12$VcZF3aPj6FN8Qn0oJ7Y2yO4K8LZQ9xV2hY1LN3H6F5K8V9N3X2V1Y',
        'COMPANY',
        'tier_basic',
        'sk_live_test_9DyHbUz8yhifxVKuveeeel16K6LDpbLKxx',
        true,
        NOW(),
        NOW()
    )
    ON CONFLICT (id) DO UPDATE SET
    username = EXCLUDED.username,
                            email = EXCLUDED.email,
                            password = EXCLUDED.password,
                            role = EXCLUDED.role,
                            tier_id = EXCLUDED.tier_id,
                            api_key = EXCLUDED.api_key,
                            is_active = EXCLUDED.is_active,
                            updated_at = NOW();

-- ============================================
-- VERIFICATION QUERIES
-- ============================================
-- Uncomment to verify data was inserted correctly

-- SELECT 'Tiers:' as info;
-- SELECT id, name, requests_per_minute, requests_per_month, price_per_month, is_active FROM tier;

-- SELECT 'Users:' as info;
-- SELECT id, username, email, role, tier_id, is_active FROM users;

-- ============================================
-- SEED DATA SUMMARY
-- ============================================

SELECT 'Seed data loaded successfully!' AS status;
SELECT COUNT(*) || ' tiers created' AS tiers FROM tier;
SELECT COUNT(*) || ' users created' AS users FROM users;

-- ============================================
-- TEST CREDENTIALS
-- ============================================
--
-- ADMIN USER:
--   Email: admin@ratelimiter.com
--   Password: admin123
--   Role: ADMIN
--   Tier: ENTERPRISE
--   API Key: sk_live_admin_6AvEYRw5vefcuSHsbbai83H3IAmQYHuu
--
-- COMPANY USER:
--   Email: company@test.com
--   Password: company123
--   Role: COMPANY
--   Tier: PROFESSIONAL
--   API Key: sk_live_company_7BwFZSx6wfgdvTIstccbj94I4JBnRZIvv
--
-- DEMO USER:
--   Email: demo@example.com
--   Password: user123
--   Role: COMPANY
--   Tier: FREE
--   API Key: sk_live_demo_8CxGaTy7xghewUJtudddck05J5KCoaKJww
--
-- TEST USER:
--   Email: test@example.com
--   Password: user123
--   Role: COMPANY
--   Tier: BASIC
--   API Key: sk_live_test_9DyHbUz8yhifxVKuveeeel16K6LDpbLKxx
--
-- ============================================