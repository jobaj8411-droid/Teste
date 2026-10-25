# CineCraft AI — All-in-One AI Video Generator

CineCraft AI transforms text prompts, still images, and story concepts into cinematic video projects right on Android. It combines a state-of-the-art AI generation engine (Gemini & multi-model video synthesis with Gemini prompt expansion), rich camera and aspect ratio controls, a multi-scene AI story studio, and an intuitive credit & project management dashboard.

### User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural and UX choices have been established based on your feedback:
> - **AI Generation Strategy**: Multi-model hub architecture pairing Google Gemini (via `gemini-3.5-flash` for prompt refinement, structured script generation, and scene breakdowns; and `gemini-omni-1.1-flash` video interaction capabilities) with customizable pluggable API profiles (Veo, Runway, Luma, Pika presets) and video synthesis engine.
> - **Home Screen Focus**: Balanced studio layout giving direct, equal spotlight to Text-to-Video, Image-to-Video, and Story Maker alongside rapid AI tools (Voice Generator, Talking Photo, Prompt Enhancer, Video Editor).
> - **Storage & Account Model**: Local-first high-performance Room database storing projects, video assets, prompts, credit ledger (starts with 150 free credits), and subscription tier state without mandatory login hurdles.
> - **Video Playback & Creation**: Native hardware-accelerated video rendering and interactive dynamic canvas animations simulating real-time rendering passes with progress stages (prompt analysis, frame composition, motion synthesis, encoding).

---

### 1. Overview & Core Concept

- **What It Does**: CineCraft AI is an all-in-one generative video creator for Android. Users can create videos from scratch via natural language prompts, animate static photos into cinematic clips with 3D camera sweeps, or develop entire multi-scene short films from a single story pitch with automatic scripts, narration, and scene sequencing.
- **Target Audience / Persona**: Content creators (TikTok/Reels/Shorts), indie filmmakers, marketers, storytellers, and visual artists seeking studio-grade AI video generation with granular motion controls and instant mobile iteration.
- **Key Value**: Streamlines the entire ideation-to-render workflow into a seamless, tactile mobile experience with instant credit tracking, prompt magic enhancement, scene-by-scene script breakdowns, and full video history management.

---

### 2. User Experience & Visual Design

#### Navigation & Screen Hierarchy
The app features an intuitive 5-tab bottom navigation with Material 3 glassmorphism:
1. **Home Studio**: Welcome banner, active credit pill, fast-action tools grid, trending video showcase with playable previews, and recent projects reel.
2. **Explore & Templates**: Filterable feed of viral AI video templates (Cinematic, Anime, Cyberpunk, Photorealistic, Fantasy, 3D Render) with "Use Prompt" one-tap fork action.
3. **Create Hub**: Dedicated creation launchpad featuring:
   - **Text-to-Video**: High-level prompt input, Gemini "Magic Enhance" button, aspect ratio selector (9:16, 16:9, 1:1, 4:5), style selector, model switcher, duration/FPS/seed controls, credit estimator.
   - **Image-to-Video**: Photo picker/camera selector, camera motion presets (Pan Left/Right, Zoom In/Out, Tilt Up/Down, Orbit 360, Cinematic Dolly), motion intensity slider, first/last frame controls.
   - **Story-to-Video**: Story concept input, genre/tone selector, multi-scene AI script generation, interactive scene editor with narration text, visual style consistency, and scene regeneration.
4. **My Videos & Studio Library**: Grid and list views of completed, in-progress, and draft video projects with instant playback, full-screen player, prompt reuse, metadata inspection, and export options.
5. **Profile & Credit Vault**: User subscription badge (Free, Pro Creator, Studio VIP), daily bonus claiming, credit history ledger, generation settings, model API key config (for custom endpoints), and billing options.

#### Visual Identity & Theme
- **Theme**: Luxury Dark Studio (Deep space obsidian `#0B0D17`, midnight navy `#12162A`, surface cards `#181D38`).
- **Accents**: Neon violet gradient (`#8B5CF6` to `#6366F1`), electric cyan highlights (`#06B6D4`), and gold credit highlights (`#F59E0B`).
- **Typography**: Clean, geometric modern typography with high-contrast headlines and readable monospace prompt chips.
- **Micro-Interactions**: Shimmer loading effects during generation, tactile spring button clicks, animated progress rings with stage indicators ("Refining Prompt" → "Synthesizing Motion" → "Compositing Audio" → "Finalizing Render"), and smooth tab transitions.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Direct Gemini REST & Interactions Integration**
  - *Chosen Approach*: Leverage Retrofit and Kotlin Serialization for `gemini-3.5-flash` (prompt refinement and structured scene script generation) and `gemini-omni-1.1-flash` (Interactions API video pipeline). Include an intelligent generation synthesizer that delivers realistic cinematic renders and video playback even if network credentials are initially absent or offline.
  - *Why*: Delivers an immediate zero-friction developer and user experience with genuine API hooks while remaining fully resilient to network and key configuration states.
- **Decision 2: Room Database for Local-First Persistence**
  - *Chosen Approach*: Android Room database with reactive Kotlin Flow for projects, generated video items, scene scripts, and credit ledger transactions.
  - *Why*: Instant offline availability, fast launch times, guaranteed user data preservation without cloud configuration roadblocks, and perfect alignment with user preference.
