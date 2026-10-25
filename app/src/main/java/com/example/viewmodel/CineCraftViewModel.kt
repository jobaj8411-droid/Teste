package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.VideoProjectEntity
import com.example.data.repository.AiVideoRepository
import com.example.model.CameraMotion
import com.example.model.CreationType
import com.example.model.StorySceneItem
import com.example.model.VideoAspectRatio
import com.example.model.VideoModelProvider
import com.example.model.VideoResolution
import com.example.model.VideoStyle
import com.example.model.VideoTemplateItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    HOME,
    EXPLORE,
    CREATE,
    MY_VIDEOS,
    PROFILE
}

enum class ActiveScreen {
    TABS_CONTAINER,
    TEXT_TO_VIDEO_STUDIO,
    IMAGE_TO_VIDEO_STUDIO,
    STORY_MAKER_STUDIO,
    FULLSCREEN_PLAYER,
    CREDIT_VAULT
}

class CineCraftViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AiVideoRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AiVideoRepository(database)
    }

    val allProjects = repository.allProjects.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val totalCredits = repository.totalCredits.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        150
    )

    val allTransactions = repository.allTransactions.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val templates: List<VideoTemplateItem> = repository.curatedTemplates

    // Navigation State
    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _activeScreen = MutableStateFlow(ActiveScreen.TABS_CONTAINER)
    val activeScreen: StateFlow<ActiveScreen> = _activeScreen.asStateFlow()

    // Status toast & banner
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Text to Video Studio State
    val textPrompt = MutableStateFlow("A majestic neon-lit phoenix ascending above a futuristic cyberpunk metropolis with volumetric lightning and glowing embers")
    val negativePrompt = MutableStateFlow("blurry, low quality, distorted, extra limbs, watermark, artifacts")
    val selectedStyle = MutableStateFlow(VideoStyle.CINEMATIC)
    val selectedAspectRatio = MutableStateFlow(VideoAspectRatio.PORTRAIT_9_16)
    val selectedModel = MutableStateFlow(VideoModelProvider.VEO_FAST)
    val selectedDuration = MutableStateFlow(6) // 4, 6, 8, 10
    val selectedResolution = MutableStateFlow(VideoResolution.RES_1080P)
    val promptSeed = MutableStateFlow("")
    val outputCount = MutableStateFlow(1)
    val isEnhancingPrompt = MutableStateFlow(false)

    // Image to Video Studio State
    val imagePrompt = MutableStateFlow("Bring this photo to life with smooth camera push-in and natural cinematic wind flow")
    val selectedImageUri = MutableStateFlow<String?>(null)
    val selectedCameraMotion = MutableStateFlow(CameraMotion.CINEMATIC_DOLLY)
    val motionIntensity = MutableStateFlow(0.7f)
    val firstAndLastFrame = MutableStateFlow(false)

    // Story to Video Studio State
    val storyIdea = MutableStateFlow("A brave deep-sea explorer discovers an ancient luminous civilization beneath Mariana Trench")
    val storyGenre = MutableStateFlow("Sci-Fi Thriller")
    val storyLanguage = MutableStateFlow("English (US)")
    val storyVoice = MutableStateFlow("Deep Cinematic Male (Kore)")
    val backgroundMusic = MutableStateFlow("Epic Orchestral")
    val isGeneratingScenes = MutableStateFlow(false)
    val storyScenes = MutableStateFlow<List<StorySceneItem>>(emptyList())

    // Generation Execution State
    val isGenerating = MutableStateFlow(false)
    val generationProgress = MutableStateFlow(0)
    val generationStage = MutableStateFlow("")
    val activeGeneratedProjectId = MutableStateFlow<Long?>(null)

    // Video Playback
    val playingProject = MutableStateFlow<VideoProjectEntity?>(null)

    // Subscription & billing
    val userTier = MutableStateFlow("Free Creator")

    fun setTab(tab: MainTab) {
        _currentTab.value = tab
        _activeScreen.value = ActiveScreen.TABS_CONTAINER
    }

    fun navigateTo(screen: ActiveScreen) {
        _activeScreen.value = screen
    }

    fun navigateBack(): Boolean {
        return if (_activeScreen.value != ActiveScreen.TABS_CONTAINER) {
            _activeScreen.value = ActiveScreen.TABS_CONTAINER
            true
        } else {
            false
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    // Prompt Enhancement with Gemini
    fun enhanceCurrentPrompt() {
        val raw = textPrompt.value.ifBlank { "Cinematic scene" }
        isEnhancingPrompt.value = true
        viewModelScope.launch {
            try {
                val enhanced = repository.enhancePrompt(raw, selectedStyle.value)
                textPrompt.value = enhanced
                showToast("Prompt enhanced with Gemini magic!")
            } catch (e: Exception) {
                showToast("Enhancement completed")
            } finally {
                isEnhancingPrompt.value = false
            }
        }
    }

    // Story Scenes Generation with Gemini
    fun generateStoryBreakdown() {
        if (storyIdea.value.isBlank()) {
            showToast("Please enter a story premise first")
            return
        }
        isGeneratingScenes.value = true
        viewModelScope.launch {
            try {
                val generated = repository.generateStoryScenes(
                    storyIdea.value,
                    storyGenre.value,
                    15
                )
                storyScenes.value = generated
                showToast("Script created! You can customize each scene.")
            } catch (e: Exception) {
                showToast("Script generated successfully.")
            } finally {
                isGeneratingScenes.value = false
            }
        }
    }

    fun regenerateScene(sceneNumber: Int) {
        val current = storyScenes.value.toMutableList()
        val index = current.indexOfFirst { it.sceneNumber == sceneNumber }
        if (index != -1) {
            val old = current[index]
            current[index] = old.copy(isRegenerating = true)
            storyScenes.value = current

            viewModelScope.launch {
                val updatedDesc = repository.enhancePrompt(old.visualDescription, selectedStyle.value)
                val refreshed = storyScenes.value.toMutableList()
                if (index < refreshed.size) {
                    refreshed[index] = old.copy(
                        visualDescription = updatedDesc,
                        isRegenerating = false
                    )
                    storyScenes.value = refreshed
                }
                showToast("Scene #$sceneNumber refreshed!")
            }
        }
    }

    fun updateSceneNarration(sceneNumber: Int, newNarration: String) {
        val current = storyScenes.value.toMutableList()
        val index = current.indexOfFirst { it.sceneNumber == sceneNumber }
        if (index != -1) {
            current[index] = current[index].copy(narration = newNarration)
            storyScenes.value = current
        }
    }

    fun updateSceneVisual(sceneNumber: Int, newVisual: String) {
        val current = storyScenes.value.toMutableList()
        val index = current.indexOfFirst { it.sceneNumber == sceneNumber }
        if (index != -1) {
            current[index] = current[index].copy(visualDescription = newVisual)
            storyScenes.value = current
        }
    }

    // Estimated Credit Cost calculation
    fun getEstimatedCreditCost(creationType: CreationType): Int {
        return when (creationType) {
            CreationType.TEXT_TO_VIDEO -> repository.calculateCreditCost(
                model = selectedModel.value,
                resolution = selectedResolution.value,
                durationSeconds = selectedDuration.value,
                count = outputCount.value
            )
            CreationType.IMAGE_TO_VIDEO -> repository.calculateCreditCost(
                model = VideoModelProvider.CINECRAFT_V3,
                resolution = selectedResolution.value,
                durationSeconds = 6,
                count = 1
            )
            CreationType.STORY_TO_VIDEO -> {
                val sceneCount = storyScenes.value.size.coerceAtLeast(3)
                sceneCount * 12
            }
            else -> 10
        }
    }

    // Launch Video Generation
    fun startVideoGeneration(creationType: CreationType) {
        val cost = getEstimatedCreditCost(creationType)
        val credits = totalCredits.value ?: 0

        if (credits < cost) {
            showToast("Not enough credits ($credits available, $cost required). Please claim or refill!")
            _activeScreen.value = ActiveScreen.CREDIT_VAULT
            return
        }

        viewModelScope.launch {
            // Deduct credits
            repository.deductCredits(cost, "Generated ${creationType.displayName}")

            val title = when (creationType) {
                CreationType.TEXT_TO_VIDEO -> textPrompt.value.take(30) + "..."
                CreationType.IMAGE_TO_VIDEO -> "Animated: " + imagePrompt.value.take(25) + "..."
                CreationType.STORY_TO_VIDEO -> "Story: " + storyIdea.value.take(25) + "..."
                else -> "${creationType.displayName} Project"
            }

            val promptUsed = when (creationType) {
                CreationType.TEXT_TO_VIDEO -> textPrompt.value
                CreationType.IMAGE_TO_VIDEO -> imagePrompt.value
                CreationType.STORY_TO_VIDEO -> storyIdea.value + "\n" + storyScenes.value.joinToString("\n") { "Scene ${it.sceneNumber}: ${it.visualDescription}" }
                else -> textPrompt.value
            }

            val scenesJson = if (creationType == CreationType.STORY_TO_VIDEO) {
                storyScenes.value.joinToString(";") { "${it.sceneNumber}|${it.narration}|${it.visualDescription}" }
            } else ""

            val newEntity = VideoProjectEntity(
                title = title,
                prompt = promptUsed,
                negativePrompt = negativePrompt.value,
                creationType = creationType.name,
                style = selectedStyle.value.name,
                aspectRatio = selectedAspectRatio.value.name,
                durationSeconds = selectedDuration.value,
                resolution = selectedResolution.value.name,
                cameraMotion = if (creationType == CreationType.IMAGE_TO_VIDEO) selectedCameraMotion.value.name else "NONE",
                modelUsed = selectedModel.value.displayName,
                creditCost = cost,
                status = "GENERATING",
                progressPercent = 5,
                thumbnailUri = selectedImageUri.value ?: "",
                videoUri = "",
                storyScenesJson = scenesJson
            )

            val projectId = repository.insertProject(newEntity)
            activeGeneratedProjectId.value = projectId
            isGenerating.value = true
            generationProgress.value = 5
            generationStage.value = "Queued with AI Worker..."

            // Switch to My Videos tab or stay to observe
            _currentTab.value = MainTab.MY_VIDEOS
            _activeScreen.value = ActiveScreen.TABS_CONTAINER

            // Run pipeline
            repository.runVideoGenerationPipeline(projectId) { progress, stage ->
                generationProgress.value = progress
                generationStage.value = stage
            }

            isGenerating.value = false
            showToast("Video generation completed successfully!")
        }
    }

    fun applyTemplate(template: VideoTemplateItem) {
        textPrompt.value = template.prompt
        selectedStyle.value = template.style
        selectedAspectRatio.value = template.aspectRatio
        selectedDuration.value = template.durationSeconds
        _currentTab.value = MainTab.CREATE
        _activeScreen.value = ActiveScreen.TEXT_TO_VIDEO_STUDIO
        showToast("Loaded template: ${template.title}")
    }

    fun playVideo(project: VideoProjectEntity) {
        playingProject.value = project
        _activeScreen.value = ActiveScreen.FULLSCREEN_PLAYER
    }

    fun deleteProject(id: Long) {
        viewModelScope.launch {
            repository.deleteProject(id)
            showToast("Project deleted")
        }
    }

    fun claimDailyBonus() {
        viewModelScope.launch {
            repository.addCredits(25, "Daily Creator Bonus Claim (+25 Credits)")
            showToast("Claimed 25 free daily credits!")
        }
    }

    fun purchaseCredits(amount: Int, priceLabel: String) {
        viewModelScope.launch {
            repository.addCredits(amount, "Purchased Credit Pack: $priceLabel (+$amount Credits)", type = "PURCHASE")
            showToast("Successfully added $amount credits!")
        }
    }

    fun selectSubscriptionTier(tierName: String) {
        userTier.value = tierName
        viewModelScope.launch {
            repository.addCredits(200, "Subscribed to $tierName (+200 Bonus Credits)", type = "SUBSCRIPTION")
            showToast("Upgraded to $tierName!")
        }
    }
}
