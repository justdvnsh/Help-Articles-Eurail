# Help Articles App - Android Take-Home Assignment

## Overview

A Kotlin Multiplatform (KMP) Android application that displays help articles with offline support, error handling, and automatic content refresh. Built with Jetpack Compose and Material 3.

**Rationale - Why KMP ? **

- I felt that project wanted to test the KMP skills along with best coding practices.
- That's why I decided to go with KMP, as this project is a pure compose multiplatform app, which not
- only shows the KMP skills, but also displays the best coding practices and separation of concerns
- of a regular android app.

## Screenshots

![Screenshot 2025-11-18 at 12.35.03 PM.png](docs/Screenshot%202025-11-18%20at%2012.35.03%E2%80%AFPM.png)
![Screenshot 2025-11-18 at 12.34.45 PM.png](docs/Screenshot%202025-11-18%20at%2012.34.45%E2%80%AFPM.png)
![Screenshot 2025-11-18 at 12.34.29 PM.png](docs/Screenshot%202025-11-18%20at%2012.34.29%E2%80%AFPM.png)
![Screenshot 2025-11-18 at 12.34.08 PM.png](docs/Screenshot%202025-11-18%20at%2012.34.08%E2%80%AFPM.png)

## Test cases

![Screenshot 2025-11-18 at 12.37.56 PM.png](docs/Screenshot%202025-11-18%20at%2012.37.56%E2%80%AFPM.png)
![Screenshot 2025-11-18 at 12.38.23 PM.png](docs/Screenshot%202025-11-18%20at%2012.38.23%E2%80%AFPM.png)


## Screen Recording - Working video

https://drive.google.com/file/d/1VLdeDJsyPhyfFzZoB66SocLuNTlD4sKD/view?usp=sharing

## Architecture

The app follows **Clean Architecture along with MVI** with clear separation of concerns:


### Layer Responsibilities

- **Presentation Layer**: Compose UI, ViewModels, UI state management
- **Domain Layer**: Business logic, domain models, use cases (acts as central hub)
- **Data Layer**: Network data sources, repository implementations, cache

**Key Principle**: Presentation and Data layers don't communicate directly - all communication flows through the Domain layer for better separation of concerns and scalability.

**Data Flow** : presentation -> domain <- data

```NOTE : We could have also gone with the classic MVVM architecture for a regular android app```

### Technology Stack

- **Kotlin Multiplatform** (KMP) with Android target
- **Jetpack Compose** with Material 3
- **Ktor** for HTTP networking (Would have been retrofit in case of a regular android app)
- **Koin** for dependency injection (Would have been dagger 2/hilt in case of a regular android app)
- **Kotlinx Coroutines & Flow** for asynchronous operations
- **WorkManager** for background tasks
- **Konnectivity** for network state monitoring (Would not need a library for network monitoring in case of a regular android app)

---

## Features Implemented

### ✅ 1. List Screen

**Implemented:**
- ✅ Displays article titles, summaries, and updated dates
- ✅ Search functionality with debounce (5 seconds)
- ✅ Loading state with CircularProgressIndicator
- ✅ Error state display with user-friendly messages
- ✅ Refresh button in header
- ✅ Settings navigation

**UI Components:**
- `ArticleListPage`: Main list screen composable
- `ArticleListItem`: Reusable article card component
- `SearchBar`: Search input with IME action support

**State Management:**
- Uses `StateFlow` with sealed `ArticleListState` data class
- Type-safe state handling with loading, error, and data states

### ✅ 2. Detail Screen

**Implemented:**
- ✅ Full article content rendering (HTML)
- ✅ Article title and metadata display
- ✅ Loading state
- ✅ Error state with server-provided error messages
- ✅ Back navigation

**HTML Rendering:**
- Platform-specific implementation using `expect/actual`
- Android: Uses `HtmlCompat.fromHtml()` with `TextView`
- iOS: Placeholder implementation ( as it was not required for assignment)

```NOTE : In a regular android app, we wouldn't need to go with platform-specific implementation. We could have simply implemented this in the android app itself (like how it has been done in the android specific implementation)``` 

### ✅ 3. Error Handling

**Error Classification:**

The app distinguishes between two types of errors:

