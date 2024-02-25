//
//  FirstRunView.swift
//  iosApp
//
//  Created by Andrew Le on 2/24/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Shared

@available(iOS 15.0, *)
struct FirstRunView: View {
    // TEMPORARY - until I can get viewModel in
    @State var showWelcome = true
    @State var viewIndex = 0
    @State var isExistingUser = false
    
    var body: some View {
        ZStack(alignment: .center) {
            VStack(spacing: 75) {
                if (viewIndex > 0) {
                    Spacer()
                }
                FirstRunHeader(showWelcome: $showWelcome)
                if viewIndex == 0 {
                    FirstRunNewUser(showWelcome: $showWelcome, viewIndex: $viewIndex, isExistingUser: $isExistingUser)
                } else if viewIndex == 1 {
                    FirstRunUsername(username: "", isExistingUser: $isExistingUser)
                } else if viewIndex == 2 {
                    FirstRunRivalCode()
                }
                if (viewIndex > 0) {
                    Spacer()
                    HStack {
                        Button {
                            withAnimation {
                                if (viewIndex == 1) {
                                    showWelcome.toggle()
                                }
                                viewIndex -= 1
                            }
                        } label: {
                            Text("Back")
                                .frame(width: 44, height: 16)
                                .padding()
                                .background(Color(red: 1, green: 0, blue: 0.44))
                                .foregroundColor(.white)
                                .clipShape(Capsule())
                                .font(.system(size: 16, weight: .medium))
                        }
                        Spacer()
                        Button {
                            withAnimation {
                                viewIndex += 1
                            }
                        } label: {
                            Text("Next")
                                .frame(width: 44, height: 16)
                                .padding()
                                .background(Color(red: 1, green: 0, blue: 0.44))
                                .foregroundColor(.white)
                                .clipShape(Capsule())
                                .font(.system(size: 16, weight: .medium))
                        }
                    }
                }
            }
        }
        
    }
}

struct FirstRunHeader: View {
    // TEMPORARY - until I can get viewModel in
    @Binding var showWelcome: Bool
    
    var body: some View {
        VStack {
            if (showWelcome) {
                Text(MR.strings().first_run_landing_header.desc().localized())
                    .font(.system(size: 24, weight: .heavy))
                    .padding(.bottom, 8)
            }
            Image("LIFE4-Logo")
                .aspectRatio(contentMode: .fit)
                .frame(width: 300)
        }
    }
}

struct FirstRunNewUser: View {
    // TEMPORARY - until I can get viewModel in
    @Binding var showWelcome: Bool
    @Binding var viewIndex: Int
    @Binding var isExistingUser: Bool

    var body: some View {
        VStack {
            Text(MR.strings().first_run_landing_description.desc().localized())
                .font(.system(size: 24, weight: .heavy))
                .padding(.bottom, 16)
            HStack(alignment: .center) {
                Button {
                    withAnimation {
                        showWelcome.toggle()
                        viewIndex += 1
                        isExistingUser = false
                    }
                } label: {
                    Text(MR.strings().yes.desc().localized())
                        .frame(minWidth: 44)
                        .padding()
                        .background(Color(red: 1, green: 0, blue: 0.44))
                        .foregroundColor(.white)
                        .clipShape(Capsule())
                        .font(.system(size: 16, weight: .medium))
                }
                Button {
                    withAnimation {
                        showWelcome.toggle()
                        viewIndex += 1
                        isExistingUser = true
                    }
                } label: {
                    Text(MR.strings().no.desc().localized())
                        .frame(minWidth: 44)
                        .padding()
                        .background(Color(red: 1, green: 0, blue: 0.44))
                        .foregroundColor(.white)
                        .clipShape(Capsule())
                        .font(.system(size: 16, weight: .medium))
                }
            }
        }
    }
}

struct FirstRunUsername: View {
    // TEMPORARY - until I can get viewModel in
    @State var username: String
    @Binding var isExistingUser: Bool
    
