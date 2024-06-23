//
//  PlayerProfileView.swift
//  iosApp
//
//  Created by Andrew Le on 6/22/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct PlayerProfileView: View {
    @ObservedObject var viewModel: PlayerProfileViewModel = PlayerProfileViewModel()
    @State var playerInfoViewState: PlayerInfoViewState?
    
    var body: some View {
        VStack {
            PlayerProfileInfo(state: playerInfoViewState ?? PlayerInfoViewState(username: "", rivalCode: "", socialNetworks: [:], rank: nil))
            Spacer()
        }
        .onAppear {
            viewModel.playerInfoViewState.subscribe { state in
                if let currentState = state {
                    withAnimation {
                        self.playerInfoViewState = currentState
                    }
                }
            }
        }
    }
}

struct PlayerProfileInfo: View {
    var state: PlayerInfoViewState
    
    var body: some View {
        HStack {
            VStack(alignment: .leading) {
                Text(state.username)
                    .font(.system(size: 28, weight: .bold))
                Text(state.rivalCode ?? "")
                    .font(.system(size: 14, weight: .bold))
            }
            Spacer()
            NavigationLink {
                RankListView()
            } label: {
                Image(state.rank != nil ? String(describing: state.rank).lowercased() : "copper1")
                    .resizable()
                    .frame(width: 64.0, height: 64.0)
                    .saturation(state.rank != nil ? 1.0 : 0.0)
            }
        }
        .frame(maxWidth: .infinity)
        .padding()
    }
}

#Preview {
    PlayerProfileView(playerInfoViewState: PlayerInfoViewState(username: "Andeh", rivalCode: "6164-4734", socialNetworks: [:], rank: nil))
}
