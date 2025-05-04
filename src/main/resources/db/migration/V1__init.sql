CREATE TABLE type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE subscription (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    type_id INTEGER NOT NULL,
    element_id UUID NOT NULL,
    FOREIGN KEY (type_id) REFERENCES type (id)
);

CREATE INDEX idx_subscription_element_id ON subscription(element_id);

INSERT INTO type (name) VALUES
    ('EVENT'),
    ('USER_MARKER');