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
-- Statement 05: Ryan Hsiao:  10%, Sathya McKinzie:  90%
-- Statement 06: Ryan Hsiao:  10%, Sathya McKinzie:  90%
-- Statement 07: Ryan Hsiao:  10%, Sathya McKinzie:  90%
-- Statement 08: Ryan Hsiao: 100%, Sathya McKinzie:   0%
-- Statement 09: Ryan Hsiao:  60%, Sathya McKinzie:  40%
-- Statement 10: Ryan Hsiao:  70%, Sathya McKinzie:  30%
--
-- SECTION 2: INTERMEDIATE STATEMENTS
-- Statement 11: Ryan Hsiao:  20%, Sathya McKinzie:  80%
-- Statement 12: Ryan Hsiao:   0%, Sathya McKinzie: 100%
-- Statement 13: Ryan Hsiao:   0%, Sathya McKinzie: 100%
-- Statement 14: Ryan Hsiao:  20%, Sathya McKinzie:  80%
-- Statement 15: Ryan Hsiao:  10%, Sathya McKinzie:  90%
-- Statement 16: Ryan Hsiao:  90%, Sathya McKinzie:  10%
-- Statement 17: Ryan Hsiao:  70%, Sathya McKinzie:  30%
-- Statement 18: Ryan Hsiao:  70%, Sathya McKinzie:  30%
-- Statement 19: Ryan Hsiao:  80%, Sathya McKinzie:  20%
-- Statement 20: Ryan Hsiao:  80%, Sathya McKinzie:  20%
--
-- SECTION 3: COMPLEX STATEMENTS
-- Statement 21: Ryan Hsiao:   0%, Sathya McKinzie: 100%
-- Statement 22: Ryan Hsiao:  20%, Sathya McKinzie:  80%
-- Statement 23: Ryan Hsiao:  20%, Sathya McKinzie:  80%
-- Statement 24: Ryan Hsiao:  20%, Sathya McKinzie:  80%
-- Statement 25: Ryan Hsiao:  10%, Sathya McKinzie:  90%
-- Statement 26: Ryan Hsiao:  70%, Sathya McKinzie:  30%
-- Statement 27: Ryan Hsiao: 100%, Sathya McKinzie:   0%
-- Statement 28: Ryan Hsiao:  90%, Sathya McKinzie:  10%
-- Statement 29: Ryan Hsiao:  80%, Sathya McKinzie:  20%
-- Statement 30: Ryan Hsiao: 100%, Sathya McKinzie:   0%
-- ============================================================================

PRAGMA foreign_keys = ON;
.headers on
.mode column


-- ----------------------------------------------------------------------------
-- Additional note: the version of db.sqlite in version control,
-- which is the same as the version submitted to CatCourses,
-- is one randomized result of running Populate.java and does not include
-- the changes applied by running this file.
-- There may be unexpected results if the wrong  version of db.sqlite is used,
-- most notably errors from the INSERT INTO statements from uniqueness failing
-- All the code used is documented in the GitHub repository in this folder:
-- https://github.com/RyanMHsiao/link-hyper/tree/main/checkpoint2
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

-- Statement 05: Retrive all users who joined in November 2026
-- Explanation: Searches through all users join dates, and retrieves those which
-- have a join date in November 2026
-- ----------------------------------------------------------------------------
SELECT username
FROM user
WHERE jointime > 1793491199 AND jointime < 1796083200; 

-- ----------------------------------------------------------------------------
-- Statement 06: List all forum threads
-- Explanation: List all forum threads by creation date
-- ----------------------------------------------------------------------------
SELECT forumthreadname
FROM forumthread
ORDER BY createtime DESC;

-- ----------------------------------------------------------------------------
-- Statement 07: List each user's follower count
-- Explanation: List each user's follower count in descending order
-- ----------------------------------------------------------------------------
SELECT followinguser, count(*)
FROM follows
GROUP BY followinguser
ORDER BY count(*) DESC;

-- ----------------------------------------------------------------------------
-- Statement 08: Show some cases of chained internallinks
-- Explanation: We find all cases in which there are two internal links with
-- the tovertex of one being the fromvertex of the other.
-- This is a chain structure that emerges naturally from a graph.
-- ----------------------------------------------------------------------------
SELECT
	il1.fromvertexid AS 'vertex 1',
	il1.tovertexid AS 'vertex 2',
	il2.tovertexid AS 'vertex 3'
