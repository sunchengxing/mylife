package main

import (
	"fmt"
	"log"
	"net/http"

	"mylife/app"
	"mylife/handlers"
	"mylife/middleware"
)

func main() {
	db, err := app.InitDB("mylife.db")
	if err != nil {
		log.Fatal(err)
	}
	defer db.Close()

	handlers.SetDB(db)

	mux := http.NewServeMux()

	// Auth (public)
	mux.HandleFunc("POST /api/auth/register", middleware.CORS(handlers.Register))
	mux.HandleFunc("POST /api/auth/login", middleware.CORS(handlers.Login))

	// Records CRUD (auth required)
	mux.HandleFunc("GET /api/{store}", middleware.CORS(middleware.Auth(handlers.ListRecords)))
	mux.HandleFunc("POST /api/{store}", middleware.CORS(middleware.Auth(handlers.CreateRecord)))
	mux.HandleFunc("PUT /api/{store}/{id}", middleware.CORS(middleware.Auth(handlers.UpdateRecord)))
	mux.HandleFunc("DELETE /api/{store}/{id}", middleware.CORS(middleware.Auth(handlers.DeleteRecord)))

	// Sync (auth required)
	mux.HandleFunc("GET /api/sync", middleware.CORS(middleware.Auth(handlers.SyncPull)))
	mux.HandleFunc("POST /api/sync", middleware.CORS(middleware.Auth(handlers.SyncPush)))

	// Stats (auth required)
	mux.HandleFunc("GET /api/stats", middleware.CORS(middleware.Auth(handlers.Stats)))

	// Static files for frontend
	fs := http.FileServer(http.Dir("../frontend"))
	mux.Handle("/", fs)

	fmt.Println("MyLife server running on :8080")
	log.Fatal(http.ListenAndServe(":8080", mux))
}
