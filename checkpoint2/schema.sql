-- Updated schema for checkpoint 2
-- Should NOT be used as submission for CatCourses assignment,
-- since additional information is needed for that

-- Social media type entity sets
CREATE TABLE IF NOT EXISTS user (
	username char(20) not null,
	password char(20) not null,
	jointime datetime not null,
	PRIMARY KEY (username)
);

CREATE TABLE IF NOT EXISTS forumthread (
	forumthreadid decimal(0, 9) not null,
	forumthreadname char(40) not null,
	originalposter char(20) not null, -- username
	createtime datetime not null,
	-- Four attributes for topic, nullable
	vertexcanonid decimal(0, 9),
	vertexid char(255),
	arccanonid decimal(0, 9),
	arcid decimal(0, 9),
	PRIMARY KEY (forumthreadid)
);

CREATE TABLE IF NOT EXISTS message (
	messageid decimal(0, 9) not null,
	forumthreadid decimal(0, 9) not null,
	content char(255) not null,
	poster char(20) not null, -- username
	posttime datetime not null,
	PRIMARY KEY (messageid)
);

-- m:n social media type relations
CREATE TABLE IF NOT EXISTS follows (
	follower char(20) not null,
	following char(20) not null,
	followtime datetime not null
);

CREATE TABLE IF NOT EXISTS block (
	follower char(20) not null,
	following char(20) not null,
	followtime datetime not null
);

-- m:n article to user type relations
CREATE TABLE IF NOT EXISTS likes (
	username char(20) not null,
	messageid decimal(0, 9) not null,
	liketime datetime not null
);

CREATE TABLE IF NOT EXISTS favorites (
	username char(20) not null,
	vertexid decimal(0, 9) not null,
	favoritetime datetime not null
);

-- Catalogue type entity sets
CREATE TABLE IF NOT EXISTS vertexcanon (
	vertexcanonid decimal(0, 9) not null,
	vertexcanonname char(255) not null,
	creator char(20) not null,
	createtime timestamp not null,
	PRIMARY KEY (vertexcanonid)
);

CREATE TABLE IF NOT EXISTS vertex (
	vertexid decimal(0, 9) not null,
	vertexcanonid decimal(0, 9) not null,
	vertexname char(40) not null,
	-- "cataloguer" instead of "creator" to avoid confusion with creator of an external record
	cataloguer char(20) not null,
	cataloguetime datetime not null,
	metadata char(255),
	content char(255),
	PRIMARY KEY (vertexid)
);

CREATE TABLE IF NOT EXISTS arccanon (
	arccanonid decimal(0, 9) not null,
	arccanonname char(255) not null,
	creator char(20) not null,
	createtime timestamp not null,
	PRIMARY KEY (arccanonid)
);

CREATE TABLE IF NOT EXISTS arc (
	arcid decimal(0, 9) not null,
	arccanonid decimal(0, 9) not null,
	arcname char(40) not null,
	arcdescription char(255) not null,
	creator char(20) not null,
	createtime timestamp not null,
	PRIMARY KEY (arcid)
);

-- m:n catalogue type relations
CREATE TABLE IF NOT EXISTS internallink (
	arccanonid decimal(0, 9) not null,
	arcid decimal(0, 9) not null,
	fromvertex decimal(0, 9) not null,
	tovertex decimal(0, 9) not null
);

-- These are leaderboards for most viewed article
CREATE TABLE IF NOT EXISTS mostViewedDay (
	vertexid decimal(0, 9) not null,
	viewCount decimal(0, 9) not null
);

CREATE TABLE IF NOT EXISTS mostViewedWeek (
	vertexid decimal(0, 9) not null,
	viewCount decimal(0, 9) not null
);

CREATE TABLE IF NOT EXISTS mostViewedMonth (
	vertexid decimal(0, 9) not null,
	viewCount decimal(0, 9) not null
);

