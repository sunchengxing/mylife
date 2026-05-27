package handlers

import (
	"database/sql"
	"encoding/json"
	"net/http"
	"time"

	"mylife/middleware"
	"mylife/models"

	"github.com/golang-jwt/jwt/v5"
	"golang.org/x/crypto/bcrypt"
)

var db *sql.DB

func SetDB(d *sql.DB) {
	db = d
}

// ─── Auth ───

type registerReq struct {
	Username string `json:"username"`
	Password string `json:"password"`
}

type loginReq struct {
	Username string `json:"username"`
	Password string `json:"password"`
}

type authResp struct {
	Token    string `json:"token"`
	Username string `json:"username"`
	UserID   int64  `json:"user_id"`
}

func Register(w http.ResponseWriter, r *http.Request) {
	var req registerReq
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, `{"error":"bad request"}`, http.StatusBadRequest)
		return
	}
	if req.Username == "" || len(req.Password) < 6 {
		http.Error(w, `{"error":"username and password (6+ chars) required"}`, http.StatusBadRequest)
		return
	}
	hash, _ := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
	res, err := db.Exec("INSERT INTO users (username, password_hash) VALUES (?, ?)", req.Username, string(hash))
	if err != nil {
		http.Error(w, `{"error":"username already exists"}`, http.StatusConflict)
		return
	}
	uid, _ := res.LastInsertId()
	token := issueToken(req.Username, uid)
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(authResp{Token: token, Username: req.Username, UserID: uid})
}

func Login(w http.ResponseWriter, r *http.Request) {
	var req loginReq
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, `{"error":"bad request"}`, http.StatusBadRequest)
		return
	}
	var id int64
	var hash string
	err := db.QueryRow("SELECT id, password_hash FROM users WHERE username = ?", req.Username).Scan(&id, &hash)
	if err != nil || bcrypt.CompareHashAndPassword([]byte(hash), []byte(req.Password)) != nil {
		http.Error(w, `{"error":"invalid credentials"}`, http.StatusUnauthorized)
		return
	}
	token := issueToken(req.Username, id)
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(authResp{Token: token, Username: req.Username, UserID: id})
}

func issueToken(username string, userID int64) string {
	claims := jwt.MapClaims{
		"username": username,
		"user_id":  userID,
		"exp":      time.Now().Add(7 * 24 * time.Hour).Unix(),
	}
	t, _ := jwt.NewWithClaims(jwt.SigningMethodHS256, claims).SignedString(middleware.GetJWTSecret())
	return t
}

// ─── Records CRUD ───

