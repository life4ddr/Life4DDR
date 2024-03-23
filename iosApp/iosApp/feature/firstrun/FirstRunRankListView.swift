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
    @State var selectedRank: LadderRank?
    
    var body: some View {
        VStack(alignment: .leading) {
            Text(MR.strings().select_a_starting_rank.desc().localized())
                .font(.system(size: 32, weight: .heavy))
                .padding(.bottom, 16)
            RankSelection(selectedRank: $selectedRank)
            if (selectedRank != nil) {
                LadderGoals(rankPreview: true)
            }
            Spacer()
        }
    }
}

@available(iOS 16.0, *)
#Preview {
    FirstRunRankListView()
}
