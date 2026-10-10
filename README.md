# Our Dream (တို့နှစ်ယောက်ရဲ့ အိမ်မက်) 💕

A native Android application designed for couples to manage shared finances, savings goals, relationship milestones, love memories, and partner profiles together.

---

## Features
- **My Finance & Our Love Cards**: Compact balance card in Thai Baht (฿) and love milestone counter calculated from actual records.
- **Offline-First Room Database**: Works 100% offline with on-device SQLite database.
- **Couple Code Connection**: Link partner accounts securely using invitation codes (`DREAM-XXXX`).
- **Savings Management**: Combined & personal savings goals with progress tracking and deposits.
- **Relationship Milestones & Notifications**: Automatic countdowns and Android notifications for 7 days, 3 days, 1 day before, and on anniversaries (Default start: 15 August 2025).
- **Western Zodiac Calculation**: Automatically calculated from birth date with symbols, traits, and Burmese names.
- **Modern Romantic Visuals**: Soft pink, cream, white, warm gold, and subtle traditional Myanmar red ornamental accents.
- **Optional Supabase Cloud Sync**: Database schema and Row Level Security (RLS) policies included in `supabase_schema.sql`.

---

## Phone-Only Guide: Building & Installing the APK via GitHub

If you are working **only from an Android phone**, follow these simple steps to build and install the application directly without a computer:

### Step 1: Upload the Project to GitHub
1. Open Chrome or any browser on your Android phone and visit [github.com](https://github.com).
2. Tap the **+** icon in the top right and select **New repository**.
3. Name your repository `our-dream` (set it to Public or Private) and tap **Create repository**.
4. In the top-right settings menu of AI Studio, tap **Export as ZIP** or push directly to GitHub if connected.
5. Alternatively, on your GitHub repository page on your phone, tap **Add file** -> **Upload files**, select the project ZIP contents, and commit them to the `main` branch.

### Step 2: Open the Actions Tab on Your Phone
1. In your GitHub repository on your phone browser, switch the browser view to **Desktop site** (via browser three-dots menu) for easier navigation.
2. Tap the **Actions** tab near the top navigation bar.

### Step 3: Run the Build Workflow
1. Under **Workflows** on the left, tap **Build Our Dream Android APK**.
2. If triggered automatically by your push, you will see a workflow run in progress.
3. If not started yet, tap the **Run workflow** dropdown button, select branch `main`, and tap the green **Run workflow** button.

### Step 4: Check Build Status
1. Tap the active workflow run (e.g., *Build Debug APK*).
2. Watch the live progress steps. When the build succeeds, a **green checkmark** will appear next to the job name.

### Step 5: Download the APK Artifact
1. On the completed workflow run page, scroll down to the **Artifacts** section at the bottom.
2. Tap on `our-dream-debug-apk`. Your phone will download a ZIP file containing `app-debug.apk`.

### Step 6: Install the APK on Your Android Phone
1. Open your phone's **Files** or **My Files** app and navigate to **Downloads**.
2. Tap the downloaded ZIP file and extract `app-debug.apk`.
3. Tap `app-debug.apk` to install.
4. If prompted with *"Install unknown apps"*, tap **Settings** and toggle **Allow from this source**.
5. Tap **Install** and then **Open** to launch **Our Dream**!

---

## Optional: Configuring Supabase Cloud Sync

Our Dream is completely functional offline using Room database. If you wish to enable cloud sync:

1. Sign up for a free project at [supabase.com](https://supabase.com).
2. Open the **SQL Editor** in your Supabase dashboard.
3. Copy the contents of `supabase_schema.sql` from this repository and run the script to create the tables and Row Level Security policies.
4. Go to **Project Settings** -> **API** to copy your **Project URL** and **anon public key**.
5. Inside the Our Dream Android app, tap **Settings** -> **Cloud Sync & Outbox** -> **Configure**, paste your URL and key, and tap **Save**.
