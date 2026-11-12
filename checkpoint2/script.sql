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


-- ============================================================================
-- SECTION 2: INTERMEDIATE STATEMENTS (Statements 11 to 20)
-- ============================================================================
-- ----------------------------------------------------------------------------
-- Statement 11: Compare % of articles which someone favorited matches someone else's list
-- Explanation: Goes through a user's list of favorited articles, and see which of those
-- are present in a different user's list of favorited articles
-- User 1: Beth, User 2: Alex
-- ----------------------------------------------------------------------------
SELECT  count(K) / count(J)
FROM (
    SELECT *
    FROM favorites
    WHERE username = 'Beth'
) K, (
    SELECT *
    FROM favorites
    WHERE username = 'Alex'
) J
WHERE K.vertexID == J.vertexID;


-- ----------------------------------------------------------------------------
-- Statement 12: View most favorited vertex among users who follow them
-- Explanation: Goes through a user's follower list, and retrives all vertex
-- then groups and sorts them by how frequent they appear
-- User: Alex
-- ----------------------------------------------------------------------------
SELECT V.vertexname, count(*)
FROM vertex V
JOIN favorites F ON V.vertexid = F.vertexid
JOIN user U ON U.username = F.username
JOIN follows FO ON FO.followinguser = U.username
WHERE U.username == 'Alex'
GROUP BY V.vertexname;

-- ----------------------------------------------------------------------------
-- Statement 13: Count how many distinct users are in each forum thread
-- Explanation: Joins message table with forum table, and count how many distinct users
-- posted in each forum, descending in count
-- ----------------------------------------------------------------------------
SELECT F.forumthreadname, count(DISTINCT poster)
FROM forumthread F
JOIN message M ON F.forumthreadid = M.forumthreadid
GROUP BY F.forumthreadid
ORDER BY count(DISTINCT poster) DESC;

-- ============================================================================
-- SECTION 3: COMPLEX STATEMENTS (Statements 21 to 30)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Statement 21: List favorited articles by User A EXCEPT those which are not favorited by User B
-- Explanation: Compares User A's and B's favorited article list, and lists out
-- articles favorited by User A which are not favorited by User B
-- User A: Alex, User B: Beth
-- ----------------------------------------------------------------------------
SELECT vertexID
FROM favorites
WHERE username = 'Alex'
EXCEPT
SELECT vertexID
FROM favorites
WHERE username = 'Beth';

-- ----------------------------------------------------------------------------
-- Statement 22: List the top 15 most favorited articles EXCEPT those which are favorited by User A
-- Explanation: Create a list of the top 15 most favorited articles by anyone, which are not in the
-- list of those which are favorited by User A
-- User A: Alex
-- ----------------------------------------------------------------------------
SELECT *
FROM (
SELECT vertexid, count(*)
FROM favorites
GROUP BY vertexid
EXCEPT
SELECT vertexid
FROM favorites
WHERE username = 'Alex'
) K limit 15;

