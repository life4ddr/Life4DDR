//
//  RankListSwiftUI.swift
//  iosApp
//
//  Created by Andrew Le on 2/27/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Shared

@available(iOS 15.0, *)
struct RankSelection: View {
    var newRankList: [LadderRank] = []
    var categories: Dictionary<LadderRankClass, [LadderRank]>
    var categoriesList: [LadderRankClass] = []
    @State var currentCategory: LadderRankClass = LadderRankClass.bronze
    
    init() {
        // Converting Kotlin arrays to Swift arrays (this is probably silly code)
        let ranks = LadderRank.values()
        for i in (0..<Int(ranks.size)) {
            newRankList.append(ranks.get(index: Int32(i))!)
        }
        let rankClasses = LadderRankClass.values()
        for i in (0..<Int(rankClasses.size)) {
            categoriesList.append(rankClasses.get(index: Int32(i))!)
        }
        
        // Mapping each rank to their category in a dictionary
        categories = Dictionary<LadderRankClass, [LadderRank]>(grouping: newRankList, by: { $0.group })
    }
    
    var body: some View {
        VStack(alignment: .center) {
            ScrollView(.horizontal, showsIndicators: false) {
                LazyHStack {
                    Spacer().frame(width: 16)
                    ForEach(categoriesList, id: \.self) { category in
                        Button {
                            withAnimation {
                                currentCategory = category
                            }
                        } label: {
                            RankImageWithTitle(rank: String(describing: category.toLadderRank()).lowercased(), text: String(describing: category), imageSize: 64, textSize: 20)
                        }.buttonStyle(.plain)
                        Spacer().frame(width: 16)
                    }
                }
            }.frame(height: 100)
            // TODO: adjust divider color on light/dark mode
            Divider().overlay(.white)
            // TODO: what is the null state of currentCategory?
            RankCategorySelector(categories: categories, categoriesList: categoriesList, currentCategory: $currentCategory)
        }
    }
}

@available(iOS 15.0, *)
struct RankCategorySelector: View {
    var compressed: Bool = true
    var categories: Dictionary<LadderRankClass, [LadderRank]>
    var categoriesList: [LadderRankClass] = []
    @Binding var currentCategory: LadderRankClass
    
    // TODO: animate this view when switching current category
    // TODO: figure out how to retrieve strings via StringResource
    var body: some View {
        if (compressed) {
            HStack {
                ForEach(categories[currentCategory]!, id: \.self) { rank in
                    RankImageWithTitle(rank: String(describing: rank).lowercased(), text: String(describing: rank.categoryNameRes.resourceId), imageSize: 48, textSize: 20).frame(maxWidth: .infinity)
                }
            }
        } else {
            VStack {
                HStack {
                    ForEach(0..<3) { i in
                        RankImageWithTitle(rank: String(describing: categories[currentCategory]![i]).lowercased(), text: String(describing: categories[currentCategory]![i]), imageSize: 84, textSize: 20).frame(maxWidth: .infinity)
                    }
                }
                Spacer().frame(height: 16)
                HStack {
                    ForEach(3..<5) { i in
                        RankImageWithTitle(rank: String(describing: categories[currentCategory]![i]).lowercased(), text: String(describing: categories[currentCategory]![i]), imageSize: 84, textSize: 20).frame(maxWidth: .infinity)
                    }
                }
            }
        }
    }
}

@available(iOS 15.0, *)
struct RankImageWithTitle: View {
    var rank: String
    var text: String
    var imageSize: Int
    var textSize: Int
    var body: some View {
        VStack {
            Image(rank)
                .resizable()
                .frame(width: CGFloat(imageSize), height: CGFloat(imageSize))
            Text(text)
                .font(.system(size: CGFloat(textSize), weight: .heavy))
        }
    }
}
