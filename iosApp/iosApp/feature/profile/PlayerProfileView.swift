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
    @State var goalListViewState: ViewState<UILadderData, NSString>?
    @State var goalData: UILadderData?
    @State var goalError: String?
    
    var body: some View {
        VStack {
            PlayerProfileInfo(state: playerInfoViewState ?? PlayerInfoViewState(username: "", rivalCode: "", socialNetworks: [:], rank: nil))
            if (goalData != nil) {
                LadderGoals(
                    data: goalData,
                    onCompletedChanged: { id in
                        viewModel.goalListViewModel.handleAction(action: RankListAction.OnGoalToggleComplete(id: id))
                    },
                    onHiddenChanged: { id in
                        viewModel.goalListViewModel.handleAction(action: RankListAction.OnGoalToggleHidden(id: id))
                    }
                )
            }
            if (goalError != nil) {
                Text(goalError!).font(.system(size: 22, weight: .bold))
            }
        }
        .onAppear {
            viewModel.playerInfoViewState.subscribe { state in
                if let currentState = state {
                    withAnimation {
                        self.playerInfoViewState = currentState
                    }
                }
            }
            viewModel.goalListViewModel.state.subscribe { state in
                if let currentGoalState = state {
                    goalListViewState = currentGoalState
                    if let success = goalListViewState as? ViewStateSuccess<UILadderData> {
                        goalData = success.data
                    } else if let error = goalListViewState as? ViewStateError<NSString> {
                        goalError = String(error.error!)
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