FROM internallink AS il1, internallink AS il2
WHERE il1.tovertexid = il2.fromvertexid
LIMIT 15;

-- ----------------------------------------------------------------------------
-- Statement 09: Insert a new user with the same password as Alex
-- Explanation: This demonstrates that there is no uniqueness constraint
-- on passwords, which would be a cybersecurity issue.
-- ----------------------------------------------------------------------------
INSERT INTO user (username, password, jointime)
VALUES
	('Alex', 'newPass123', 1793569995),
	('Beth', 'newPass123', 1793569995);

-- ----------------------------------------------------------------------------
-- Statement 10: Add some favorited items by Alex and Beth
-- Explanation: INSERT INTO supports inserting multiple values at once.
-- The uniqueness constraint is only on the username and vertexid as a pair,
-- so there are multiple tuples with the same username and multiple with
-- the same vertexid.
-- ----------------------------------------------------------------------------
INSERT INTO favorites (username, vertexid, favoritetime)
VALUES
	('Alex', 30, 1793570000),
	('Alex', 31, 1793570010),
	('Alex', 32, 1793570020),
	('Beth', 31, 1793570030),
	('Beth', 32, 1793570040),
	('Beth', 33, 1793570050);

-- ============================================================================
-- SECTION 2: INTERMEDIATE STATEMENTS (Statements 11 to 20)
-- ============================================================================
-- ----------------------------------------------------------------------------
-- Statement 11: Compare % of articles which someone favorited matches someone else's list
-- Explanation: Goes through a user's list of favorited articles, and see which of those
-- are present in a different user's list of favorited articles
-- User 1: Beth, User 2: Alex
-- ----------------------------------------------------------------------------
WITH countJ AS (
    SELECT COUNT(*)
    FROM favorites
    WHERE username = 'Alex'
)
SELECT (100.0 * count(*) / (SELECT * FROM countJ)) || '%' AS '% favorited by Alex also favorited by Beth'
FROM favorites AS K, favorites AS J
WHERE 
	K.username = 'Beth' AND
	J.username = 'Alex' AND
	K.vertexID = J.vertexID;

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

-- ----------------------------------------------------------------------------
-- Statement 14: Retrieve all messages posted by users who joined in November 2026
-- Explanation: Joins user and message tables, and lists all messages posted
-- by users who have a join date in November 2026
-- ----------------------------------------------------------------------------
SELECT content
FROM user
JOIN message ON poster = username
WHERE jointime > 1793491199 AND jointime < 1796083200
ORDER BY posttime DESC;

-- ----------------------------------------------------------------------------
-- Statement 15: Lists number of likes and favorites by each user
-- Explanation: Joins user with favorite and likes table
-- Counts how many favorites/likes each user has
-- ----------------------------------------------------------------------------
SELECT U.username, count(vertexid), count(messageid)
FROM user U
JOIN favorites F ON U.username = F.username
JOIN likes L ON L.username = U.username
GROUP BY U.username;

-- ----------------------------------------------------------------------------
-- Statement 16: Set a certain set of arcs to be described as deprecated
-- without invalidating any tuples like a deletion might.
-- Explanation: This is a demonstration of the use of UPDATE to change the
-- value of many tuples at once based on some given condition.
-- There is enough information contained in the arc relation to do this.
-- ----------------------------------------------------------------------------
UPDATE arc
SET arcdescription="This arc is deprecated and should no longer be used."
WHERE 
	arccanonid = 0 AND
	createtime < 1794395697 AND
	MOD(arcid, 3) > 0;

-- ----------------------------------------------------------------------------
-- Statement 17: Calculate a relevance score for internallinks by multiplying
-- the number of forumthreads started about that internallink with the number
-- of times the fromvertex of the internallink has been favorited.
-- Explanation: This relevance score takes advantage of a quirk of
-- many-to-many relationships, that being that the count of tuples from
-- the cartesian product is the arithmetic product.
-- ----------------------------------------------------------------------------
SELECT
	forumthread.internallinkid AS 'internallink id',
	COUNT(*) AS 'relevance score'
