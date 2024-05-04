//
//  FirstRunConnector.swift
//  iosApp
//
//  Created by Corey Perrigo on 4/27/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Combine
import Shared
//import Kotlinx_coroutines_coreFlow

class FirstRunCollector: ObservableObject {
    @Published var state: FirstRunStep?
    private var cancellable: AnyCancellable?
    
    init(viewModel: FirstRunInfoViewModel) {
        cancellable = viewModel.state
            .receive(on: DispatchQueue.main)
            .collect { [weak self] step in
                DispatchQueue.main.async {
                    self?.state = step
                }
            }
    }
}

