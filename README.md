# 📱 CareConnect – Kotlin Multiplatform

**Die All-in-One Pflege-App für Pflegedienste, Arztpraxen, Kliniken und Patienten – für Android und iOS aus einer Codebasis.**

CareConnect war ursprünglich eine reine iOS-App (SwiftUI). Dieses Projekt ist der Neubau mit **Kotlin Multiplatform** und **Compose Multiplatform**: Logik und Oberfläche werden einmal in Kotlin geschrieben und laufen auf Android und iOS.

---

## 🌟 Features

### 🏢 Organisationen
- Pflegedienste, Arztpraxen und Kliniken gründen ihre eigene Organisation
- Beitritt per **Einladungscode** oder per **Suche mit Beitrittsanfrage**
- Rollen: Leitung, Mitarbeiter und Patient
- Leitung verwaltet Anfragen, Codes und Mitglieder
- Wechsel zwischen mehreren Organisationen

### 🏠 Startseite
- **Mitarbeiter:** eigener Status (Verfügbar, Beschäftigt, Pause …), Team-Übersicht, „Heute für dich“ mit Tour in Google Maps
- **Patient:** großer Notruf-Button (112, Notfallkontakt oder Pflegedienst)

### 📝 Aufträge
- Offene, aktuelle und erledigte Aufträge
- Annehmen, bearbeiten und abschließen mit **Leistungsnachweis** (Notiz und Unterschrift mit dem Finger)
- Patienten sehen ihre Aufträge als übersichtliche Liste mit Detailseite und Status-Verlauf

### 🚗 Live-Standort
- Wie bei Lieferdiensten: Der Patient sieht den Mitarbeiter als **Auto auf der Karte**, die **Strecke als Linie** und die **Ankunftszeit**
- Standort wird nur geteilt, solange ein Auftrag läuft, und nur nach **Zustimmung von beiden Seiten**
- Karte: OpenStreetMap-Daten (Esri-Kacheln) auf Android, MapKit auf iOS, Route über OSRM – kein API-Key nötig

### 📄 Verordnungen
- Praxen und Kliniken schicken Verordnungen an den Pflegedienst, den der Patient freigegeben hat
- Der Pflegedienst übernimmt sie als einzelne oder wiederkehrende Einsätze (z. B. Mo/Mi/Fr für 4 Wochen)

### 🧑‍🤝‍🧑 Patienten, 💊 Medikamente & 📅 Termine
- Patientenprofil mit Kontakt, Notfallkontakt, Allergien, Vorgeschichte und Karte
- Medikamente nach Morgens, Mittags und Abends
- Termine mit Adresse und Dingen zum Mitbringen

### 🔔 Benachrichtigungen
- Mitarbeiter: neue Aufträge und Verordnungen, **nur wenn sie gerade arbeiten**
- Patienten: Auftrag angenommen, erledigt, geändert oder abgesagt
- Lokale Benachrichtigungen, solange die App läuft

### 🌍 Sprachen & Darstellung
- Deutsch, Englisch, Türkisch, Arabisch (von rechts nach links) und Spanisch, direkt in der App umschaltbar
- Hell- und Dunkelmodus

---

## 🛠 Technik

| Bereich | Umsetzung |
|---|---|
| Sprache & UI | Kotlin, Compose Multiplatform, Material 3 |
| Architektur | MVVM mit Repository Pattern |
| Dependency Injection | Koin |
| Backend | Firebase Authentication, Firestore, Storage (GitLive Firebase SDK) |
| Navigation | Navigation Compose mit typsicheren Routen |
| Lokale Daten | DataStore Preferences |
| Netzwerk | Ktor (Routing über OSRM, optional Geoapify) |
| Bilder | Coil, FileKit |
| Karten | osmdroid (Android), MapKit (iOS) |
| Standort | FusedLocationProvider (Android), CLLocationManager (iOS) |

---

## 🧱 Projektstruktur

```
shared/src/commonMain/kotlin/com/example/careconnect
├── di/                 Koin-Module
├── domain/
│   ├── model/          Datenmodelle (je Klasse/Enum eine Datei)
│   └── repository/     Repository-Interfaces
├── remote/
│   ├── model/          DTOs mit Mappern
│   └── repository/     Firestore-Implementierungen
├── local/              DataStore (Einstellungen)
├── mock/               Beispieldaten und Repository-Mocks für die Previews
├── service/            Standort-Tracking und Benachrichtigungen
├── session/            Aktuelle Organisation
├── platform/           expect/actual (Karte, Geocoder, Standort, Benachrichtigungen)
├── i18n/               Übersetzungen
├── ui/                 Screens, Komponenten und ViewModels pro Feature
└── util/               Helferklassen
```

- `androidApp/` – Einstieg für Android (MainActivity, Berechtigungen)
- `iosApp/` – Einstieg für iOS (Xcode-Projekt)

Jedes Composable hat eine eigene Datei mit Preview. Die Previews laufen über die Mocks in `mock/repository`, dadurch braucht man für sie weder Firebase noch Koin.

---

## 🚀 Projekt starten

### Voraussetzungen
- Android Studio (aktuelle Version) mit Kotlin-Multiplatform-Plugin
- Xcode für die iOS-App
- Ein Firebase-Projekt mit Authentication (E-Mail/Passwort), Firestore und Storage

### Firebase einrichten
1. `google-services.json` nach `androidApp/` legen
2. `GoogleService-Info.plist` nach `iosApp/iosApp/` legen
3. Inhalt von `firestore.rules` und `storage.rules` in der Firebase-Konsole veröffentlichen

### Starten
- **Android:** Run-Konfiguration `androidApp` oder `./gradlew :androidApp:assembleDebug`
- **iOS:** `iosApp/iosApp.xcodeproj` in Xcode öffnen, Team in `iosApp/Configuration/Config.xcconfig` eintragen und starten

### Optional
- Geoapify-Key in `shared/src/commonMain/kotlin/com/example/careconnect/remote/Secrets.kt` für die Adresssuche mit Vorschlägen
- Im Emulator unter **⋯ → Location** einen Standort oder eine Route setzen, um den Live-Standort zu testen

---

## 👤 Autor

**Ali Chehab** – Abschlussprojekt im Android-Modul am Syntax Institut