FROM forumthread, internallink, favorites
WHERE
	forumthread.internallinkid = internallink.internallinkid AND
	fromvertexid = favorites.vertexid
GROUP BY forumthread.internallinkid
ORDER BY COUNT(*) DESC
LIMIT 10;

-- ----------------------------------------------------------------------------
-- Statement 18: Find users who have sent messages in a forumthread made
-- by a user who has blocked them.
-- Explanation: Iterating through all the tuples in block, check for whether
-- there is an applicable case where the forumthread and message are from
-- the two people in the block tuple.
-- ----------------------------------------------------------------------------
SELECT blocker, blocking, message.messageid
FROM forumthread, block, message
WHERE
    forumthread.forumthreadid = message.forumthreadid AND
    originalposter = blocker AND
    message.poster = blocking;

-- ----------------------------------------------------------------------------
-- Statement 19: Find users who have created a forumthread and follow other
-- users who have created a forumthread
-- Explanation: Grouping by the follower, who must have started at least one
-- forumthread, count how many forumthreads are created by followed users.
-- This does not include hanging tuples, so all users found have created a
-- forumthread and follow a user who has created a forumthread.
-- ----------------------------------------------------------------------------
SELECT follower AS 'active social users'
FROM follows, forumthread AS T1, forumthread AS T2
WHERE
	follower = T1.originalposter AND
	followinguser = T2.originalposter
GROUP BY follower
ORDER BY COUNT(*) DESC;

-- ----------------------------------------------------------------------------
-- Statement 20: Get 10 most recent messages in a forumthread created by
-- either user 31 or any user followed by user 31
-- Explanation: Checks the forumthread associated with each message and
-- checks if the creator of the thread is followed by user 31 or user 31.
-- 10 most recent results given by sorting by descending timestamp
-- ----------------------------------------------------------------------------
SELECT DISTINCT poster AS 'message by', content, posttime, originalposter AS 'in thread created by'
FROM message, forumthread, follows
WHERE
    (originalposter = follower OR
        originalposter = followinguser) AND
    message.forumthreadid = forumthread.forumthreadid AND
    follower = 'user 31'
ORDER BY posttime DESC
LIMIT 10;

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

-- ----------------------------------------------------------------------------
-- Statement 23: List users who have at least 5 followers, and have favorited at least one article 
-- Explanation: See users who meet both the criteria of having at least 5 followers, while also favoriting at least one article
-- ----------------------------------------------------------------------------
SELECT followinguser AS 'username'
FROM follows
GROUP BY username
HAVING COUNT(*) > 4
INTERSECT
SELECT DISTINCT username
FROM favorites;

-- ----------------------------------------------------------------------------
-- Statement 24: List users who have at least 10 followers, who are not blocked by at least 3 people
-- Explanation: See users who meet the criteria of having at least 10 followers, while not being blocked by at least 3 people
-- ----------------------------------------------------------------------------
SELECT followinguser AS 'username'
FROM follows
GROUP BY username
HAVING COUNT(*) > 9
EXCEPT
SELECT blocking AS 'username'
FROM block
GROUP BY username
HAVING COUNT(*) < 3;

-- ----------------------------------------------------------------------------
-- Statement 25: List creators of forum threads, and whether they have secure or insure passwords through case variable
-- Explanation: Join username and forumthread, and if a secure password (11+ characters) assign secure, if less characters, assign insecure
-- ----------------------------------------------------------------------------
SELECT DISTINCT U.username, (CASE WHEN LENGTH(U.password) > 10 THEN 'secure' ELSE 'insecure' END) AS 'security of password'
FROM user U
JOIN forumthread F ON U.username = F.originalposter;

