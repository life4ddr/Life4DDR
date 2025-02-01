//
//  BannerSwiftUI.swift
//  iosApp
//
//  Created by Andrew Le on 1/29/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct BannerContainer: View {
    var data: UIBanner?
    
    var body: some View {
        if (data != nil) {
            var text = data!.text.localized()
            Text(text)
        }
    }
}

#Preview {
    BannerContainer()
}
