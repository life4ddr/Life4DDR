//
//  SettingsView.swift
//  iosApp
//
//  Created by Andrew Le on 6/25/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct SettingsView: View {
    @ObservedObject var viewModel: SettingsViewModel = SettingsViewModel(onClose: {}, onNavigateToCredits: {})
    @State var state: UISettingsData?
    
    var body: some View {
        VStack {
            SettingsViewContent(
                items: state?.settingsItems,
                onAction: { action in
                    viewModel.handleAction(action: action)
                }
            )
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

struct SettingsViewContent: View {
    var items: [UISettingsItem]?
    var onAction: (SettingsAction) -> (Void)
    
    var body: some View {
        List {
            ForEach(items ?? [], id: \.self) { item in
                if var link = item as? UISettingsItem.Link {
                    Button {
                        withAnimation {
                            onAction(link.action)
                        }
                    } label: {
                        Text(link.title.localized())
                    }.buttonStyle(.plain)
                } else if var checkbox = item as? UISettingsItem.Checkbox {
                    HStack {
                        Text(checkbox.title.localized())
                        Spacer()
                        Toggle("", isOn: Binding(
                            get: { checkbox.toggled },
                            set: { _ in
                                onAction(checkbox.action)
                            }
                        ))
                        .labelsHidden()
                    }
                }
            }
        }
    }
}

#Preview {
    SettingsView()
}
