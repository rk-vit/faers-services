-- Flyway migration V2: Create vehicles and dispatch_assignments tables

CREATE TABLE IF NOT EXISTS vehicles (
    id VARCHAR(64) PRIMARY KEY,
    vehicle_type VARCHAR(50) NOT NULL,
    capability_tier VARCHAR(50) NOT NULL,
    license_plate VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
    current_latitude DOUBLE PRECISION NOT NULL,
    current_longitude DOUBLE PRECISION NOT NULL,
    base_station_id VARCHAR(64),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_vehicles_status ON vehicles(status);
CREATE INDEX IF NOT EXISTS idx_vehicles_type ON vehicles(vehicle_type);

CREATE TABLE IF NOT EXISTS dispatch_assignments (
    id VARCHAR(64) PRIMARY KEY,
    emergency_request_id VARCHAR(64) NOT NULL,
    vehicle_id VARCHAR(64) NOT NULL REFERENCES vehicles(id),
    status VARCHAR(50) NOT NULL DEFAULT 'ASSIGNED',
    estimated_distance_km DOUBLE PRECISION,
    estimated_eta_seconds INTEGER,
    assigned_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP WITH TIME ZONE,
    failure_reason TEXT
);

CREATE INDEX IF NOT EXISTS idx_dispatch_assignments_emergency ON dispatch_assignments(emergency_request_id);
CREATE INDEX IF NOT EXISTS idx_dispatch_assignments_vehicle ON dispatch_assignments(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_dispatch_assignments_status ON dispatch_assignments(status);