1. **Connectivity/Transport Errors** (`DataError.RemoteErrors`):
    - `NO_INTERNET`: Network unreachable
    - `REQUEST_TIMEOUT`: Request timed out
    - `SERVER`: 5xx server errors
    - `SERIALIZATION`: JSON parsing failures
    - `TOO_MANY_REQUESTS`: Rate limiting (429)
    - `UNKNOWN`: Generic network errors

2. **Local Errors** (`DataError.LocalErrors`):
    - `DISK_FULL`: Storage issues
    - `UNKNOWN`: Generic local errors

**Error Display:**
- All errors converted to user-friendly `UiText` messages
- Errors displayed in UI with clear messaging
- Safe fallback handling for malformed payloads (catches exceptions, returns generic error)

```NOTE : The implementation to handle error states have been implemented in such a way, that it will be extremely straightforward to scale for (n) number of errors.```

**Error Flow:**


### ✅ 4. Offline Mode (KMP Cache)

**Cache Module Structure:**
- Separate KMP module (`:cache`) for shared cache logic
- Thread-safe operations using `Mutex`
- TTL-based staleness checking

**Cache Strategy:**

1. **Fresh Cache Check**: If cache exists and is within TTL, return immediately
2. **Stale Cache Fallback**: If remote fetch fails, return stale cache (if available)
3. **Cache on Success**: Automatically cache successful responses

**Staleness Rule:**
- **List Cache TTL**: 6 hours (`DEFAULT_LIST_TTL_MS`)
- **Detail Cache TTL**: 24 hours (`DEFAULT_DETAIL_TTL_MS`) - *Note: Not used since details are in list response*
- **Formula**: `(currentTime - cachedAt) <= TTL` → cache is fresh

**Cache Implementation:**
- `CacheDataSource`: Main cache logic with TTL checking
- `CacheStore`: In-memory storage (can be replaced with persistent storage)
- `CacheWrapper`: Wraps cached data with timestamp
- Automatic corruption handling (removes corrupted cache entries)

**Offline Behavior:**
- When offline: Shows cached data if available
- When no cache: Shows error message
- Cache can be disabled via Settings (for testing)

```NOTE : The cache has been implemented in such a way, that I have provided an interface to create own data stores as well. Currently the cache used in this project uses a simple list-based cache, but using the interface, since this is a KMP library, it could easily be inherited to create custom cache stores in the project, such as platform specific (shared-prefs for android) & (user-defaults for iOS)```

### ✅ 5. Auto-Refresh

**Connectivity-Based Refresh:**
- Observes network connectivity using `Konnectivity`
- When connectivity is restored:
    - Automatically triggers `getArticles()` if cache is stale
    - Updates UI with latest articles

```NOTE : If this were a regular android app, we wouldn't need this checker all together. We could have relied on android's native connectivity checker principles```

**Implementation:**
- `ArticleListViewModel.observeConnectivity()` monitors connectivity state
- Filters for `isConnected == true` events
- Triggers refresh on connectivity restoration

**Note on App Lifecycle Resume:**
- Auto-refresh on app resume is handled indirectly via connectivity observer
- When app resumes and connectivity is available, the observer triggers refresh
- This satisfies the requirement without explicit ProcessLifecycleOwner implementation

**Rationale** : I went with this decision, instead of handling onResume calls, is because I decided to go with a KMP project instead of a regular android app, so to accomodate the iOS version as well, we had to draft a generic way. However, the principles do remain the same , but we could have made it more abstract by using a platform specific implementation (lifecycles on android and ios), having had more time on hand.

### ✅ 6. Background Prefetch

**WorkManager Implementation:**
- `WorkManagerScheduler`: Schedules periodic refresh work
- `RefreshArticlesWorker`: Executes background refresh

**Scheduling Strategy:**
- **Interval**: 24 hours with 12-hour flex window
- **Constraints**:
    - `NetworkType.CONNECTED`: Only runs when network is available
    - `RequiresBatteryNotLow`: Respects device battery state
- **Policy**: `ExistingPeriodicWorkPolicy.KEEP` (avoids duplicate work)

**Rationale:**
- 24-hour period ensures cache is refreshed roughly once per day
- 12-hour flex allows WorkManager to optimize execution within a 12-24 hour window
- Network constraint prevents wasted battery on failed requests
- Battery constraint respects user's device battery state