- **Decision 3: Multi-Tool Sub-Screens with Unified Navigation**
  - *Chosen Approach*: Clean Compose state-based navigation with `BackHandler` integration across all nested sub-studios (Text-to-Video, Image-to-Video, Story Maker, Video Player, Credit Shop, Template Detail).
  - *Why*: Fluid navigation without routing crashes, keeping state perfectly synchronized between list views and active creation drafts.

---

### 4. Technical Architecture & Data Strategy *(Technical Reference)*

#### System Architecture Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                        CineCraft AI Android App                        │
├────────────────────────────────────────────────────────────────────────┤
│                           UI Layer (Compose)                           │
│  ┌──────────────┬──────────────┬──────────────┬─────────────┬────────┐  │
│  │     Home     │   Explore    │ Create Studio│  My Videos  │Profile │  │
│  │  Dashboard   │  Templates   │(Text/Img/Sty)│  & Player   │& Credits│  │
│  └──────┬───────┴──────┬───────┴──────┬───────┴──────┬──────┴────┬───┘  │
│         │              │              │              │           │      │
│         ▼              ▼              ▼              ▼           ▼      │
│  ┌───────────────────────────────────────────────────────────────────┐  │
│  │                 CineCraftViewModel & State Holders                │  │
│  │   - Active Creation State (Prompt, Image Uri, Aspect, Camera)     │  │
│  │   - Story Script Generator & Scene Breakdown State                │  │
│  │   - Generation Engine Queue & Progress Stage Tracker              │  │
│  │   - Credit Balance & Subscription State                           │  │
│  └───────────────────┬───────────────────────────────┬───────────────┘  │
│                      │                               │                  │
├──────────────────────┼───────────────────────────────┼──────────────────┤
│                      ▼                               ▼                  │
│       ┌────────────────────────────┐    ┌─────────────────────────┐     │
│       │   AiVideoRepository        │    │  ProjectRoomRepository  │     │
│       │  - Gemini Prompt Magic     │    │  - AppDatabase (Room)   │     │
│       │  - Story Script Generator  │    │  - VideoProjectDao      │     │
│       │  - Video Engine Pipeline   │    │  - UserCreditDao        │     │
│       │  - Model Preset Config     │    │  - TemplateDao          │     │
│       └──────────────┬─────────────┘    └────────────┬────────────┘     │
│                      │                               │                  │
├──────────────────────┼───────────────────────────────┼──────────────────┤
│                      ▼                               ▼                  │
│     ┌────────────────────────────────┐     ┌───────────────────┐        │
│     │ Google Generative Language API │     │   Local Storage   │        │
│     │ - gemini-3.5-flash             │     │   - SQLite (Room) │        │
│     │ - gemini-omni-1.1-flash        │     │   - Cached Videos │        │
│     └────────────────────────────────┘     └───────────────────┘        │
└────────────────────────────────────────────────────────────────────────┘
```

#### Core Data Entities
- **`VideoProject`**: ID, title, prompt, negativePrompt, creationType (`TEXT_TO_VIDEO`, `IMAGE_TO_VIDEO`, `STORY_TO_VIDEO`), style, aspectRatio, durationSeconds, resolution, videoUrl/previewUri, thumbnailUrl, status (`PENDING`, `GENERATING`, `COMPLETED`, `FAILED`), progressPercent, createdAtTimestamp, creditCost, modelUsed, cameraMotion.
- **`StoryScene`**: Scene index, narrationText, visualDescription, durationSeconds, videoClipUrl, isRegenerating.
- **`CreditTransaction`**: ID, type (`DAILY_BONUS`, `VIDEO_GENERATION`, `PACK_PURCHASE`), amount, timestamp, description.
- **`VideoTemplate`**: ID, title, category, prompt, previewUrl, styleTag, ratio, duration.

#### Step-by-Step Execution Plan
1. **App Metadata & Resources**: Update `metadata.json` ("CineCraft AI") and `strings.xml` to match platform naming rules.
2. **Dependencies & Networking Configuration**: Configure Kotlin Serialization, Room Database, and OkHttp/Retrofit in `app/build.gradle.kts`.
3. **Data Layer (Room & API)**:
   - Define Room Entities, DAOs, and Database for projects, templates, and credit transactions.
   - Implement Gemini API models, endpoints, and `AiVideoRepository` with real prompt enhancement and story scene decomposition.
4. **Core Business Logic (ViewModel)**:
   - Implement `CineCraftViewModel` managing state flows for generation tasks, credit checks, active filters, and scene regeneration.
5. **UI Layer - Material 3 Dark Theme & Shared Components**:
   - Modern color tokens, gradients, glowing cards, progress meters, aspect ratio pills, video player canvas.
6. **Feature Screens**:
   - `HomeScreen`: Quick launch tools, credit display, featured templates, recent projects.
   - `ExploreScreen`: Curated gallery with categories, search, and one-tap prompt forking.
   - `CreateStudioScreen`:
     - Text-to-Video tab with Magic Prompt Enhancer, style picker, aspect ratio selectors, and duration controls.
     - Image-to-Video tab with image picker, camera motion preset wheel, and motion intensity.
     - Story-to-Video tab with story idea generator, scene breakdown timeline, script editor, and narration selector.
   - `MyVideosScreen` & Fullscreen Video Player: Interactive playback, pause/play, scrub bar, prompt details, share/export simulation.
   - `ProfileScreen` & Credit Shop: Tier selection (Creator/Pro/Studio), claim daily credits, API settings.
7. **Verification**: Run `compile_applet` to ensure pristine compilation and error-free execution.
