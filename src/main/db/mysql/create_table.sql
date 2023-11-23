USE cache;

CREATE TABLE IF NOT EXISTS factorization_results (
    number		INT PRIMARY KEY
    , factors	JSON NOT NULL
);