**Worker Logic:**
- Checks if cache is fresh before fetching
- If fresh: Returns success immediately (no work needed)
- If stale: Fetches from network and caches result
- On failure: Returns retry (WorkManager will retry later)

### ✅ 7. Mock Data Source

**Implementation:**
- `RemoteArticleDataSourceImpl`: Self-contained mock implementation
- `MockArticleDataSourceAPI`: Provides mock article data

**Mock Scenarios Covered:**
- ✅ **Normal responses** (70% probability): Successful list and detail responses
- ✅ **Server errors** (15% probability): 500 Internal Server Error
- ✅ **Transport errors** (10% probability): SocketTimeoutException
- ✅ **No internet** (5% probability): UnresolvedAddressException

**Mock Features:**
- Random network delay simulation (300-1500ms)
- Realistic error distribution
- Multiple articles with HTML content

### ✅ 8. KMP Cache Module

**Module Structure:**
- `:cache` - Separate KMP library module
- Shared code in `commonMain`
- Platform-specific implementations in `androidMain` and `iosMain`

**Key Components:**
- `CacheDataSource`: Main cache logic with TTL/staleness
- `CacheStore`: Storage abstraction (in-memory implementation)
- `HelpArticleCache`: Interface for storage operations
- `CacheWrapper`: Data wrapper with timestamp

**Features:**
- Thread-safe operations (Mutex-based)
- TTL-based staleness checking
- Automatic corruption handling
- Configurable TTL values

**Usage from Android:**
- Integrated via `implementation(project(":cache"))`
- Used in `ArticleRepositoryImpl` for caching logic

### ✅ 9. Tests

**Unit Test (KMP Cache):**
- **File**: `cache/src/commonTest/kotlin/com/thatmobiledevagency/cache/CacheDataSourceTest.kt`
- **Tests**:
    - `cachedEntry_isFresh_beforeTtl`: Verifies cache is fresh when within TTL
    - `cachedEntry_isStale_afterTtl`: Verifies cache is stale after TTL expires
    - `cachedEntry_isFresh_withinTtl`: Verifies cache freshness within TTL window
    - `isListFresh_false_when_no_cache_exists`: Verifies behavior when no cache exists

**Compose UI Test:**
- **File**: `composeApp/src/androidTest/kotlin/com/thatmobiledevagency/helparticles/ArticleListPageTest.kt`
- **Tests**:
    - `errorState_showsErrorAndRefreshButtonTriggersRetry`: Tests error display and retry functionality
    - `errorState_errorMessageIsVisible`: Tests error message visibility

**Test Infrastructure:**
- Uses Mockito-Android for mocking (Android-compatible)
- Compose UI testing with `createAndroidComposeRule`
- Coroutine testing with `runTest` and `advanceUntilIdle`

### ✅ 10. Tech Expectations

**Compose & Material 3:**
- ✅ Material 3 design system
- ✅ Light/dark theme support (system-based)
- ✅ Scalable typography
- ✅ Accessible touch targets (48dp minimum)
- ✅ Clean state handling with sealed classes

**State Management:**
- ✅ `UiState` pattern with data classes
- ✅ Sealed classes for type-safe state
- ✅ StateFlow for reactive state updates

**Optional Features:**
- ✅ Smooth animations (scroll animations in list)

---

## Key Design Decisions

### 1. Cache Strategy

**Decision**: List-only caching, no separate detail caching

**Rationale**:
- Article details are included in the list response
- No need for separate detail API calls
- Simpler cache management
- Better offline experience (all data available from list)
- Also, since it is a help-articles app, caching an entire list (a couple hundred articles which are not going to change very frequently), caching the entire list seems pretty feasible.

### 2. Error Handling Approach

**Decision**: Use error codes/enums instead of parsing backend error payloads

**Rationale**:
- Assignment mentions backend errors with `errorCode`, `errorTitle`, `errorMessage`
- However, I went with error codes displaying a range of errors. However, this implementation could very well be tuned to handle the payloads as well, inside the `safeCall` extention function I created.
- Current approach distinguishes connectivity vs server errors clearly
- Error messages are user-friendly and consistent

### 3. Auto-Refresh Implementation

**Decision**: Connectivity-based refresh instead of explicit app lifecycle handling

