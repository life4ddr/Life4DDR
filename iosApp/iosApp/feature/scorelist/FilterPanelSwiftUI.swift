//
//  FilterPanelSwiftUI.swift
//  iosApp
//
//  Created by Andrew Le on 1/28/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct FilterPane: View {
    var data: UIFilterView
    var onAction: (UIFilterAction) -> (Void)
    @State var selectedDifficulty: PlayStyle = .single
    
    var body: some View {
        VStack {
            if (data.playStyleSelector != nil) {                
                ControlGroup {
                    ForEach(data.playStyleSelector!, id: \.self) { item in
                        Button {
                            withAnimation {
                                onAction(item.action)
                            }
                        } label: {
                            Text(item.text.localized())
                        }
                    }
                }
            }
            
            HStack {
                ForEach(data.difficultyClassSelector, id: \.self) { item in
                    VStack {
                        Toggle("", isOn: Binding(
                            get: { item.selected },
                            set: { _ in
                                onAction(item.action)
                            }
                        ))
                        Text(item.text.localized())
                    }
                }
            }
            
            Spacer().frame(height: 8)
//            Text(data.difficultyNumberTitle.localized()).frame(maxWidth: .infinity, alignment: .leading)
            
        }.padding(.horizontal, 16)
    }
}

//#Preview {
//    FilterPanelSwiftUI()
//}
