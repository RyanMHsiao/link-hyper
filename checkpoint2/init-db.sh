#!/bin/sh
# ^ (technically not always formally correct) shebang for POSIX shell
# This is a pretty simple script, I'm just paranoid about bash weirdness

JDBC=../sqlite-jdbc-3.47.0.0.jar
DB=db.sqlite

if [ ! -e "$JDBC" ]; then
	printf "Expected file $JDBC. This is the same .jar file included with CSE111 Lab7, and it can also be downloaded from the sqlite-jdbc GitHub page.\n"
else
	rm "$DB"
	sqlite3 db.sqlite < schema.sql

	javac Populate.java
	java -classpath ".:../sqlite-jdbc-3.47.0.0.jar" Populate
	printf "If population was successful, you can now run script.sql.\n"

	# Clean up
	rm Populate.class
fi
