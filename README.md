# HabitBuilder


<img width="428" height="908" alt="User Landing Page" src="https://github.com/user-attachments/assets/40a18eec-e560-4bd7-aba7-2b00d2e3c479" />


## Overview
HabitBuilder is a 90-day, daily habit-tracking app designed to help users establish and maintain positive routines. Users create actionable tasks that must be completed and checked off each day, and the app organizes these habits by category while providing performance metrics for individual tasks, entire categories, and overall progress. This allows people with busy schedules to track improvement in areas such as health, study habits, productivity, and finances, all while gradually integrating consistent, goal-oriented behaviors into their routines.

Daily tracking is a critical part of habit formation because repetition and awareness are key drivers of long-term behavioral change. By prompting users to complete and record actions every day, HabitTracker reinforces the cue-routine-reward cycle that supports new habits. The visual feedback and progress trends create accountability, helping users see how small, steady actions accumulate over time. This combination of consistency, reflection, and measurable progress makes HabitTracker an effective tool for anyone looking to build sustainable, productive habits.


## User Stories
- US1 – Login & setup habits – As a HabitBuilder user, I want to log into the application and add the activities I want to complete every day.
- US2 – Daily tracking – As a HabitBuilder user, I want to log in and check off my activities for the day.
- US3 – Performance over time – As a HabitBuilder user, I want to view my performance for the day, week, and month.
- US4 – Edit & remove habits – As a HabitBuilder user, I want to edit and delete some of my activities.

## Architecture
### Android app in Java with Activities (no Fragments yet)

<img width="2747" height="3804" alt="App UI Workflow" src="https://github.com/user-attachments/assets/2c26b1d8-4ee2-4f92-bba6-7319ae0bea1b" />

### Room database (Users, Habits, Categories, HabitLogs)
  
<img width="579" height="418" alt="CSUMB_CST338_Project02_HabitBuilder_Database" src="https://github.com/user-attachments/assets/cec549d8-dbc3-45c5-b885-26ef3cd5241d" />

### HabitBuilderDatabase + HabitBuilderRepository
### Activities:
  - LoginActivity – authenticate user
  - SignupActivity – create account
  - MainActivity – landing (stats + checklist)
  - EditingActivity – manage habits
  - AccountActivity – reset/delete/logout
  - ManageActivity – admin tools

## Project Structure
- `database/entities/` – POJO entities
- `database/` – Room database, DAOs, Repository
- root package – Activities and intent factories
- `res/layout` – layouts per Activity
- `res/menu/bottom_nav_menu.xml` – bottom nav config

## Flow

  <img width="5544" height="7002" alt="Flowchart - Page 1 App User Flow Diagram" src="https://github.com/user-attachments/assets/c9b52a8d-0952-4ef1-8d8f-edb8a888a0e4" />

## Collaboration Workflow

0. **Clone the Repo**
   - Via Android Studio -> New -> Project from version control... -> GitHub -> theReaLSingularity/CST_338_F25B_Group2_Project02 -> Clone

1. **Update main**
   - `git switch main`
   - `git pull --ff-only`

2. **Create a branch from main for each PR**
   - Naming convention: `<initial>/<issue-id>-short-description`
   - Examples:
     - `b/IB-3-room-database`
     - `a/IA-1-main-landing`
     - `l/IL-1-signup-ui`
   - Command: `git switch -c b/IB-3-room-database`

3. **Do the work**
   - Make multiple small, focused commits
   - Reference issues in commit messages if helpful

4. **Push branch and open a PR**
   - `git push -u origin b/IB-3-room-database`
   - PR title example: `PR: B2 – Room setup and data layer (Closes IB-3, IB-4, IB-5, IB-6)`
   - Replace `B` with your initial (A, L, etc.)
   - Link the corresponding issues in the PR (e.g., "Closes IB-3, IB-4")

5. **Review and merge**
   - Another teammate reviews and approves
   - Merge via GitHub

6. **Update local main and start next branch**
   - `git switch main`
   - `git pull --ff-only`
   - Repeat from step 2 for the next PR

7. **Branches**
   - You may keep branches for grading clarity, or delete them after merge.
   - If in doubt, **keep remote branches** until the project is graded.

## Team Responsibilities

### Bryan — Architecture & Editing Tools

- Established initial project structure, Room database setup, and bottom navigation framework.
- Implemented core data layer: entities, DAOs, HabitBuilderDatabase, and HabitBuilderRepository.
- Developed LoginActivity with database-backed authentication and intent factories.
- Built EditingActivity with full habit creation, modification, and termination logic.
- Authored unit tests for DAO operations, repository logic, and navigation intents.

### Alexander — Landing Screen, Performance Metrics & Admin Tools

- Developed MainActivity as a shared landing screen for users and admins.
- Implemented habit checklist UI with RecyclerView, grouping, and completion visual updates.
- Added performance metric computation (day/week/30-day) using HabitLogs.
- Built ManageActivity for admin user oversight and account management tasks.
- Wrote unit tests for HabitLog/metrics logic.

### Lee — User Registration & Account Management

- Created SignupActivity with validation and database-backed user registration.
- Added account management features in AccountActivity for password resets, logout, and account deletion.
- Integrated UserDAO and Repository methods into account workflows.
- Implemented intent factories for navigation consistency.
- Authored unit tests for UserDAO, repository login/signup logic, and intent extras.

## Testing
- Instructions to run unit tests (`./gradlew test` or via Android Studio)
- List of what is covered (DAO tests, repository tests, intent tests)

