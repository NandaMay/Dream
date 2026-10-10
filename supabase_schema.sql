-- ====================================================================
-- OUR DREAM — SUPABASE POSTGRESQL SCHEMA WITH ROW LEVEL SECURITY (RLS)
-- ====================================================================

-- 1. EXTENSIONS
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. COUPLES TABLE
CREATE TABLE IF NOT EXISTS public.couples (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    couple_code VARCHAR(20) UNIQUE NOT NULL,
    partner1_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    partner2_id UUID REFERENCES auth.users(id) ON DELETE SET NULL,
    relationship_start_date DATE NOT NULL DEFAULT '2025-08-15',
    is_connected BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

ALTER TABLE public.couples ENABLE ROW LEVEL SECURITY;

-- Couples RLS: Only partners who belong to the couple can view or update it
CREATE POLICY "Users can view their own couple"
    ON public.couples FOR SELECT
    USING (auth.uid() = partner1_id OR auth.uid() = partner2_id);

CREATE POLICY "Partner 1 can create a couple"
    ON public.couples FOR INSERT
    WITH CHECK (auth.uid() = partner1_id);

CREATE POLICY "Partners can update their couple"
    ON public.couples FOR UPDATE
    USING (auth.uid() = partner1_id OR auth.uid() = partner2_id);

-- 3. PROFILES TABLE
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email TEXT,
    display_name TEXT NOT NULL,
    birth_date DATE,
    zodiac_sign TEXT,
    bio TEXT,
    photo_url TEXT,
    couple_id UUID REFERENCES public.couples(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now()),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

-- Profiles RLS: Self can view & edit; partner in same couple can view
CREATE POLICY "Users can view self profile"
    ON public.profiles FOR SELECT
    USING (auth.uid() = id);

CREATE POLICY "Users can view partner profile in same couple"
    ON public.profiles FOR SELECT
    USING (
        couple_id IS NOT NULL AND
        couple_id IN (
            SELECT id FROM public.couples
            WHERE partner1_id = auth.uid() OR partner2_id = auth.uid()
        )
    );

CREATE POLICY "Users can insert their own profile"
    ON public.profiles FOR INSERT
    WITH CHECK (auth.uid() = id);

CREATE POLICY "Users can update their own profile"
    ON public.profiles FOR UPDATE
    USING (auth.uid() = id);

-- 4. TRANSACTIONS TABLE
CREATE TABLE IF NOT EXISTS public.transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    couple_id UUID REFERENCES public.couples(id) ON DELETE CASCADE,
    type VARCHAR(10) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    amount NUMERIC(12, 2) NOT NULL CHECK (amount > 0),
    category VARCHAR(50) NOT NULL,
    date BIGINT NOT NULL, -- Epoch millis
    description TEXT,
    payer_or_recipient TEXT,
    scope VARCHAR(10) NOT NULL DEFAULT 'PERSONAL' CHECK (scope IN ('PERSONAL', 'SHARED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now()),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

ALTER TABLE public.transactions ENABLE ROW LEVEL SECURITY;

-- Transactions RLS:
-- Private transactions: user can view/manage only their own.
-- Shared transactions: either partner in the couple can view/manage.
CREATE POLICY "Transactions view policy"
    ON public.transactions FOR SELECT
    USING (
        auth.uid() = user_id
        OR (
            scope = 'SHARED'
            AND couple_id IN (
                SELECT id FROM public.couples
                WHERE partner1_id = auth.uid() OR partner2_id = auth.uid()
            )
        )
    );

CREATE POLICY "Transactions insert policy"
    ON public.transactions FOR INSERT
    WITH CHECK (
        auth.uid() = user_id
        AND (
            scope = 'PERSONAL'
            OR (
                scope = 'SHARED'
                AND couple_id IN (
                    SELECT id FROM public.couples
                    WHERE partner1_id = auth.uid() OR partner2_id = auth.uid()
                )
            )
        )
    );

CREATE POLICY "Transactions update policy"
    ON public.transactions FOR UPDATE
    USING (
        auth.uid() = user_id
        OR (
            scope = 'SHARED'
            AND couple_id IN (
                SELECT id FROM public.couples
                WHERE partner1_id = auth.uid() OR partner2_id = auth.uid()
            )
        )
    );

CREATE POLICY "Transactions delete policy"
    ON public.transactions FOR DELETE
    USING (
        auth.uid() = user_id
        OR (
            scope = 'SHARED'
            AND couple_id IN (
                SELECT id FROM public.couples
                WHERE partner1_id = auth.uid() OR partner2_id = auth.uid()
            )
        )
    );

-- 5. SAVINGS GOALS TABLE
CREATE TABLE IF NOT EXISTS public.savings_goals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    couple_id UUID REFERENCES public.couples(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    target_amount NUMERIC(12, 2) NOT NULL CHECK (target_amount > 0),
    current_amount NUMERIC(12, 2) NOT NULL DEFAULT 0 CHECK (current_amount >= 0),
    target_date DATE,
    scope VARCHAR(10) NOT NULL DEFAULT 'SHARED' CHECK (scope IN ('PERSONAL', 'SHARED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now()),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

ALTER TABLE public.savings_goals ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Savings goals select policy"
    ON public.savings_goals FOR SELECT
    USING (
        auth.uid() = user_id
        OR (
            scope = 'SHARED'
            AND couple_id IN (
                SELECT id FROM public.couples
                WHERE partner1_id = auth.uid() OR partner2_id = auth.uid()
            )
        )
    );

CREATE POLICY "Savings goals insert policy"
    ON public.savings_goals FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Savings goals update policy"
    ON public.savings_goals FOR UPDATE
    USING (
        auth.uid() = user_id
        OR (
            scope = 'SHARED'
            AND couple_id IN (
                SELECT id FROM public.couples
                WHERE partner1_id = auth.uid() OR partner2_id = auth.uid()
            )
        )
    );

CREATE POLICY "Savings goals delete policy"
    ON public.savings_goals FOR DELETE
    USING (auth.uid() = user_id);

-- 6. MEMORY EVENTS TABLE
CREATE TABLE IF NOT EXISTS public.memory_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    couple_id UUID REFERENCES public.couples(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    event_type VARCHAR(30) NOT NULL,
    event_date DATE NOT NULL,
    description TEXT,
    photo_url TEXT,
    is_recurring_yearly BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT timezone('utc'::text, now())
);

ALTER TABLE public.memory_events ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Memory events select policy"
    ON public.memory_events FOR SELECT
    USING (
        auth.uid() = user_id
        OR couple_id IN (
            SELECT id FROM public.couples
            WHERE partner1_id = auth.uid() OR partner2_id = auth.uid()
        )
    );

CREATE POLICY "Memory events insert policy"
    ON public.memory_events FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Memory events update/delete policy"
    ON public.memory_events FOR ALL
    USING (
        auth.uid() = user_id
        OR couple_id IN (
            SELECT id FROM public.couples
            WHERE partner1_id = auth.uid() OR partner2_id = auth.uid()
        )
    );

-- Helper trigger for automatic profile creation on Supabase Auth signup
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS trigger AS $$
BEGIN
  INSERT INTO public.profiles (id, email, display_name, created_at, updated_at)
  VALUES (new.id, new.email, COALESCE(new.raw_user_meta_data->>'full_name', 'Partner'), now(), now());
  RETURN new;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE OR REPLACE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();