-- ----------------------------------------------------------------------------
-- Statement 26: Calculate scores of activity from positive and negative
-- interactions with the service based on the number of favorited vertexes
-- and both the number of followed and blocked user accounts.
-- Explanation: The score is calculated by multiplying the number of
-- favorites multiplied by the number of users followed or blocked. This could
-- be changed slightly by adding the condition that the vertex must be
-- catalogued by a followed or blocked user, which would show the degree of
-- correlation between following, blocking, and favoriting the vertex.
-- ----------------------------------------------------------------------------
WITH countpos AS (SELECT follower AS username, COUNT(*) AS countpos
FROM follows, favorites, vertex
WHERE
    follower = favorites.username AND
    favorites.vertexid = vertex.vertexid
GROUP BY follower
),
countneg AS (SELECT blocker AS username, COUNT(*) AS countneg
FROM block, favorites, vertex
WHERE
    blocker = favorites.username AND
    favorites.vertexid = vertex.vertexid
GROUP BY blocker)
SELECT username, countpos, countneg
FROM user NATURAL JOIN countpos NATURAL JOIN countneg;

-- ----------------------------------------------------------------------------
-- Statement 27: Displays messages with the most repeated end digits for
-- the posttime, which is similar to the imageboard tradition of observing
-- repeated digits in post numbers.
-- Explanation: SQLite string functions are used together to count the number
-- of repeated digits, and that number is then sorted. The way the repeated
-- digits are counted is by seeing how many characters are removed when
-- the value of the rightmost digit is trimmed from the text representation
-- of the timestamp as Unix epoch time.
-- ----------------------------------------------------------------------------
SELECT
	CASE consecutive
		WHEN 1 THEN '...'
		ELSE 'Nice ' || consecutive || 'x repeated end digits!'
	END AS 'consecutive message', posttime, content, poster, messageid
FROM (SELECT * FROM (SELECT
		LENGTH(posttime) - LENGTH(RTRIM(posttime, SUBSTR(posttime, -1, 1))) AS consecutive,
		posttime, content, poster, messageid
	FROM message
	ORDER BY consecutive DESC
	LIMIT 10)
	UNION
	SELECT * FROM (SELECT
		LENGTH(posttime) - LENGTH(RTRIM(posttime, SUBSTR(posttime, -1, 1))) AS consecutive,
		posttime, content, poster, messageid
	FROM message
	LIMIT 5));

-- ----------------------------------------------------------------------------
-- Statement 28: Displays a list of notifications of different types of events
-- Explanation: The different types of data from the three relations are
-- converted to the common format of a string and a timestamp, which means
-- they can be combined through UNION to be displayed together.
-- In an application 'user 31' would be replaced by a scalar query.
-- ----------------------------------------------------------------------------
SELECT *
FROM (SELECT 'Like received from ' || username AS notification, liketime AS stamp
		FROM likes NATURAL JOIN message
		WHERE poster = 'user 31'
	UNION SELECT 'Follow received from ' || follower AS notification, followtime AS stamp
		FROM follows
		WHERE followinguser = 'user 31'
	UNION SELECT 'Favorite received from ' || username AS notification, favoritetime AS stamp
		FROM favorites NATURAL JOIN vertex
		WHERE cataloguer = 'user 31')
ORDER BY stamp DESC;

-- ----------------------------------------------------------------------------
-- Statement 29: Redact messages in a creator's forumthread after the poster
-- has been blocked.
-- Explanation: The approach of updating the message and replacing the content
-- is taken rather than deletion in order to avoid complicating the process
-- to generate messageid values and to avoid invalidating other tuples
-- ----------------------------------------------------------------------------
UPDATE message
SET content='message from blocked user'
WHERE EXISTS (SELECT *
	FROM forumthread, block
	WHERE
		forumthread.forumthreadid = message.forumthreadid AND
		originalposter = blocker AND
		message.poster = blocking AND
		posttime >= blocktime);

-- ----------------------------------------------------------------------------
-- Statement 30: Add a root message to each forumthread
-- Explanation: Adds one new message for each forumthread tuple.
-- The timestamp is set to the same time as the creation of the forumthread,
-- which is not exactly true but it makes the root message the first
-- chronologically within the thread.
-- ----------------------------------------------------------------------------
INSERT INTO message (messageid, forumthreadid, content, poster, posttime)
WITH countmessage AS (SELECT COUNT(*) FROM message)
SELECT
	(SELECT * FROM countmessage) + ROW_NUMBER() OVER (ORDER BY createtime) - 1,
	forumthreadid, 'root message of ' || forumthreadname, originalposter, createtime
FROM forumthread;
