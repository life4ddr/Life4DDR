//
//  MainView.swift
//  iosApp
//
//  Created by Andrew Le on 6/20/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct MainView: View {
    var body: some View {
        TabView {
            PlayerProfileView()
                .tabItem {
                    Label("Profile", systemImage: "person")
                }
            ContentView()
                .tabItem {
                    Label("Scores", systemImage: "music.note.list")
                }
            TrialListView()
                .tabItem {
                    Label("Trials", systemImage: "square.grid.2x2")
                }
        }.navigationBarBackButtonHidden(true)
    }
}

#Preview {
    MainView()
}
