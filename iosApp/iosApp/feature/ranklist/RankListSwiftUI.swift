//
//  RankListSwiftUI.swift
//  iosApp
//
//  Created by Andrew Le on 2/27/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct RankSelection: View {    
    @Environment(\.colorScheme) var colorScheme
    var noRank: UINoRank
    var onRankClicked: (LadderRank?) -> (Void)
    var categories: Dictionary<LadderRankClass?, [LadderRank?]>
    var categoriesList: [LadderRankClass?] = []
    @State var selectedCategory: LadderRankClass?
    @State var showSelectorPanel: Bool = false
    @State var compressSelectorPanel: Bool = false
        
    init(ranks: [LadderRank?] = LadderRank.entries, noRank: UINoRank = UINoRank.Companion().DEFAULT, onRankClicked: @escaping (LadderRank?) -> (Void)) {
        self.noRank = noRank
        self.onRankClicked = onRankClicked
        categories = Dictionary(grouping: ranks, by: { $0?.group })
        // Must append from LadderRankClass values; can't do categories keys since it's unsorted
        // Question: are there cases where "No Rank" will not show up?
        categoriesList.append(nil)
        let rankClasses = LadderRankClass.values()
        for i in (0..<Int(rankClasses.size)) {
            let rankClass = rankClasses.get(index: Int32(i))!
            categoriesList.append(rankClass)
        }
    }
    
    var body: some View {
        VStack {
            ScrollView(.horizontal, showsIndicators: false) {
                LazyHStack {
                    ForEach(categoriesList, id: \.self) { category in
                        Button {
                            withAnimation {
                                selectedCategory = category
                                showSelectorPanel = true
                                compressSelectorPanel = false
                            }
                        } label: {
                            RankImageWithTitle(
                                rank: category?.toLadderRank(),
                                text: category?.nameRes.desc().localized() ?? MR.strings().no_rank.desc().localized(),
                                imageSize: 64,
                                textSize: 20
                            )
                        }
                        .padding(.horizontal, 8)
                        .buttonStyle(.plain)
                    }
                }
            }.frame(height: 100)
            Divider().overlay(colorScheme == .dark ? .white : .black)
            // TODO: add the proper animations/transitions when showing ranks
            if (showSelectorPanel) {
                let availableRanks = categories[selectedCategory]
                if (availableRanks!.count < 5) {
                    NoRankDetails(noRank: noRank)
                } else {
                    RankCategorySelector(
                        availableRanks: availableRanks!,
                        onRankClicked: onRankClicked,
                        compressed: $compressSelectorPanel
                    )
                }
            }
        }
    }
}

struct NoRankDetails: View {
    var noRank: UINoRank
    
    var body: some View {
        VStack {
            Text(noRank.bodyText.desc().localized())
            Spacer().frame(height: 16)
            Button {
                withAnimation {
                    // TODO: onRankRejected
                }
            } label: {
                Text(noRank.buttonText.desc().localized())
                    .padding()
                    .background(Color(red: 1, green: 0, blue: 0.44))
                    .foregroundColor(.white)
                    .clipShape(Capsule())
                    .font(.system(size: 16, weight: .medium))
            }
        }.padding(EdgeInsets(top: 16, leading: 24, bottom: 16, trailing: 24))
    }
}

struct RankCategorySelector: View {
    var availableRanks: [LadderRank?]
    var onRankClicked: (LadderRank?) -> (Void)
    @Binding var compressed: Bool
    
    var body: some View {
        if compressed {
            HStack {
                ForEach(availableRanks, id: \.self) { rank in
                    Button {
                        withAnimation {
                            onRankClicked(rank)
                        }
                    } label: {
                        RankImageWithTitle(rank: rank, text: rank!.categoryNameRes.desc().localized(), imageSize: 48)
                    }.buttonStyle(.plain)
                }
            }
        } else {
            VStack {
                HStack {
                    ForEach(availableRanks[0..<3], id: \.self) { rank in
                        Button {
                            withAnimation {
                                compressed = true
                                onRankClicked(rank)
                            }
                        } label: {
                            RankImageWithTitle(rank: rank, text: rank!.nameRes.desc().localized(), imageSize: 84)
                        }.buttonStyle(.plain)
                    }
                }
                Spacer().frame(height: 16)
                HStack {
                    ForEach(availableRanks[3..<5], id: \.self) { rank in
                        Button {
                            withAnimation {
                                compressed = true
                                onRankClicked(rank)
                            }
                        } label: {
                            RankImageWithTitle(rank: rank, text: rank!.nameRes.desc().localized(), imageSize: 84)
                        }.buttonStyle(.plain)
                    }
                }
            }
        }
    }
}

struct RankImageWithTitle: View {
    var rank: LadderRank?
    var text: String
    var imageSize: Int
    var textSize: Int = 20
    
    var body: some View {
        VStack {
            Image(rank != nil ? String(describing: rank!).lowercased() : "copper1")
                .resizable()
                .frame(width: CGFloat(imageSize), height: CGFloat(imageSize))
                .saturation(rank != nil ? 1.0 : 0.0)
            Text(text)
                .font(.system(size: CGFloat(textSize), weight: .heavy))
                .frame(width: CGFloat(imageSize) + 20.0)
                .minimumScaleFactor(0.5)
                .lineLimit(1)
        }.frame(maxWidth: .infinity)
    }
}
