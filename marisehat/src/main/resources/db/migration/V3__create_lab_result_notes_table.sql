CREATE TABLE lab_result_notes (
    note TEXT NOT NULL,
    lab_result_id UUID NOT NULL REFERENCES lab_results (id) ON DELETE CASCADE
);