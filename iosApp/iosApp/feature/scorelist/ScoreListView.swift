//
//  ScoreListView.swift
//  iosApp
//
//  Created by Andrew Le on 6/25/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct ScoreListView: View {
    @ObservedObject var viewModel: ScoreListViewModel = ScoreListViewModel()
    @State var state: UIScoreList?
    @State var filterShowing: Bool = false
    
    var body: some View {
        VStack {
            Button {
                withAnimation {
                    filterShowing = !filterShowing
                }
            } label: {
                Text("Filter")
                    .frame(width: 44, height: 16)
                    .padding()
                    .background(Color(red: 1, green: 0, blue: 0.44))
                    .foregroundColor(.white)
                    .clipShape(Capsule())
                    .font(.system(size: 16, weight: .medium))
            }.frame(maxWidth: .infinity, alignment: .trailing)
            if (filterShowing) {
                FilterPane(
                    data: state!.filter,
                    onAction: { action in
                       viewModel.handleFilterAction(action: action)
                   }
                )
            }
            List {
                ForEach(state?.scores ?? [], id: \.self) { score in
                    ScoreEntry(data: score)
                }
            }.listStyle(.plain)
        }
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

struct ScoreEntry: View {
    var data: UIScore
    
    var body: some View {
        HStack {
            VStack {
                Text(data.titleText)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .lineLimit(1)
                HStack {
                    Text(data.difficultyText.localized()).foregroundColor(Color(data.difficultyColor.getUIColor()))
                    Spacer()
                    Text(data.scoreText.localized()).foregroundColor(Color(data.scoreColor.getUIColor()))
                }
            }
            if (data.flareLevel != nil) {
                AsyncImage(url: URL(string: "flare_\(Int(data.flareLevel!) < 10 ? String(Int(data.flareLevel!)) : "ex")")) { image in
                    image.image?.resizable()
                }
                .frame(width: 32, height: 32)
            }
        }.padding(4)
    }
}

#Preview {
    ScoreListView()
}
