import SwiftUI

struct ContentView: View {
    var body: some View {
        TabView {
            NavigationStack {
                VStack(spacing: 12) {
                    Text("TN News")
                        .font(.title.bold())
                    Text("Base iOS chargée avec succès")
                        .foregroundStyle(.secondary)
                }
                .navigationTitle("Accueil")
            }
            .tabItem { Label("Home", systemImage: "house") }

            NavigationStack {
                Text("Prières")
                    .navigationTitle("Prières")
            }
            .tabItem { Label("Prières", systemImage: "clock") }

            NavigationStack {
                Text("Favoris")
                    .navigationTitle("Favoris")
            }
            .tabItem { Label("Favoris", systemImage: "bookmark") }

            NavigationStack {
                Text("Paramètres")
                    .navigationTitle("Paramètres")
            }
            .tabItem { Label("Paramètres", systemImage: "gearshape") }
        }
    }
}

#Preview {
    ContentView()
}
