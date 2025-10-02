import SwiftUI
import ComposeApp
import di

@main
struct iOSApp: App {
    init() {
        KoinKt.doInitKoin()
    }
    var body: some Scene {
        WindowGroup {
            ComposeViewController()
        }
    }
}

struct ComposeViewController: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        Main_iosKt.MainViewController()
    }
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

