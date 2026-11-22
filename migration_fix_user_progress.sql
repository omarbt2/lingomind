-- migration_fix_user_progress.sql
-- Adds a UNIQUE constraint on user_progress.user_id so that
-- the on_conflict=user_id upsert in syncUserProgress works correctly.
-- Also sets server-side defaults for new user rows.

-- 1. Ensure user_id is NOT NULL and unique
ALTER TABLE user_progress
  ALTER COLUMN user_id SET NOT NULL;

-- Remove any duplicate-user_id rows (keep the most recent)
DELETE FROM user_progress a
WHERE a.ctid <> (
  SELECT b.ctid FROM user_progress b
  WHERE b.user_id = a.user_id
  ORDER BY b.id DESC LIMIT 1
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_user_progress_user_id
  ON user_progress (user_id);

-- 2. Set server-side defaults for a fresh user_progress row
ALTER TABLE user_progress
  ALTER COLUMN hearts SET DEFAULT 5,
  ALTER COLUMN gems SET DEFAULT 0,
  ALTER COLUMN streak SET DEFAULT 0,
  ALTER COLUMN xp SET DEFAULT 0,
  ALTER COLUMN streak_freeze_count SET DEFAULT 0;

-- 3. Add a function to initialize a new user's progress row
CREATE OR REPLACE FUNCTION public.initialize_user_progress()
RETURNS trigger
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
  INSERT INTO public.user_progress (user_id, hearts, gems, streak, xp, streak_freeze_count)
  VALUES (NEW.id, 5, 0, 0, 0, 0)
  ON CONFLICT (user_id) DO NOTHING;
  RETURN NEW;
END;
$$;

-- Optionally, trigger on auth.user creation (uncomment after verifying auth schema)
-- CREATE OR REPLACE TRIGGER on_auth_user_created
--   AFTER INSERT ON auth.users
--   FOR EACH ROW
--   EXECUTE FUNCTION public.initialize_user_progress();
