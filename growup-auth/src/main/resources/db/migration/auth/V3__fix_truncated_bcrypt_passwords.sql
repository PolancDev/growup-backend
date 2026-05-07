-- Fix truncated BCrypt hashes for demo users
-- The original V2 migration had 59-char hashes (bcrypt requires 60 chars)
-- This updates all passwords to a valid BCrypt hash for: Abcd@123

UPDATE users SET password = '$2b$10$btpY.FrtH7xdKlXPAG3YmOwHBJBe8XY.o4nDLLz4/LvqIn3EpmMG2'
WHERE email IN ('admin@growup.com', 'student@growup.com', 'maria@growup.com');