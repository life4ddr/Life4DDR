//
//  FirstRunRankListView.swift
//  iosApp
//
//  Created by Andrew Le on 2/26/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Shared

@available(iOS 16.0, *)
struct FirstRunRankListView: View {
    @ObservedObject var viewModel: RankListViewModel = RankListViewModel(isFirstRun: true)
    @State var state: UIRankList?
    var onPlacementClicked: () -> (Void)
    // TODO: implement goToMainScreen and onRankSelected
    
    var body: some View {
        VStack {
            Text(state?.titleText.desc().localized() ?? "")
                .frame(maxWidth: .infinity, alignment: .leading)
                .font(.system(size: 32, weight: .heavy))
                .padding(.bottom, 16)
            RankSelection(
                ranks: state?.ranks as? [LadderRank?] ?? [],
                noRank: state?.noRank ?? UINoRank.Companion().DEFAULT,
                onRankClicked: viewModel.setRankSelected
                // TODO: after onRankSelected is implemented, add onRankRejected here
            )
            // TODO: add LadderGoals here when ladderData gets set in state
            Spacer()
            if (state?.firstRun != nil) {
                Text(state?.footerText.desc().localized() ?? "")
                    .minimumScaleFactor(0.5)
                    .lineLimit(1)
                Button {
                    withAnimation {
                        viewModel.moveToPlacements()
                        onPlacementClicked()
                    }
                } label: {
                    Text(state?.firstRun?.buttonText.desc().localized() ?? "")
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color(red: 1, green: 0, blue: 0.44))
                        .foregroundColor(.white)
                        .clipShape(Capsule())
                        .font(.system(size: 16, weight: .medium))
                }
            }
        }
        .navigationBarBackButtonHidden(true)
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

@available(iOS 16.0, *)
#Preview {
    FirstRunRankListView(onPlacementClicked: {})
}
