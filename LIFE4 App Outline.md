# LIFE4 App Outline

1 - Landing
    Description
        Landing page that handles app startup
    User Interface
        None; immediately navigates to another flow
    Behavior/Navigation
        If an account exists, skip to 5
        Else continue to 2
    Associated Data/Capabilities
        Account Status

2 - Signup/Login
    Description
    User Interface
        self-contained form
    Behavior/Navigation
    Associated Data/Capabilities

3 - Initial Rank Selection
    Description
    User Interface
    Behavior/Navigation
    Associated Data/Capabilities

4 - Placement Trial
    Description

    User Interface
        Placement selection
        Placement details/photo collection
    Behavior/Navigation
        User can choose to move to 3
    Associated Data/Capabilities

5 - Main Screen
    Description





Data and Capabilities

Account Status (aka Init State)
    internal
    exposes an enum flag to decide which screen to show on app start
        null, PLACEMENTS, RANKS, DONE
    set when the user completes initial 
Account Details
    internal
