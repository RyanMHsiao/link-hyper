-- ============================================================================
-- CSE 111 Fall 2026: Project Checkpoint 2 Script
-- Project Title: LinkHyper: Information Cataloguing as Social Media
-- Team Members: Ryan Hsiao, Sathya McKinzie
-- File: Checkpoint2-script.sql
-- ============================================================================

-- ============================================================================
-- SCHEMA MODIFICATIONS SINCE CHECKPOINT 1
-- ============================================================================
-- This is a long list of many minor changes.
-- The main concepts are unchanged from the schema described in Checkpoint 1.
-- 1. More attributes for metadata are added. This is most noticeable in the
--    timestamp attribute added to each relation. There are also some more
--    attributes containing textual metadata.
-- 2. externalrecord and externaldatabase are renamed to vertex and
--    vertex canon. linktype is now an entity called arc in a n:1 relationship
--    with arccanon rather than just a string.
-- 3. "isa" relationships are removed and replaced by completely separate
--    relations for each subclass. Thus rather than message and externalrecord
--    being subclasses of artifact, there are completely separate message and
--    vertex relations.
-- 4. There is no longer a designated root message in a forumthread
-- 5. Users are now able to block users, like messages, and favorite vertexes,
--    which is equivalent to liking an artifact in the Checkpoint 1 schema.
-- ============================================================================

-- ============================================================================
-- MEMBER CONTRIBUTIONS SUMMARY (GROUPED AT THE TOP)
-- ============================================================================
-- SECTION 1: SIMPLE STATEMENTS
-- Statement 01: Ryan Hsiao:  10%, Sathya McKinzie:  90%
-- Statement 02: Ryan Hsiao:   0%, Sathya McKinzie: 100%
-- Statement 03: Ryan Hsiao:   0%, Sathya McKinzie: 100%
-- Statement 04: Ryan Hsiao:   0%, Sathya McKinzie: 100%
-- ...
--
-- SECTION 2: INTERMEDIATE STATEMENTS
-- Statement 11: Ryan Hsiao:  20%, Sathya McKinzie:  80%
-- Statement 12: Ryan Hsiao:   0%, Sathya McKinzie: 100%
-- Statement 13: Ryan Hsiao:   0%, Sathya McKinzie: 100%
-- ...
--
-- SECTION 3: COMPLEX STATEMENTS
-- Statement 21: Ryan Hsiao:   0%, Sathya McKinzie: 100%
-- Statement 22: Ryan Hsiao:  20%, Sathya McKinzie:  80%
-- ...
-- ============================================================================

PRAGMA foreign_keys = ON;
.headers on
.mode column


-- ----------------------------------------------------------------------------
-- Additional note: the version of db.sqlite in version control,
-- which is the same as the version submitted to CatCourses,
-- is one randomized result of running Populate.java and does not include
-- the changes applied by running this file.
-- ----------------------------------------------------------------------------

-- ============================================================================
-- SECTION 1: SIMPLE STATEMENTS (Statements 1 to 10)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Statement 01: Register a new use into the website
-- Explanation: Add a new user into the user table
-- Sample user Alex, password: pass123, join time: nov 1 2026 (Unix timestamp)
-- ----------------------------------------------------------------------------
INSERT INTO user (username, password, jointime)
VALUES ('Alex', 'pass123', 1793563200);


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
WITH J AS (
    SELECT vertexID
    FROM favorites
    WHERE username = 'Alex'
)
SELECT count(*) / (SELECT COUNT(*) FROM J) AS 'Proportion of articles favorited by Alex that are also favorited by Beth'
FROM (
    SELECT *
    FROM favorites
    WHERE username = 'Beth'
) K
WHERE K.vertexID IN J;


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
SELECT vertexname AS '15 most favorited articles EXCEPT those favorited by Alex'
FROM (SELECT vertexid
	FROM favorites
	WHERE username <> 'Alex'
	GROUP BY vertexid
	ORDER BY COUNT(*) DESC
	LIMIT 15)
    NATURAL JOIN vertex;
