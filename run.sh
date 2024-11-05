#!/bin/bash
 
start_service() {
    echo "Starting $1..."
    (cd $2 && $3) &
    if [ $? -ne 0 ]; then
        echo "Failed to start $1. Exiting."
        exit 1
    fi
}
 
# Start services
start_service "fmts service" "./fmts-backend/" "npm start"
start_service "Spring Boot backend" "./backend-trade-marshals/" "mvn spring-boot:run"
start_service "Node mid-tier" "./midtier-trade-marshals/" "npm run dev"
start_service "Angular frontend" "./frontend-trade-marshals/" "ng serve -o"
 
# Wait for all background processes to finish
wait
 
echo "All services started."