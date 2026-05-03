import SwiftUI
import UIKit

@main
struct TNNewsAppApp: App {
    init() {
        TNTabBarStyle.configure()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

enum TNTabBarStyle {
    /// Couleur verte de la marque (cf. androidGreen #36C750).
    static let selectedColor = UIColor(red: 0x36 / 255.0,
                                       green: 0xC7 / 255.0,
                                       blue: 0x50 / 255.0,
                                       alpha: 1.0)

    /// Fond sombre de la barre, rappelant le bandeau noir/anthracite de la maquette.
    static let backgroundColor = UIColor(red: 0x1C / 255.0,
                                         green: 0x1C / 255.0,
                                         blue: 0x1E / 255.0,
                                         alpha: 1.0)

    /// Couleur d'icône inactive : un blanc cassé pour rester lisible sur fond sombre.
    static let unselectedColor = UIColor(white: 1.0, alpha: 0.55)

    static func configure() {
        let appearance = UITabBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.backgroundColor = backgroundColor
        appearance.shadowColor = .clear

        let selectedAttrs: [NSAttributedString.Key: Any] = [
            .foregroundColor: selectedColor,
            .font: UIFont.systemFont(ofSize: 11, weight: .semibold)
        ]
        let normalAttrs: [NSAttributedString.Key: Any] = [
            .foregroundColor: unselectedColor,
            .font: UIFont.systemFont(ofSize: 11, weight: .medium)
        ]

        appearance.stackedLayoutAppearance.selected.iconColor = selectedColor
        appearance.stackedLayoutAppearance.selected.titleTextAttributes = selectedAttrs
        appearance.stackedLayoutAppearance.normal.iconColor = unselectedColor
        appearance.stackedLayoutAppearance.normal.titleTextAttributes = normalAttrs

        appearance.inlineLayoutAppearance.selected.iconColor = selectedColor
        appearance.inlineLayoutAppearance.selected.titleTextAttributes = selectedAttrs
        appearance.inlineLayoutAppearance.normal.iconColor = unselectedColor
        appearance.inlineLayoutAppearance.normal.titleTextAttributes = normalAttrs

        appearance.compactInlineLayoutAppearance.selected.iconColor = selectedColor
        appearance.compactInlineLayoutAppearance.selected.titleTextAttributes = selectedAttrs
        appearance.compactInlineLayoutAppearance.normal.iconColor = unselectedColor
        appearance.compactInlineLayoutAppearance.normal.titleTextAttributes = normalAttrs

        UITabBar.appearance().standardAppearance = appearance
        UITabBar.appearance().scrollEdgeAppearance = appearance
        UITabBar.appearance().tintColor = selectedColor
        UITabBar.appearance().unselectedItemTintColor = unselectedColor
    }
}
