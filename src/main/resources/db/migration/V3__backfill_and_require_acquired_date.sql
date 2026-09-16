UPDATE book_records
SET acquired_date = CURRENT_DATE
WHERE acquired_date IS NULL;

ALTER TABLE book_records
ALTER COLUMN acquired_date SET NOT NULL;


