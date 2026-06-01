# My3DApp Assignment — Android App (My3DApp)

 Overview
Android application for displaying 3D GLB models using SceneView.

## Library/Dependency Used
SceneView 2.2.1 (io.github.sceneview:sceneview:2.2.1)

### SceneView
- Built on top of Filament (Google's high performance 3D engine)
- Simple API for loading GLB models
- Handles lighting and rendering automatically
- Better than raw Filament which requires complex setup.

#### Performance Optimizations
Models are loaded inside sceneView.post{} so SceneView is fully initialized before model loading begins, preventing crashes on startupEach
SceneView instance is independent
Pinch resize uses coerceIn to prevent extreme sizes that could cause memory issues

##### Trade-offs Made
Used one SceneView for each model card — simpler code but higher memory usage
    with many models open at once
All models load immediately when selected
Chose Views over Jetpack Compose for simpler SceneView integration.

###### What I Would Improve With More Time
Add loading spinner while model is being loaded
Use Jetpack Compose
Add model name label on each card
Save card positions when app is backgrounded.

####### Known Bugs / Limitations
With 5+ models open simultaneously, memory usage is high on low-end devices
Close button can sometimes be hidden behind other cards
Interaction mode pinch zoom sensitivity may feel different across devices

######## Minimum SDK
Android 7.0 (API 24)