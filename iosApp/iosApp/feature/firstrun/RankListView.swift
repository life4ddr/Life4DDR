//
//  RankListView.swift
//  iosApp
//
//  Created by Andrew Le on 2/26/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct RankListView: View {
    var isFirstRun: Bool
    @ObservedObject var viewModel: RankListViewModel
    @State var state: UIRankList?
    var onPlacementClicked: () -> (Void)
    var goToMainView: () -> (Void)
    
    init(isFirstRun: Bool = false, onPlacementClicked: @escaping () -> (Void) = {}, goToMainView: @escaping () -> (Void) = {}) {
        self.isFirstRun = isFirstRun
        viewModel = RankListViewModel(isFirstRun: isFirstRun)
        self.onPlacementClicked = onPlacementClicked
        self.goToMainView = goToMainView
    }
    
    var body: some View {
        VStack {
            RankSelection(
                ranks: state?.ranks as? [LadderRank?] ?? [],
                noRank: state?.noRank ?? UINoRank.Companion().DEFAULT,
                onRankClicked: viewModel.setRankSelected,
                onRankRejected: {
                    viewModel.saveRank(ladderRank: nil)
                    goToMainView()
                }
            )
            // TODO: add LadderGoals here when ladderData gets set in state
            Spacer()
            if (state?.firstRun != nil) {
                FirstRunWidget(
                    data: (state?.firstRun)!,
                    onPlacementClicked: {
                        viewModel.moveToPlacements()
                        onPlacementClicked()
                    }
                )
            }
        }
        .navigationBarBackButtonHidden(isFirstRun)
        .navigationTitle(state?.titleText.desc().localized() ?? "")
        .navigationBarTitleDisplayMode(.large)
        .onAppear {
            viewModel.state.subscribe { state in
                if let currentState = state {
                    withAnimation {
                        self.state = currentState
                    }
                }
            }
        }
    }
}

struct FirstRunWidget: View {
    var data: UIFirstRunRankList
    var onPlacementClicked: () -> (Void)
    
    var body: some View {
        Text(data.footerText.desc().localized())
            .minimumScaleFactor(0.5)
            .lineLimit(1)
        Button {
            withAnimation {
                onPlacementClicked()
            }
        } label: {
            Text(data.buttonText.desc().localized())
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color(red: 1, green: 0, blue: 0.44))
                .foregroundColor(.white)
                .clipShape(Capsule())
                .font(.system(size: 16, weight: .medium))
        }
    }
}

#Preview {
    RankListView()
}
