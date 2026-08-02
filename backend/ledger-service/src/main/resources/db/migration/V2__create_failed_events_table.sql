CREATE TABLE failed_events (
    id UUID PRIMARY KEY,
    original_topic VARCHAR(255) NOT NULL,
    partition_number INTEGER NOT NULL,
    original_offset BIGINT NOT NULL,
    message_key VARCHAR(255),
    payload TEXT NOT NULL,
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_failed_events_original_topic
    ON failed_events (original_topic);

CREATE INDEX idx_failed_events_created_at
    ON failed_events (created_at);