package models

import "time"

type Record struct {
	ID        string    `json:"id"`
	UserID    int64     `json:"user_id"`
	Store     string    `json:"store"` // clothing, food, housing, transport
	Name      string    `json:"name"`
	Category  string    `json:"category,omitempty"`
	Season    string    `json:"season,omitempty"`    // clothing only
	Calories  string    `json:"calories,omitempty"`  // food only
	Cost      string    `json:"cost,omitempty"`
	Note      string    `json:"note,omitempty"`
	Photo     string    `json:"photo,omitempty"`     // base64 data URL
	CreatedAt time.Time `json:"created_at"`
	UpdatedAt time.Time `json:"updated_at"`
}

var ValidStores = map[string]bool{
	"clothing":  true,
	"food":      true,
	"housing":   true,
	"transport": true,
}

type SyncPayload struct {
	Records []Record `json:"records"`
}

type StatsResponse struct {
	TotalRecords   int                `json:"total_records"`
	TotalCost      float64            `json:"total_cost"`
	ByStore        map[string]StoreStat `json:"by_store"`
}

type StoreStat struct {
	Count int     `json:"count"`
	Cost  float64 `json:"cost"`
}
