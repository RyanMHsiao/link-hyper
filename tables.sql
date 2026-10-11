
-- Users
CREATE TABLE user (
 userKey decimal(9,0) not null,
 user char(20) not null,
 password char(20) not null,
 joinDate date not null
 PRIMARY KEY (userKey, user)
);

-- For creating posts
CREATE TABLE post (
 postKey decimal(9,0) not null,
 postTime date not null,
 postById decimal (9,0) not null
 PRIMARY KEY (postKey)

);

-- This is for the filters to a post, need to add more filters down the road
CREATE TABLE postFilters (
 postFiltersKey decimal(9,0) not null,
 wikipedia BOOLEAN,
 britanina BOOLEAN,
 PRIMARY KEY (postFiltersKey)
);

-- M to M relationship for users to posts
CREATE TABLE joinedPosts (
 userID decimal(9,0) not null,
 postID decimal(9,0) not null

);

-- External sources, isa relationships
CREATE TABLE externalSource (
 externalID decimal (9,0) not null,
 typeOf char(20) not null,
 PRIMARY KEY(externalID)
);

CREATE TABLE books (
 isbn decimal (9,0) not null,
 title char(60) not null,
 shareDay decimal(9,0) not null,
 shareWeek decimal(9,0) not null,
 shareMonth decimal(9,0) not null,
 PRIMARY KEY(isbn)
);

CREATE TABLE webpage (
 webpageID decimal(9,0) not null,
 url char(100) not null,
 domain char(60) not null,
 shareDay decimal(9,0) not null,
 shareWeek decimal(9,0) not null,
 shareMonth decimal(9,0) not null,
 PRIMARY KEY(webpageID)
);
-- following 3 are isa relationships, just a "main filters" and "other" articles
CREATE TABLE wikipedia (
 wikipediaID decimal(9,0) not null,
 lastEdit date not null,
 PRIMARY KEY(lastEdit)
);

CREATE TABLE britanica (
 britanicaID decimal(9,0) not null,
 datePublished date not null,
 PRIMARY KEY(lastEdit)
);

CREATE TABLE otherDomians (
 domainName char(60) not null,
 datePublished date not null,
 pageID decimal(9,0) not null,
 PRIMARY KEY(pageID)
);

-- Many to Many between users and websites
CREATE TABLE favoritedPages (
 favoriteID decimal(9,0) not null,
 userID decimal(9,0) not null,
 webpageID decimal(9,0) not null,
 PRIMARY KEY(favoriteID)
);

-- Many to Many between users and books
CREATE TABLE favoritedBooks (
 favoriteID decimal(9,0) not null,
 userID decimal(9,0) not null,
 isbn decimal(9,0) not null,
 PRIMARY KEY(favoriteID)
);



