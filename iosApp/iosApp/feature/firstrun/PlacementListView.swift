//
//  PlacementListView.swift
//  iosApp
//
//  Created by Andrew Le on 6/15/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Shared

struct PlacementListView: View {
    @Environment(\.colorScheme) var colorScheme
    @ObservedObject var viewModel: PlacementListViewModel = PlacementListViewModel()
    @State var data: UIPlacementListScreen?
    var onRanksClicked: () -> (Void)
    var goToMainView: () -> (Void)
    // TODO: implement onPlacementSelected, which will require PlacementDetailsView
    
    @State var selectedPlacement: String?
    @State var closeConfirmShown: Bool = false
    
    var body: some View {
        VStack {
            Text(data?.titleText.localized() ?? "")
                .frame(maxWidth: .infinity, alignment: .leading)
                .font(.system(size: 32, weight: .heavy))
            ScrollView(showsIndicators: false) {
                LazyVStack {
                    Text(data?.headerText.localized() ?? "")
                        .fixedSize(horizontal: false, vertical: true)
                        .padding(.bottom, 16)
                    ForEach(data?.placements ?? [], id: \.self) { placement in
                        PlacementItem(
                            data: placement,
                            expanded: selectedPlacement == placement.id,
                            onExpand: {
                                if (selectedPlacement == placement.id) {
                                    selectedPlacement = nil
                                } else {
                                    selectedPlacement = placement.id
                                }
                            }
                        )
                    }
                }
            }
            Button {
                withAnimation {
                    viewModel.setFirstRunState(state: InitState.ranks)
                    onRanksClicked()
                }
            } label: {
                Text(MR.strings().select_rank_instead.desc().localized())
                    .padding()
                    .background(Color(red: 1, green: 0, blue: 0.44))
                    .foregroundColor(.white)
                    .clipShape(Capsule())
                    .font(.system(size: 16, weight: .medium))
            }
            Button {
                withAnimation {
                    closeConfirmShown = true
                }
            } label: {
                Text(MR.strings().start_no_rank.desc().localized())
                    .padding()
                    .foregroundColor(colorScheme == .dark ? .white : .black)
                    .font(.system(size: 16, weight: .medium))
            }.alert(isPresented: $closeConfirmShown) {
                Alert(
                    title: Text(MR.strings().placement_close_confirm_title.desc().localized()),
                    message: Text(MR.strings().placement_close_confirm_body.desc().localized()),
                    primaryButton: .destructive(Text("Confirm")) {
                        viewModel.setFirstRunState(state: InitState.done)
                        goToMainView()
                    },
                    secondaryButton: .cancel()
                )
            }
        }
        .padding(16)
        .navigationBarBackButtonHidden(true)
        .onAppear {
            viewModel.screenData.subscribe { data in
                if let currentData = data {
                    withAnimation {
                        self.data = currentData
                    }
                }
            }
        }
    }
}

struct PlacementItem: View {
    var data: UIPlacement
    var expanded: Bool = false
    var onExpand: () -> (Void)
    
    var body: some View {
        let rankString = data.placementName.desc().localized()
        VStack {
            HStack {
                Image(String(describing: data.rankIcon).lowercased())
                    .resizable()
                    .frame(width: 64.0, height: 64.0)
                    .padding(.trailing, 8)
                Text(rankString)
                    .font(.system(size: 28, weight: .bold))
                    .foregroundColor(Color(data.color.getUIColor()))
                Spacer()
                Text(data.difficultyRangeString)
                    .font(.system(size: 14, weight: .bold))
                Image(systemName: "chevron.down")
                    .rotationEffect(.degrees(expanded ? 180.0 : 0.0))
            }
            if (expanded) {
                VStack {
                    ForEach(data.songs, id: \.self) { song in
                        PlacementSongItem(data: song, textColor: "\(rankString.lowercased())ContainerText")
                    }
                    Button {
                        withAnimation {
                            
                        }
                    } label: {
                        Text(MR.strings().placement_start.desc().localized())
                            .padding()
                            .foregroundColor(Color("\(rankString.lowercased())ContainerText"))
                            .font(.system(size: 22, weight: .bold))
                    }
                }
            }
        }
        .padding(EdgeInsets(top: 12, leading: 16, bottom: 12, trailing: 16))
        .background(Color("\(rankString.lowercased())Container"))
        .clipShape(RoundedRectangle(cornerRadius: 16.0))
        .onTapGesture {
            withAnimation {
                onExpand()
            }
        }
    }
}

struct PlacementSongItem: View {
    var data: UITrialSong
    var textColor: String
    
    var body: some View {
        HStack {
            AsyncImage(url: URL(string: data.jacketUrl!)) { image in
                image.image?.resizable()
            }
            .frame(width: 64, height: 64)

            VStack {
                Text(data.songNameText)
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor(Color(textColor))
                    .minimumScaleFactor(0.5)
                    .lineLimit(1)
//                Text(data.subtitleText)
//                    .font(.system(size: 14, weight: .bold))
//                    .foregroundColor(Color(textColor))
//                    .minimumScaleFactor(0.5)
//                    .lineLimit(1)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            
            VStack {
                Text(data.chartString)
                    .font(.system(size: 14, weight: .bold))
                    .foregroundColor(Color(data.difficultyClass.colorRes.getUIColor()))
                Text(data.difficultyText)
                    .font(.system(size: 16, weight: .bold))
                    .foregroundColor(Color(data.difficultyClass.colorRes.getUIColor()))
            }
            .padding(8)
            .background(Color(red: 0.1, green: 0.1, blue: 0.1))
            .clipShape(RoundedRectangle(cornerRadius: 8.0))
        }
    }
}

#Preview {
    PlacementListView(onRanksClicked: {}, goToMainView: {})
}
