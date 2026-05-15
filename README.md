# Yakshagana-Loka (Android)

Professional internship-ready Android app scaffold for:
- Tonight's live shows
- 30-day mela schedule
- Performance map
- Artist directory with vesha gallery
- Talamaddale radio (ExoPlayer)
- Event reminder notifications with poster sharing

## Open in Android Studio
1. Open Android Studio.
2. Click **Open** and select this folder: `Yakshagana-Mela`.
3. Let Gradle sync and download dependencies.
4. Run on emulator/device (Android 8.0+).

## Required setup
- **Google Maps API key**: add your key in `AndroidManifest.xml` using:
  ```xml
  <meta-data
      android:name="com.google.android.geo.API_KEY"
      android:value="YOUR_MAPS_API_KEY" />
  ```
- **Firebase**:
  - Create a Firebase project
  - Add Android app id: `com.yakshagana.loka`
  - Download `google-services.json` into `app/` if you later enable Analytics/Crashlytics
  - Firestore/Auth SDKs are already included

## Notes
- Current app uses a `FakeContentRepository` with sample data.
- Replace with Firestore repository for live manager updates.
- Reminder popup supports quick "Share Poster" action.