func ListRecords(w http.ResponseWriter, r *http.Request) {
	store := r.PathValue("store")
	if !models.ValidStores[store] {
		http.Error(w, `{"error":"invalid store"}`, http.StatusBadRequest)
		return
	}
	uid := middleware.GetUserID(r)

	q := r.URL.Query().Get("q")
	category := r.URL.Query().Get("category")
	from := r.URL.Query().Get("from")
	to := r.URL.Query().Get("to")

	query := "SELECT id, user_id, store, name, category, season, calories, cost, note, photo, created_at, updated_at FROM records WHERE user_id = ? AND store = ?"
	args := []interface{}{uid, store}

	if q != "" {
		query += " AND (name LIKE ? OR note LIKE ?)"
		args = append(args, "%"+q+"%", "%"+q+"%")
	}
	if category != "" {
		query += " AND category = ?"
		args = append(args, category)
	}
	if from != "" {
		query += " AND created_at >= ?"
		args = append(args, from)
	}
	if to != "" {
		query += " AND created_at <= ?"
		args = append(args, to)
	}
	query += " ORDER BY created_at DESC"

	rows, err := db.Query(query, args...)
	if err != nil {
		http.Error(w, `{"error":"query failed"}`, http.StatusInternalServerError)
		return
	}
	defer rows.Close()

	var records []models.Record
	for rows.Next() {
		var rec models.Record
		var createdAt, updatedAt string
		err := rows.Scan(&rec.ID, &rec.UserID, &rec.Store, &rec.Name, &rec.Category, &rec.Season, &rec.Calories, &rec.Cost, &rec.Note, &rec.Photo, &createdAt, &updatedAt)
		if err != nil {
			continue
		}
		rec.CreatedAt = parseTime(createdAt)
		rec.UpdatedAt = parseTime(updatedAt)
		records = append(records, rec)
	}
	if records == nil {
		records = []models.Record{}
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(records)
}

func CreateRecord(w http.ResponseWriter, r *http.Request) {
	store := r.PathValue("store")
	if !models.ValidStores[store] {
		http.Error(w, `{"error":"invalid store"}`, http.StatusBadRequest)
		return
	}
	uid := middleware.GetUserID(r)

	var rec models.Record
	if err := json.NewDecoder(r.Body).Decode(&rec); err != nil {
		http.Error(w, `{"error":"bad request"}`, http.StatusBadRequest)
		return
	}
	if rec.Name == "" {
		http.Error(w, `{"error":"name required"}`, http.StatusBadRequest)
		return
	}
	rec.Store = store
	rec.UserID = uid
	now := time.Now()
	rec.CreatedAt = now
	rec.UpdatedAt = now

	_, err := db.Exec(
		"INSERT INTO records (id, user_id, store, name, category, season, calories, cost, note, photo, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
		rec.ID, rec.UserID, rec.Store, rec.Name, rec.Category, rec.Season, rec.Calories, rec.Cost, rec.Note, rec.Photo, rec.CreatedAt, rec.UpdatedAt,
	)
	if err != nil {
		http.Error(w, `{"error":"insert failed"}`, http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(rec)
}

func UpdateRecord(w http.ResponseWriter, r *http.Request) {
	store := r.PathValue("store")
	id := r.PathValue("id")
	if !models.ValidStores[store] {
		http.Error(w, `{"error":"invalid store"}`, http.StatusBadRequest)
		return
	}
	uid := middleware.GetUserID(r)

	var rec models.Record
	if err := json.NewDecoder(r.Body).Decode(&rec); err != nil {
		http.Error(w, `{"error":"bad request"}`, http.StatusBadRequest)
		return
	}
	rec.UpdatedAt = time.Now()

	// Read existing created_at to preserve it
	var createdAt string
	db.QueryRow("SELECT created_at FROM records WHERE id=? AND user_id=? AND store=?", id, uid, store).Scan(&createdAt)

	res, err := db.Exec(
		"UPDATE records SET name=?, category=?, season=?, calories=?, cost=?, note=?, photo=?, updated_at=? WHERE id=? AND user_id=? AND store=?",
		rec.Name, rec.Category, rec.Season, rec.Calories, rec.Cost, rec.Note, rec.Photo, rec.UpdatedAt, id, uid, store,
	)
	if err != nil {
		http.Error(w, `{"error":"update failed"}`, http.StatusInternalServerError)
		return
	}
	n, _ := res.RowsAffected()
	if n == 0 {
		http.Error(w, `{"error":"not found"}`, http.StatusNotFound)
		return
	}
	rec.ID = id
	rec.Store = store
	rec.UserID = uid
	rec.CreatedAt = parseTime(createdAt)
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(rec)
}

func DeleteRecord(w http.ResponseWriter, r *http.Request) {
	store := r.PathValue("store")
	id := r.PathValue("id")
	uid := middleware.GetUserID(r)

	res, err := db.Exec("DELETE FROM records WHERE id=? AND user_id=? AND store=?", id, uid, store)
	if err != nil {
		http.Error(w, `{"error":"delete failed"}`, http.StatusInternalServerError)
		return
	}
	n, _ := res.RowsAffected()
	if n == 0 {
		http.Error(w, `{"error":"not found"}`, http.StatusNotFound)
		return
	}
	w.WriteHeader(http.StatusNoContent)
}

// ─── Sync ───

func SyncPull(w http.ResponseWriter, r *http.Request) {
	uid := middleware.GetUserID(r)
	since := r.URL.Query().Get("since")

	query := "SELECT id, user_id, store, name, category, season, calories, cost, note, photo, created_at, updated_at FROM records WHERE user_id = ?"
	args := []interface{}{uid}
	if since != "" {
		query += " AND updated_at > ?"
		args = append(args, since)
	}
	query += " ORDER BY updated_at ASC"

	rows, err := db.Query(query, args...)
	if err != nil {
		http.Error(w, `{"error":"query failed"}`, http.StatusInternalServerError)
		return
	}
	defer rows.Close()

	var records []models.Record
	for rows.Next() {
		var rec models.Record
		var createdAt, updatedAt string
		if err := rows.Scan(&rec.ID, &rec.UserID, &rec.Store, &rec.Name, &rec.Category, &rec.Season, &rec.Calories, &rec.Cost, &rec.Note, &rec.Photo, &createdAt, &updatedAt); err != nil {
			continue
		}
		rec.CreatedAt = parseTime(createdAt)
		rec.UpdatedAt = parseTime(updatedAt)
		records = append(records, rec)
	}
	if records == nil {
		records = []models.Record{}
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(models.SyncPayload{Records: records})
}

func SyncPush(w http.ResponseWriter, r *http.Request) {
	uid := middleware.GetUserID(r)
	var payload models.SyncPayload
	if err := json.NewDecoder(r.Body).Decode(&payload); err != nil {
		http.Error(w, `{"error":"bad request"}`, http.StatusBadRequest)
		return
	}

	for _, rec := range payload.Records {
		rec.UserID = uid
		if rec.CreatedAt.IsZero() {
			rec.CreatedAt = time.Now()
		}
		rec.UpdatedAt = time.Now()

		db.Exec(
			"INSERT OR REPLACE INTO records (id, user_id, store, name, category, season, calories, cost, note, photo, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
			rec.ID, rec.UserID, rec.Store, rec.Name, rec.Category, rec.Season, rec.Calories, rec.Cost, rec.Note, rec.Photo, rec.CreatedAt, rec.UpdatedAt,
		)
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(map[string]int{"synced": len(payload.Records)})
}

// ─── Stats ───

func Stats(w http.ResponseWriter, r *http.Request) {
	uid := middleware.GetUserID(r)
	resp := models.StatsResponse{
		ByStore: make(map[string]models.StoreStat),
	}

	totalCount := 0
	totalCost := 0.0
	for store := range models.ValidStores {
		var count int
		var cost sql.NullFloat64
		db.QueryRow("SELECT COUNT(*), COALESCE(SUM(CAST(cost AS REAL)), 0) FROM records WHERE user_id = ? AND store = ?", uid, store).Scan(&count, &cost)
		c := 0.0
		if cost.Valid {
			c = cost.Float64
		}
		totalCount += count
		totalCost += c
		resp.ByStore[store] = models.StoreStat{Count: count, Cost: c}
	}
	resp.TotalRecords = totalCount
	resp.TotalCost = totalCost

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(resp)
}

// ─── Helpers ───

func parseTime(s string) time.Time {
	for _, fmt := range []string{
		"2006-01-02 15:04:05",
		"2006-01-02T15:04:05Z",
		"2006-01-02T15:04:05.999999999Z",
		time.RFC3339,
	} {
		if t, err := time.Parse(fmt, s); err == nil {
			return t
		}
	}
	return time.Time{}
}
