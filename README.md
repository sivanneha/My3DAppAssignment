# 3D Model Viewer — Android App

## Library Used
**SceneView 2.2.1** (io.github.sceneview:sceneview:2.2.1)

### Why SceneView?
- Built on top of Filament (Google's high performance 3D engine)
- Simple API for loading GLB models
- Handles lighting and rendering automatically
- Better than raw Filament which requires complex setup

## Performance Optimizations
- Models are loaded inside `sceneView.post{}` to ensure SceneView is
  fully initialized before model loading begins, preventing crashes on startup
- Each SceneView instance is independent so only visible models use GPU resources
- `scaleToUnits = 1.0f` keeps model geometry small and efficient
- Pinch resize uses `coerceIn` to prevent extreme sizes that could cause memory issues

## Trade-offs Made
- Used one SceneView per model card — simpler code but higher memory usage
  with many models open at once
- No lazy loading — models load immediately when selected
- Chose Views over Jetpack Compose for simpler SceneView integration

## What I Would Improve With More Time
- Lazy load models only when card is visible on screen
- Add loading spinner while model is being loaded
- Reuse a single Filament engine across all SceneView instances to save memory
- Add model name label on each card
- Save card positions when app is backgrounded

## Known Bugs / Limitations
- With 5+ models open simultaneously, memory usage is high on low-end devices
- Close button can sometimes be hidden behind other cards
- Interaction mode pinch zoom sensitivity may feel different across devices

## Minimum SDK
Android 7.0 (API 24)
