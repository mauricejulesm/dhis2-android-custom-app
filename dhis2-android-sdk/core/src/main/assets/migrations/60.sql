CREATE TABLE FileResource (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT NOT NULL UNIQUE, name TEXT, created TEXT, lastUpdated TEXT, contentType TEXT, contentLength INTEGER, path TEXT, state TEXT);