**Rationale**:
- Connectivity observer handles both connectivity restoration AND app resume scenarios
- When app resumes with connectivity, observer automatically triggers refresh
- Simpler implementation, covers the requirement
- This implementation as mentioned was made to accomodate both iOS and Android's native functionalities, since this is a compose multiplatform app. But again, as mentioned, this functionaly could easily be tuned into a platform-specific implementation, having process based lifecycle owners for android.

### 4. HTML Rendering

**Decision**: Platform-specific implementation using `expect/actual`

**Rationale**:
- Android uses `HtmlCompat.fromHtml()` (simple, efficient)
- iOS placeholder (as was not required for assignment)
- Can be extended to use WebView if needed for complex HTML

### 5. Search Debounce

**Decision**: 5-second debounce for search queries

**Rationale**:
- Reduces API calls while user is typing
- Balances responsiveness with efficiency
- Can be adjusted based on requirements
- In the implementation, I have decided to not cache search query results due to time constraints, but they could as well be cached similar to the list.
- Search also calls our mock API, so the results will be based on the mock results. Sometimes search results could also provide errors, and same goes for detail screen as well.

---

## What's Completed ✅

### Core Requirements
- ✅ List screen with search/filter
- ✅ Detail screen with HTML rendering
- ✅ KMP cache module with TTL/staleness
- ✅ Offline mode with cache fallback
- ✅ Auto-refresh on connectivity
- ✅ Background prefetch with WorkManager
- ✅ Error handling (connectivity vs server errors)
- ✅ Mock data source with various scenarios
- ✅ Unit test for cache staleness
- ✅ Compose UI test for error + retry
- ✅ Light/dark theme support
- ✅ Material 3 design

### Additional Features
- ✅ Settings screen (cache toggle - disable cache when we would want to test the variety of network errors)
- ✅ Navigation with type-safe routes
- ✅ Search functionality with debounce
- ✅ Loading states
- ✅ Error states with user-friendly messages
- ✅ Refresh button in UI
- ✅ Ui Test cases
- ✅ Cache unit tests
- ✅ Minimal shared cache
- ✅ Background worker for android
- ✅ Offline usage.

---

## What's Missing / Not Implemented ⚠️

### 1. User friendly states
- Diff. error states and empty states have been handled, but given more time, they could have been more user friendly.

### 2. Backend Error Payload Parsing
- Payload parsing is not present, so that we could show the range of errors in given time, but the current implementation could easily be extended to showcase the same.

### 3. Detail Screen Caching
- details are in list response, so caching list covers this
**Note**: This is a design decision, not a missing feature

### 4. App Lifecycle Resume (Explicit)
- When app resumes with connectivity, refresh is triggered automatically.
- ProccessLifecycleOwner not implemented, but could easily be extended to platform-specific requirements when needed.

### 5. Date Formatting
- Currently, I have not spent time in date formatting. Since this is a mock data source, I am displaying the dates as is, but in prod, obviously this would be converted into a more reader-friendly date value.

---

## Trade-offs & Decisions

### Time Constraints (4-5 hours)

**Completed within time:**
- Core architecture and structure
- List and detail screens
- Cache implementation
- Error handling
- Background refresh
- Basic tests

**Simplified for time:**
- Empty state UI (functional but basic)
- Date formatting (shows timestamp)
- Comprehensive test coverage (basic tests only)
- Backend error payload parsing (using error codes instead)

**Design Decisions:**
- No detail caching (details in list response)
- Connectivity-based refresh (covers app resume requirement)
- Simple HTML rendering (HtmlCompat instead of WebView)

---

## Future Improvements

If more time were available I would have:

1. **Enhanced Empty States**: Dedicated empty state composables with retry buttons
2. **Backend Error Parsing**: Parse structured error payloads from API responses
3. **Date Formatting**: Human-readable date formatting (e.g., "2 hours ago")
4. **More Tests**: Comprehensive test coverage for ViewModels, repositories, and UI
5. **Detail Caching**: Separate detail cache if API structure changes
6. **App Lifecycle**: Explicit ProcessLifecycleOwner for more control
7. **Search Empty State**: "No results found" message for search
8. **Accessibility**: More comprehensive accessibility labels and testing
9. **Animation**: More polished animations and transitions
10. **Error Recovery**: More sophisticated error recovery strategies

---

**Estimated Time Spent**: ~4-5 hours (as per assignment constraint)