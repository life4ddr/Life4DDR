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
        // TODO: account for light/dark mode on banner color
        let textColor = data?.textColor as? ColorDescResource
        let backgroundColor = data?.backgroundColor as? ColorDescResource
        if (data != nil) {
            Text(data!.text.localized())
                .foregroundColor(Color(textColor?.resource.getUIColor() ?? .white))
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(EdgeInsets(top: 8, leading: 16, bottom: 8, trailing: 16))
                .background(Color(backgroundColor?.resource.getUIColor() ?? .gray))
        }
    }
}
