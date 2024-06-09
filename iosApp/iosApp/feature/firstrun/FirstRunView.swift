//
//  FirstRunView.swift
//  iosApp
//
//  Created by Andrew Le on 2/24/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Shared

@available(iOS 16.0, *)
struct FirstRunView: View {
    @ObservedObject var viewModel: FirstRunInfoViewModel = FirstRunInfoViewModel()
    @State var step: FirstRunStep = FirstRunStep.Landing()
    // TODO: add onComplete functionality
        
    var body: some View {
        ZStack(alignment: .center) {
            VStack(spacing: 75) {
                if (step != FirstRunStep.Landing()) {
                    Spacer()
                }
                
                FirstRunHeader(showWelcome: step == FirstRunStep.Landing())
                
                switch step {
                    case is FirstRunStep.Landing:
                        FirstRunNewUser(viewModel: viewModel)
                    case is FirstRunStep.PathStepUsername:
                        FirstRunUsername(viewModel: viewModel, step: step as! FirstRunStep.PathStepUsername)
                    case is FirstRunStep.PathStepRivalCode:
                        FirstRunRivalCode(viewModel: viewModel)
                    case is FirstRunStep.PathStepSocialHandles:
                        FirstRunSocials()
                    case is FirstRunStep.PathStepInitialRankSelection:
                        FirstRunRankMethod()
                    default:
                        Text("No step here")
                }
                
                if (step != FirstRunStep.Landing()) {
                    Spacer()
                    HStack {
                        Button {
                            withAnimation {
                                _ = viewModel.navigateBack()
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
                        if (step.showNextButton) {
                            Button {
                                withAnimation {
                                    viewModel.navigateNext()
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
            }.onAppear {
                viewModel.state.subscribe { state in
                    if let currentStep = state {
                        withAnimation {
                            step = currentStep
                        }
                    }
                }
            }
        }
    }
}

struct FirstRunHeader: View {
    var showWelcome: Bool
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        VStack {
            if (showWelcome) {
                Text(MR.strings().first_run_landing_header.desc().localized())
                    .font(.system(size: 24, weight: .heavy))
                    .padding(.bottom, 8)
            }
            if (colorScheme == .dark) {
                Image("LIFE4-Logo")
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 300)
            } else {
                Image("LIFE4-Logo")
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 300)
                    .colorInvert()
            }
        }
    }
}

struct FirstRunNewUser: View {
    @ObservedObject var viewModel: FirstRunInfoViewModel

    var body: some View {
        VStack {
            Text(MR.strings().first_run_landing_description.desc().localized())
                .font(.system(size: 24, weight: .heavy))
                .padding(.bottom, 16)
            HStack(alignment: .center) {
                Button {
                    withAnimation {
                        viewModel.doNewUserSelected(isNewUser: true)
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
                        viewModel.doNewUserSelected(isNewUser: false)
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
    @ObservedObject var viewModel: FirstRunInfoViewModel
    var step: FirstRunStep.PathStepUsername
    @State var error: FirstRunError.UsernameError?
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        VStack(alignment: .leading) {
            Text(step.headerText.localized())
                .font(.system(size: 24, weight: .heavy))
                .padding(.bottom, 16)
            if (step.descriptionText != nil) {
                Text(step.descriptionText!.localized())
                    .fixedSize(horizontal: false, vertical: true)
                    .padding(.bottom, 16)
            }
            TextField(MR.strings().username.desc().localized(), text: viewModel.binding(\.username))
                .disableAutocorrection(true)
                .padding(12)
                .overlay(
                    RoundedRectangle(cornerRadius: 5)
                        .stroke(colorScheme == .dark ? .white : .black)
                )
            if (error != nil) {
                Text(error!.errorText.localized())
                    .foregroundColor(.red)
            }
        }
        .padding(.horizontal, 15)
        .onAppear {
            viewModel.errors.subscribe { errorsState in
                if let currentErrors = errorsState {
                    if (currentErrors.count > 0) {
                        withAnimation {
                            error = currentErrors[0] as? FirstRunError.UsernameError
                        }
                    }
                }
            }
        }
    }
}

@available(iOS 15.0, *)
struct FirstRunRivalCode: View {
    @ObservedObject var viewModel: FirstRunInfoViewModel
    @State var error: FirstRunError.RivalCodeError?

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
            RivalCodeEntry(viewModel: viewModel)
            if (error != nil) {
                Text(error!.errorText.localized())
                    .foregroundColor(.red)
            }
        }
        .padding(.horizontal, 15)
        .onAppear {
            viewModel.errors.subscribe { errorsState in
                if let currentErrors = errorsState {
                    if (currentErrors.count > 0) {
                        withAnimation {
                            error = currentErrors[0] as? FirstRunError.RivalCodeError
                        }
                    }
                }
            }
        }
    }
}

@available(iOS 15.0, *)
struct RivalCodeEntry: View {
    @ObservedObject var viewModel: FirstRunInfoViewModel
    @FocusState private var isKeyboardShowing: Bool
    
    var body : some View {
        HStack {
            let currentRivalCode = String(viewModel.rivalCode.value!)
            let rivalCodeArray = Array(currentRivalCode)
            ForEach(0..<4, id: \.self) { index in
                RivalCodeCell(cellValue: rivalCodeArray.count > index ? String(rivalCodeArray[index]) : " ")
            }
            Text("-").fontWeight(.bold)
            ForEach(4..<8, id: \.self) { index in
                RivalCodeCell(cellValue: rivalCodeArray.count > index ? String(rivalCodeArray[index]) : " ")
            }
        }
        .background(content: {
            TextField("", text: viewModel.binding(\.rivalCode).limit(8))
                .keyboardType(.numberPad)
                .frame(width: 1, height: 1)
                .opacity(0.001)
                .blendMode(.screen)
                .focused($isKeyboardShowing)
                .onChange(of: viewModel.rivalCode.value!) { newCode in
                    if String(newCode).count == 8 {
                        isKeyboardShowing = false
                    }
                }
        })
        .onTapGesture {
            isKeyboardShowing.toggle()
        }
    }
}

@available(iOS 15.0, *)
struct RivalCodeCell: View {
    var cellValue: String
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        Text(cellValue)
            .font(.system(size: 24, weight: .bold))
            .frame(width: 36, height: 48)
            .background(Color.gray.opacity(0.1))
            .cornerRadius(5)
            .overlay(
                RoundedRectangle(cornerRadius: 5)
                    .stroke(colorScheme == .dark ? .white : .black)
        )
    }
}

struct FirstRunSocials: View {
    // TODO: update this View when it's functional on Android
    var body: some View {
        VStack(alignment: .leading) {
            Text(MR.strings().first_run_social_header.desc().localized())
                .font(.system(size: 24, weight: .heavy))
                .padding(.bottom, 16)
            Text(MR.strings().first_run_social_description.desc().localized())
                .fixedSize(horizontal: false, vertical: true)
                .padding(.bottom, 16)
            Button {
                withAnimation {
                    
                }
            } label: {
                Text(MR.strings().first_run_social_add_new.desc().localized())
                    .padding()
                    .background(Color(red: 0, green: 0.41, blue: 0.56))
                    .foregroundColor(.white)
                    .clipShape(Capsule())
                    .font(.system(size: 16, weight: .medium))
            }
        }.padding(.horizontal, 15)
    }
}

@available(iOS 16.0, *)
struct FirstRunRankMethod: View {
    var body: some View {
        VStack {
            Text(MR.strings().first_run_rank_selection_header.desc().localized())
                .font(.system(size: 24, weight: .heavy))
                .padding(.bottom, 16)
            Button {
                withAnimation {
                    
                }
            } label: {
                Text(MR.strings().first_run_rank_method_no_rank.desc().localized())
                    .padding()
                    .background(Color(red: 1, green: 0, blue: 0.44))
                    .foregroundColor(.white)
                    .clipShape(Capsule())
                    .font(.system(size: 16, weight: .medium))
            }
            Button {
                withAnimation {
                    
                }
            } label: {
                Text(MR.strings().first_run_rank_method_placement.desc().localized())
                    .padding()
                    .background(Color(red: 1, green: 0, blue: 0.44))
                    .foregroundColor(.white)
                    .clipShape(Capsule())
                    .font(.system(size: 16, weight: .medium))
            }
            NavigationLink(destination: FirstRunRankListView()) {
                Text(MR.strings().first_run_rank_method_selection.desc().localized())
                    .padding()
                    .background(Color(red: 1, green: 0, blue: 0.44))
                    .foregroundColor(.white)
                    .clipShape(Capsule())
                    .font(.system(size: 16, weight: .medium))
            }
            Text(MR.strings().first_run_rank_selection_footer.desc().localized())
                .fixedSize(horizontal: false, vertical: true)
                .padding(.top, 16)
        }.padding(.horizontal, 15)
    }
}

// Below are all of the SwiftUI previews

@available(iOS 16.0, *)
#Preview {
    FirstRunView()
}

struct FirstRunHeader_Previews: PreviewProvider {
    static var previews: some View {
        FirstRunHeader(showWelcome: true)
    }
}

struct FirstRunNewUser_Previews: PreviewProvider {
    static var previews: some View {
        @ObservedObject var viewModel: FirstRunInfoViewModel = FirstRunInfoViewModel()
        FirstRunNewUser(viewModel: viewModel)
    }
}

struct FirstRunUsernameNew_Previews: PreviewProvider {
    static var previews: some View {
        @ObservedObject var viewModel: FirstRunInfoViewModel = FirstRunInfoViewModel()
        FirstRunUsername(viewModel: viewModel, step: FirstRunStep.PathStepUsername(path: FirstRunPath.theNewUserLocal))
    }
}

struct FirstRunUsernameExisting_Previews: PreviewProvider {
    static var previews: some View {
        @ObservedObject var viewModel: FirstRunInfoViewModel = FirstRunInfoViewModel()
        FirstRunUsername(viewModel: viewModel, step: FirstRunStep.PathStepUsername(path: FirstRunPath.existingUserLocal))
    }
}

@available(iOS 15.0, *)
struct FirstRunRivalCode_Previews: PreviewProvider {
    static var previews: some View {
        @ObservedObject var viewModel: FirstRunInfoViewModel = FirstRunInfoViewModel()
        FirstRunRivalCode(viewModel: viewModel)
    }
}

@available(iOS 15.0, *)
struct RivalCodeEntry_Previews: PreviewProvider {
    static var previews: some View {
        @ObservedObject var viewModel: FirstRunInfoViewModel = FirstRunInfoViewModel()
        RivalCodeEntry(viewModel: viewModel)
    }
}

struct FirstRunSocials_Previews: PreviewProvider {
    static var previews: some View {
        FirstRunSocials()
    }
}

@available(iOS 16.0, *)
struct FirstRunRankMethod_Previews: PreviewProvider {
    static var previews: some View {
        FirstRunRankMethod()
    }
}

extension Binding where Value == String {
    func limit(_ length: Int) -> Self {
        if self.wrappedValue.count > length {
            DispatchQueue.main.async {
                self.wrappedValue = String(self.wrappedValue.prefix(length))
            }
        }
        return self
    }
}
