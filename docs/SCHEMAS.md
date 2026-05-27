# Database Schema: Lost and Found Reporting System (LFRS)

This document contains the database schema for the LFRS application, implemented using SQLite.

## DBML (Database Markup Language)
Use this code in [dbdiagram.io](https://dbdiagram.io) to generate a visual Entity-Relationship (ER) diagram.

```dbml
Table users {
  id integer [primary key, increment]
  name varchar
  email varchar [unique]
  password varchar
  role varchar // "Standard User", "Administrator"
  student_no varchar
  program varchar
  section varchar
}

Table categories {
  id integer [primary key, increment]
  category_name varchar // "Electronics", "Wallets", "Documents"
}

Table items {
  id integer [primary key, increment]
  description text
  image_path varchar
  date_reported timestamp [default: `now()`]
  status varchar // "Lost", "Found", "Claimed"
  location varchar // e.g., "Library", "Canteen"
  category_id integer
  reporter_id integer
}

Table claimants {
  id integer [primary key, increment]
  name varchar
  id_number varchar // Verification detail
}

Table claims {
  id integer [primary key, increment]
  item_id integer
  claimant_id integer
  claim_date timestamp [default: `now()`]
}

// Relationships
Ref: items.category_id > categories.id // Many items belong to one category
Ref: items.reporter_id > users.id     // Many items reported by one user
Ref: claims.item_id > items.id         // Many claims can be made for one item
Ref: claims.claimant_id > claimants.id // Many claims can be made by one claimant
```

## SQLite Implementation Scripts
The following SQL commands are used to initialize the SQLite database. Note the use of `INSERT OR REPLACE` in seeding scripts to maintain referential integrity.

```sql
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    role TEXT DEFAULT 'Standard User',
    student_no TEXT,
    program TEXT,
    section TEXT,
    avatar_path TEXT
);

CREATE TABLE IF NOT EXISTS categories (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    category_name TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    image_path TEXT,
    date_reported DATETIME DEFAULT CURRENT_TIMESTAMP,
    status TEXT NOT NULL, -- "Lost", "Found", "Claimed"
    location TEXT,
    category_id INTEGER,
    reporter_id INTEGER,
    FOREIGN KEY (category_id) REFERENCES categories (id),
    FOREIGN KEY (reporter_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS claimants (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    id_number TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS claims (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    item_id INTEGER,
    claimant_id INTEGER,
    claim_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES items (id),
    FOREIGN KEY (claimant_id) REFERENCES claimants (id)
);
```