    var body: some View {
        ZStack(alignment: .bottomTrailing) {
            VStack(alignment: .leading) {
                Text(isExistingUser ? MR.strings().first_run_username_existing_header.desc().localized() : MR.strings().first_run_username_new_header.desc().localized())
                    .font(.system(size: 24, weight: .heavy))
                    .padding(.bottom, 16)
                if (!isExistingUser) {
                    Text(MR.strings().first_run_username_description.desc().localized())
                        .fixedSize(horizontal: false, vertical: true)
                        .padding(.bottom, 16)
                }
                TextField(MR.strings().username.desc().localized(), text: $username)
                    .disableAutocorrection(true)
                    .padding(12)
                    .overlay(
                        RoundedRectangle(cornerRadius: 5)
                            .stroke(.white)
                    )
            }.padding(.horizontal, 15)
        }
    }
}

@available(iOS 15.0, *)
struct FirstRunRivalCode: View {
    var body: some View {
        VStack(alignment: .leading) {
            Text(MR.strings().first_run_rival_code_header.desc().localized())
                .font(.system(size: 24, weight: .heavy))
                .padding(.bottom, 16)
            Text(MR.strings().first_run_rival_code_description_1.desc().localized())
                .fixedSize(horizontal: false, vertical: true)
                .padding(.bottom, 8)
            Text(MR.strings().first_run_rival_code_description_2.desc().localized())
                .fixedSize(horizontal: false, vertical: true)
                .padding(.bottom, 32)
            RivalCodeEntry()
        }.padding(.horizontal, 15)
    }
}

@available(iOS 15.0, *)
struct RivalCodeEntry: View {
    @State var enteredCode = Array(repeating: "", count: 8)
    @FocusState var fieldFocus: Int?
    
    var body : some View {
        HStack {
            ForEach(0..<4, id: \.self) { index in
                RivalCodeCell(index: index, enteredCode: $enteredCode, focused: $fieldFocus)
            }
            Text("-").fontWeight(.bold)
            ForEach(4..<8, id: \.self) { index in
                RivalCodeCell(index: index, enteredCode: $enteredCode, focused: $fieldFocus)
            }
        }
    }
}

@available(iOS 15.0, *)
struct RivalCodeCell: View {
    var index: Int
    @Binding var enteredCode: [String]
    @FocusState.Binding var focused: Int?
    @State private var oldValue = ""
    
    var body: some View {
        TextField("", text: $enteredCode[index], onEditingChanged: { editing in
            if editing {
                oldValue = enteredCode[index]
            }
        })
            .keyboardType(.numberPad)
            .frame(width: 36, height: 48)
            .background(Color.gray.opacity(0.1))
            .cornerRadius(5)
            .overlay(
                RoundedRectangle(cornerRadius: 5)
                    .stroke(.white)
            )
            .multilineTextAlignment(.center)
            .font(.system(size: 24, weight: .bold))
            .focused($focused, equals: index)
            .tag(index)
            .onChange(of: enteredCode[index]) { newValue in
                if enteredCode[index].count > 1 {
                    let currentValue = Array(enteredCode[index])
                    enteredCode[index] = currentValue[0] == Character(oldValue) ? String(enteredCode[index].suffix(1)) : String(enteredCode[index].prefix(1))
                }
                
                if !newValue.isEmpty {
                    focused = index == 7 ? nil : (focused ?? 0) + 1
                } else {
                    focused = (focused ?? 0) - 1
                }
            }
    }
}

// Below are all of the SwiftUI previews

@available(iOS 15.0, *)
#Preview {
    FirstRunView()
}

struct FirstRunHeader_Previews: PreviewProvider {
    static var previews: some View {
        FirstRunHeader(showWelcome: .constant(true))
    }
}

struct FirstRunNewUser_Previews: PreviewProvider {
    static var previews: some View {
        FirstRunNewUser(showWelcome: .constant(true), viewIndex: .constant(0), isExistingUser: .constant(false))
    }
}

struct FirstRunUsernameNew_Previews: PreviewProvider {
    static var previews: some View {
        FirstRunUsername(username: "", isExistingUser: .constant(false))
    }
}

struct FirstRunUsernameExisting_Previews: PreviewProvider {
    static var previews: some View {
        FirstRunUsername(username: "", isExistingUser: .constant(true))
    }
}

@available(iOS 15.0, *)
struct FirstRunRivalCode_Previews: PreviewProvider {
    static var previews: some View {
        FirstRunRivalCode()
    }
}

@available(iOS 15.0, *)
struct RivalCodeEntry_Previews: PreviewProvider {
    static var previews: some View {
        RivalCodeEntry()
    }
}
