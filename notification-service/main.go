package main

import (
	"encoding/json"
	"log"
	"net/http"
	"time"
)

// NotificationRequest mirrors the payload sent by the Java task-service
type NotificationRequest struct {
	EventType string `json:"eventType"` // e.g. TASK_CREATED, TASK_COMPLETED
	TaskID    int64  `json:"taskId"`
	Title     string `json:"title"`
	Message   string `json:"message"`
}

type NotificationResponse struct {
	Status    string `json:"status"`
	Delivered string `json:"delivered"` // simulated channel, e.g. "email"
	Timestamp string `json:"timestamp"`
}

func healthHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(map[string]string{
		"status":  "UP",
		"service": "notification-service",
	})
}

func notifyHandler(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "method not allowed", http.StatusMethodNotAllowed)
		return
	}

	var req NotificationRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "invalid request body", http.StatusBadRequest)
		return
	}

	// Simulate sending a notification (email/SMS/push).
	// In a real system this would call an email provider, SMS gateway, etc.
	log.Printf("[NOTIFY] event=%s taskId=%d title=%q message=%q\n",
		req.EventType, req.TaskID, req.Title, req.Message)

	resp := NotificationResponse{
		Status:    "SENT",
		Delivered: "email(simulated)",
		Timestamp: time.Now().UTC().Format(time.RFC3339),
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusAccepted)
	json.NewEncoder(w).Encode(resp)
}

func main() {
	mux := http.NewServeMux()
	mux.HandleFunc("/health", healthHandler)
	mux.HandleFunc("/webhook/notify", notifyHandler)

	addr := ":8081"
	log.Printf("notification-service listening on %s\n", addr)
	if err := http.ListenAndServe(addr, mux); err != nil {
		log.Fatal(err)
	}
}
