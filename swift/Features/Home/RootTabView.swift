import SwiftUI

struct RootTabView: View {
    var body: some View {
        TabView {
            HomeView()
                .tabItem { Label("Home", systemImage: "house") }
            PrayerView()
                .tabItem { Label("Prières", systemImage: "clock") }
            FavoritesView()
                .tabItem { Label("Favoris", systemImage: "bookmark") }
            Top24View()
                .tabItem { Label("Top24", systemImage: "chart.bar") }
            SettingsView()
                .tabItem { Label("Paramètres", systemImage: "gearshape") }
        }
    }
}
