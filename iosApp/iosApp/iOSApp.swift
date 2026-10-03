import SwiftUI
import FirebaseCore

@main
struct iOSApp: App {

    // Firebase muss vor dem ersten Zugriff aus Kotlin konfiguriert sein
    init() {
        FirebaseApp.configure()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
