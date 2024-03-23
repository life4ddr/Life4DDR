//
//  LadderSwiftUI.swift
//  iosApp
//
//  Created by Andrew Le on 3/1/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Shared

class LadderGoal {
    var text: String
    var completed: Bool = false
    var hidden: Bool = false
    var progress: Float = 0.5
    
    init(text: String) {
        self.text = text
    }
}

@available(iOS 15.0, *)
struct LadderGoals: View {
    var rankPreview: Bool = false
    let goalStrings = ["14 Blue Lamp", "95 14 PFCs", "991k 14 Floor", "15 Red Lamp", "48 15 PFCs", "980k 15 Floor", "16 Clear Lamp", "14 16 PFCs", "955k 16 Floor", "17 Clear Lamp", "12 17 AAAs", "910k 17 Floor (4E)"]
    
    var body: some View {
        ScrollView {
            ForEach(goalStrings, id: \.self) { goal in
                LadderGoalItem(goal: LadderGoal(text: goal), rankPreview: rankPreview)
            }
        }
        
    }
}

@available(iOS 15.0, *)
struct LadderGoalItem: View {
    var goal: LadderGoal
    var rankPreview: Bool = false
    
    var body: some View {
        VStack {
            LadderGoalHeaderRow(goal: goal, rankPreview: rankPreview)
            if (!rankPreview && goal.progress > 0.0) {
                ProgressView(value: goal.progress).tint(.green)
            }
        }
        .background(rankPreview ? .gray : .white).cornerRadius(10.0)
    }
}

@available(iOS 15.0, *)
struct LadderGoalHeaderRow: View {
    var goal: LadderGoal
    var rankPreview: Bool = false
    @State var isSelected: Bool = false
    
    var body: some View {
        if (rankPreview) {
            HStack {
                Text(goal.text)
                Spacer()
            }.padding(12)
        } else {
            HStack {
                Text(goal.text).colorInvert()
                Spacer()
                Text("5/10").colorInvert().font(.system(size: 12, weight: .heavy))
                // completed logic goes here
                Toggle("", isOn: $isSelected)
                    .labelsHidden()
                    .toggleStyle(.checklist)
                Button {
                    withAnimation {
                        // ignore logic goes here
                    }
                } label: {
                    Image(systemName: "eye.fill").foregroundStyle(.black)
                }
            }.padding(EdgeInsets(top: 12, leading: 12, bottom: rankPreview ? 12 : 0, trailing: 12))
        }
        
    }
}

@available(iOS 15.0, *)
#Preview {
    LadderGoals()
}

@available(iOS 15.0, *)
struct ToggleCheckboxStyle: ToggleStyle {
    func makeBody(configuration: Configuration) -> some View {
        Button {
            configuration.isOn.toggle()
        } label: {
            Image(systemName: configuration.isOn ? "checkmark.square.fill" : "square").foregroundStyle(.green)
        }
    }
}

@available(iOS 15.0, *)
extension ToggleStyle where Self == ToggleCheckboxStyle {
    static var checklist: ToggleCheckboxStyle { .init() }
}
