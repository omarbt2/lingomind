-- Phase 1: Enable RLS on all five tables
ALTER TABLE public.phases ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.units ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.lessons ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.exercises ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.flashcards ENABLE ROW LEVEL SECURITY;

-- Phase 2: Create public read policies
CREATE POLICY "phases_public_read" ON public.phases
    FOR SELECT USING (true);

CREATE POLICY "units_public_read" ON public.units
    FOR SELECT USING (true);

CREATE POLICY "lessons_public_read" ON public.lessons
    FOR SELECT USING (true);

CREATE POLICY "exercises_public_read" ON public.exercises
    FOR SELECT USING (true);

CREATE POLICY "flashcards_public_read" ON public.flashcards
    FOR SELECT USING (true);