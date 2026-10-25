package com.example.model

enum class CreationType(val displayName: String) {
    TEXT_TO_VIDEO("Text to Video"),
    IMAGE_TO_VIDEO("Image to Video"),
    STORY_TO_VIDEO("AI Story Maker"),
    TALKING_PHOTO("AI Talking Photo"),
    VOICE_GEN("AI Voice & Sound"),
    VIDEO_EDITOR("AI Video Editor")
}

enum class VideoAspectRatio(val label: String, val ratioString: String, val widthWeight: Float, val heightWeight: Float) {
    PORTRAIT_9_16("9:16", "9:16 (TikTok/Reels)", 9f, 16f),
    LANDSCAPE_16_9("16:9", "16:9 (YouTube)", 16f, 9f),
    SQUARE_1_1("1:1", "1:1 (Square)", 1f, 1f),
    PORTRAIT_4_5("4:5", "4:5 (Instagram)", 4f, 5f)
}

enum class VideoStyle(val displayName: String, val promptModifier: String, val colorHex: Long) {
    CINEMATIC("Cinematic", "masterpiece, 8k cinematic lighting, photorealistic, anamorphic lens, 35mm film", 0xFF8B5CF6),
    ANIME("Anime", "studio ghibli and makoto shinkai style, vibrant colors, detailed anime background, cel shaded", 0xFFEC4899),
    CYBERPUNK("Cyberpunk", "cyberpunk aesthetic, neon lighting, volumetric fog, rainy night metropolis, futuristic reflections", 0xFF06B6D4),
    PHOTOREALISTIC("Photoreal", "hyper-realistic, high fidelity, 8k resolution, raw photo, natural diffused lighting", 0xFF10B981),
    FANTASY_3D("3D Fantasy", "pixar 3d style, vibrant fantasy lighting, unreal engine 5 render, octane render, magical glow", 0xFFF59E0B),
    VINTAGE_FILM("Vintage 70s", "1970s 16mm film grain, warm nostalgic tones, vintage lens aberrations, film aesthetics", 0xFFE11D48)
}

enum class CameraMotion(val displayName: String, val description: String) {
    NONE("Static Camera", "Fixed perspective with natural scene motion"),
    ZOOM_IN("Zoom In", "Smooth slow push-in towards the subject"),
    ZOOM_OUT("Zoom Out", "Gradual camera pull-back revealing surroundings"),
    PAN_LEFT("Pan Left", "Horizontal camera sweep moving from right to left"),
    PAN_RIGHT("Pan Right", "Horizontal camera sweep moving from left to right"),
    CINEMATIC_DOLLY("Cinematic Dolly", "Dynamic cinematic tracking shot with parallax"),
    ORBIT_360("Orbit 360", "Smooth 360-degree rotational arc around focal subject"),
    NATURAL_FLOAT("Natural Float", "Organic handheld floating camera movement")
}

enum class VideoResolution(val label: String, val creditMultiplier: Float) {
    RES_720P("720p HD", 1.0f),
    RES_1080P("1080p FHD", 1.5f),
    RES_4K("4K Ultra HD", 2.5f)
}

enum class VideoModelProvider(val displayName: String, val tag: String, val baseCost: Int) {
    VEO_FAST("Google Veo 2", "veo-2", 20),
    CINECRAFT_V3("CineCraft Turbo", "cinecraft-v3", 15),
    RUNWAY_GEN3("Runway Gen-3", "runway-gen3", 25),
    LUMA_DREAM("Luma Dream V2", "luma-dream", 20)
}

enum class ProjectStatus {
    PENDING,
    GENERATING,
    COMPLETED,
    FAILED
}

data class StorySceneItem(
    val sceneNumber: Int,
    var narration: String,
    var visualDescription: String,
    var durationSeconds: Int = 4,
    var cameraMotion: CameraMotion = CameraMotion.CINEMATIC_DOLLY,
    var isRegenerating: Boolean = false
)

data class VideoTemplateItem(
    val id: String,
    val title: String,
    val category: String,
    val prompt: String,
    val style: VideoStyle,
    val aspectRatio: VideoAspectRatio,
    val durationSeconds: Int,
    val tags: List<String>,
    val viewsCount: String,
    val previewGradientColors: List<Long>
)
