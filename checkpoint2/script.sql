-- ============================================================================
-- SECTION 1: SIMPLE STATEMENTS (Statements 1 to 10)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Statement 01: Register a new use into the website
-- Explanation: Add a new user into the user table
-- Sample user Alex, password: pass123, join time: nov 1 2026
-- ----------------------------------------------------------------------------
INSERT INTO user (username, password, jointime)
VALUES ('Alex', 'pass123', 20261101);


-- ----------------------------------------------------------------------------
-- Statement 02: User deletes account
-- Explanation: Remove a user from the user table
-- Sample user Alex
-- ----------------------------------------------------------------------------
DELETE FROM user
WHERE username = 'Alex';

-- ----------------------------------------------------------------------------
-- Statement 03: List all vertex a user favorited
-- Explanation: Searches through the favorite database and returns vertexes favorited by a user
-- Sample user Alex
-- ----------------------------------------------------------------------------
SELECT vertexid
FROM favorites
WHERE username = 'Alex';

-- ----------------------------------------------------------------------------
-- Statement 04: Update password
-- Explanation: Updates a user's password
-- Sample user Alex, password: newPass123
-- ----------------------------------------------------------------------------
UPDATE user
SET password = 'newPass123'
WHERE username = 'Alex';