package app

import (
	"database/sql"
	"fmt"

	_ "github.com/mattn/go-sqlite3"
)

func InitDB(path string) (*sql.DB, error) {
	db, err := sql.Open("sqlite3", path+"?_journal_mode=WAL&_busy_timeout=5000")
	if err != nil {
		return nil, fmt.Errorf("open db: %w", err)
	}

	schema := `
	CREATE TABLE IF NOT EXISTS users (
		id         INTEGER PRIMARY KEY AUTOINCREMENT,
		username   TEXT    UNIQUE NOT NULL,
		password_hash TEXT NOT NULL,
		created_at DATETIME DEFAULT CURRENT_TIMESTAMP
	);

	CREATE TABLE IF NOT EXISTS records (
		id         TEXT    PRIMARY KEY,
		user_id    INTEGER NOT NULL,
		store      TEXT    NOT NULL,
		name       TEXT    NOT NULL,
		category   TEXT    DEFAULT '',
		season     TEXT    DEFAULT '',
		calories   TEXT    DEFAULT '',
		cost       TEXT    DEFAULT '',
		note       TEXT    DEFAULT '',
		photo      TEXT    DEFAULT '',
		created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
		updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
		FOREIGN KEY (user_id) REFERENCES users(id)
	);

	CREATE INDEX IF NOT EXISTS idx_records_user_store ON records(user_id, store);
	CREATE INDEX IF NOT EXISTS idx_records_updated    ON records(user_id, updated_at);
	`

	if _, err := db.Exec(schema); err != nil {
		return nil, fmt.Errorf("init schema: %w", err)
	}
	return db, nil
}
