-- Remove legacy BI-related columns. Safe to run multiple times on MySQL 8+.
ALTER TABLE students
    DROP COLUMN IF EXISTS binum,
    DROP COLUMN IF EXISTS biemissao,
    DROP COLUMN IF EXISTS bidata;
