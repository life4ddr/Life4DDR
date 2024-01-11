# LIFE4 App Outline

## Landing
##### Description
The landing page that handles app startup.
##### User Interface
None; immediately navigates to another flow.
##### Behavior/Navigation
- If an account exists, skip to **Main Screen** flow
- Otherwise continue to **Signup/Login** flow
##### Associated Data/Capabilities
- Account Status

----------------

## Signup/Login
##### Description
The flow surfaced to a user that does not have an existing account in the app yet.  This flow contains everything the user may need to do before finalizing the account and landing in **Main Screen**.


### Info Form
##### Description
Screen that allows user to input their details
##### User Interface
Step-by-step form
- If online login is disabled:
    - "Are you new here?" with "Yes"/"No" buttons
    - Username entry
    - Rival Code entry
    - If "new here" is "yes", show options
        - "Play a Placement Trial"
        - "Select a Rank from the List"
        - "Start without a Rank"
- If online login is enabled:
    - "Signup"/"Login" buttons
    - TODO
##### Behavior/Navigation
Upon form completion:
- If "new here" is "no", skip to **Initial Rank Selection**
- Otherwise, navigate based on button pressed:
    - "Play a Placement Trial" -> **Placement Trial**
    - "Select a Rank from the List" -> **Initial Rank Selection**
    - "Start without a Rank" -> **Main Screen**
##### Associated Data/Capabilities
- Account Status
- Account Details


### Initial Rank Selection
##### Description
Screen that allows the user to select their initial rank.
##### User Interface
Four vertically-stacked sections:
1. Class selector (Bronze, Silver, Gold, etc.)
2. Rank selector (I, II, III, etc.), initially hidden
3. Goals preview, initially hidden
4. Controls
    - Accept button, initially hidden
    - 
##### Behavior/Navigation
- Only Class selector is visible to start.
- When the user selects a class, make the Rank selector visible and populate it with the smaller ranks within that class.  Unselect any previous Rank and hide the Goals preview.
- When the user selects a Rank, make the Goals preview
##### Associated Data/Capabilities
TODO

### Placement Trial
Screen that allows the user to browse and play through a Placement trial to obtain a starting Rank.
##### User Interface
Two screens:
1. Placement selection list
    - Shows basic rules, and a list of the available placements.
    - Placements are expandable, and will show the list of songs, their difficulties, and a "Play Now" button to access the 
2. Placement details screen, give specific rules and facilitate results photo
##### Behavior/Navigation
TODO
##### Associated Data/Capabilities
TODO

----------------

## Main Screen
The "home" screen, surfaced to users on app start when an account already exists, or directly after account creation is fully completed.  This screen has three tabs, detailed below.

### Profile
Primary tab.  Shows the user's details, rank, and their personalized list of goals.
##### User Interface
Three vertically-stacked sections:
1. User info across the top
2. Suggestions
3. Active goals
##### Behavior/Navigation
Exists in an isolated tab
(If online login is disabled): User can tap the Rank icon to access the **Rank Selection** screen.
##### Associated Data/Capabilities
TODO


### Browse
This main screen tab allows users to interact with their song scores.  We'll surface the user's scores and allow the user to sort and filter them.
##### User Interface
Two vertically-stacked sections:
1. Controls across the top
    - Dropdown difficulty number selector, 1-19 and "All"
    - Filter button, opens a dialog to filter by
        - Difficulty class
        - Clear type
        - Mix
        - Locked status
        - Shock Arrows
    - Sort button, opens a dialog to sort by
        - Score
        - Clear type
        - Alphabetically
        - Difficulty number
        - Difficulty class
    - Import button, opens a dialog to import scores from:
        - DDR Score Manager app
        - Sanbai Icecream
        - SkillAttack
2. Scores list with two columns
    - Song name
    - Score
##### Behavior/Navigation
Exists in an isolated tab
Clicking on any of the controls shows their associated UI
Clicking on a song in the list will bring up the **Song Details** screen for it.
##### Associated Data/Capabilities
TODO


### Trials
This main screen tab allows users to interact with Trials, and serves as the launching point for playing them.
##### User Interface

##### Behavior/Navigation
TODO
##### Associated Data/Capabilities
TODO

----------------

### Song Details
TODO

----------------

## Data and Capabilities

#### Account Status (aka Init State)
Internal, stored in Preferences
An enum flag to decide which screen to show on app start.  Set when the user navigates between any of these screens.

##### Values
- null (initial **Signup/Login** page)
- PLACEMENTS
- RANKS
- DONE

#### Account Details
Internal, stored in Preferences
A series of fields for tracking the different user details.

##### Fields
- Username (string)
- Rival Code (string, stored as numerals only)
- User Rank (enum, **LadderRank?**)
- Social Networks (Map<SocialNetwork, string>)











### Name
TODO
##### User Interface
TODO
##### Behavior/Navigation
TODO
##### Associated Data/Capabilities
TODO