# Sema

Sema is an Android language-learning application built around interactive practice, lesson progress, and learner rewards.

## Learning experience

The application is designed to support:

- Structured language lessons
- Interactive exercises and vocabulary practice
- Progress tracking and learning goals
- Streaks, experience points, and rewards
- Supabase-backed account and learning data
- AI-assisted learning features where configured

## Technology

- Kotlin
- Jetpack Compose
- Android Gradle Plugin
- Supabase
- Google Generative AI integration

## Requirements

- Android Studio
- Android SDK 36
- A compatible JDK and Android Gradle toolchain

## Build

**Windows PowerShell**

```powershell
.gradlew.bat :app:assembleDebug
```

**macOS / Linux**

```bash
./gradlew :app:assembleDebug
```

## Tests

```bash
./gradlew test
```

## Configuration and security

The Android project uses the Gradle Secrets plugin with `.env` and `.env.example`. Keep private credentials out of Git. Publishable client keys are not substitutes for server-side authorization, and privileged service-role keys must never be embedded in the application.

## Project structure

- `app/` — Android application source and resources
- `supabase/` — database migrations and backend configuration
- `gradle/` — Gradle wrapper and version catalog
- `seed.sql` — development seed data
