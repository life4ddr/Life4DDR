//
//  LadderSwiftUI.swift
//  iosApp
//
//  Created by Andrew Le on 3/1/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct LadderGoals: View {
    var data: UILadderData?
    var onInput: (RankListInput) -> (Void)
    
    var body: some View {
        ScrollView(showsIndicators: false) {
            if data?.goals is UILadderGoals.SingleList {
                SingleGoalList(
                    goals: data!.goals as! UILadderGoals.SingleList,
                    onInput: onInput
                )
            } else if data?.goals is UILadderGoals.CategorizedList {
                CategorizedList(
                    goals: data!.goals as! UILadderGoals.CategorizedList,
                    onInput: onInput
                )
            }
        }
    }
}

struct SingleGoalList: View {
    var goals: UILadderGoals.SingleList
    var onInput: (RankListInput) -> (Void)
    
    var body: some View {
        ForEach(goals.items, id: \.self) { goal in
            LadderGoalItem(
                goal: goal,
                onInput: onInput
            )
        }
    }
}

struct CategorizedList: View {
    var goals: UILadderGoals.CategorizedList
    var onInput: (RankListInput) -> (Void)
    
    var body: some View {
        ForEach(goals.categories, id: \.self) { category in
            HStack {
                Text((category.first?.title.localized())!)
                Spacer()
                if (category.first?.goalText != nil) {
                    Text((category.first?.goalText?.localized())!)
                }
            }
            ForEach((category.second as? [UILadderGoal])!, id: \.self) { goal in
                LadderGoalItem(
                    goal: goal,
                    expanded: !goal.detailItems.isEmpty,
                    onInput: onInput
                )
            }
            Spacer().frame(height: 16)
        }
    }
}

struct LadderGoalItem: View {
    var goal: UILadderGoal
    var expanded: Bool = false
    var onInput: (RankListInput) -> (Void)
    
    var body: some View {
        VStack {
            LadderGoalHeaderRow(
                goal: goal,
                onInput: onInput
            )
            if expanded {
                Divider().overlay(Color.primary)
                LadderGoalDetailShade(items: goal.detailItems)
            }
            if goal.progress != nil {
                ProgressView(value: goal.progress?.progressPercent)
                    .frame(maxWidth: .infinity)
                    .accentColor(.green)
                    .scaleEffect(x: 1, y: 4, anchor: .center)
            }
        }
        .background(Color("goalBackground"))
        .opacity(goal.hidden ? 0.5 : 1.0)
        .cornerRadius(10.0)
    }
}

struct LadderGoalHeaderRow: View {
    var goal: UILadderGoal
    var onInput: (RankListInput) -> (Void)
    
    var body: some View {
        HStack {
            Text(goal.goalText.localized())
            Spacer()
            if goal.progress != nil {
                Text(goal.progress!.progressText)
            }
            if goal.showCheckbox {
                Button {
                    withAnimation {
                        onInput(goal.completeAction!)
                    }
                } label: {
                    Image(systemName: goal.completed ? "checkmark.square.fill" : "square")
                }
                .buttonStyle(.plain)
                .disabled(goal.completeAction == nil)
            }
            if (goal.hideAction != nil) {
                Button {
                    withAnimation {
                        onInput(goal.hideAction!)
                    }
                } label: {
                    Image(systemName: "eye.fill")
                }.buttonStyle(.plain)
            }
        }
        .padding(12)
        .onTapGesture {
            if (goal.expandAction != nil) {
                withAnimation {
                    onInput(goal.expandAction!)
                }
            }
        }
    }
}

struct LadderGoalDetailShade: View {
    var items: [UILadderDetailItem]
    
    var body: some View {
        LazyVStack(spacing: 4) {
            ForEach(items, id: \.self) { item in
                HStack {
                    Text(item.leftText)
                        .foregroundColor(Color(item.leftColor?.getUIColor() ?? .label))
                    if item.rightText != nil {
                        Spacer()
                        Text(item.rightText!)
                            .foregroundColor(Color(item.rightColor?.getUIColor() ?? .label))
                    }
                }.frame(maxWidth: .infinity)
            }
        }.padding(12)
    }
}

func createSongDetailItem(songName: String, difficultyClass: DifficultyClass? = nil) -> UILadderDetailItem {
    let score: Int = 1000000 - Int.random(in: 100...50000)
    return UILadderDetailItem(leftText: songName, leftColor: difficultyClass?.colorRes, leftWeight: 0.8, rightText: String(score), rightColor: nil, rightWeight: 0.2)
}

func createUILadderGoal(goalText: String, completed: Bool = false, hidden: Bool = false, progress: UILadderProgress? = nil, detailItems: [UILadderDetailItem] = []) -> UILadderGoal {
    return UILadderGoal(id: 0, goalText: RawStringDesc(string: goalText), completed: completed, canComplete: hidden, showCheckbox: true, hidden: true, canHide: true, progress: progress, expandAction: nil, detailItems: detailItems)
}

let detailItems = [
    createSongDetailItem(songName: "L'amour et la libert&eacute;(DDR Ver.)", difficultyClass: DifficultyClass.beginner),
    createSongDetailItem(songName: "LOVE&hearts;SHINE", difficultyClass: DifficultyClass.basic),
    createSongDetailItem(songName: "Miracle Moon ～L.E.D.LIGHT STYLE MIX～", difficultyClass: DifficultyClass.difficult),
    createSongDetailItem(songName: "PARANOIA survivor", difficultyClass: DifficultyClass.expert),
    createSongDetailItem(songName: "PARANOIA survivor MAX", difficultyClass: DifficultyClass.challenge),
    createSongDetailItem(songName: "Pink Rose", difficultyClass: DifficultyClass.beginner),
    createSongDetailItem(songName: "SO IN LOVE", difficultyClass: DifficultyClass.basic),
    createSongDetailItem(songName: "STAY (Organic house Version)", difficultyClass: DifficultyClass.difficult),
    createSongDetailItem(songName: "stoic (EXTREME version)", difficultyClass: DifficultyClass.expert),
    createSongDetailItem(songName: "sync (EXTREME version)", difficultyClass: DifficultyClass.challenge),
    createSongDetailItem(songName: "TEARS")
]

struct PreviewGoalItem: View {
    var goal: UILadderGoal
    
    var body: some View {
        LadderGoalItem(goal: goal, expanded: true, onInput: {_ in })
    }
}

struct LadderGoalItemDetail_Previews: PreviewProvider {
    static var previews: some View {
        PreviewGoalItem(goal: createUILadderGoal(goalText: "Clear any 10 L5's.", progress: UILadderProgress(count: 7, max: 10, showMax: true), detailItems: detailItems))
    }
}
