package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CameraMotion
import com.example.model.CreationType
import com.example.model.VideoAspectRatio
import com.example.model.VideoModelProvider
import com.example.model.VideoResolution
import com.example.model.VideoStyle
import com.example.ui.components.AspectRatioSelector
import com.example.ui.components.CameraMotionGrid
import com.example.ui.components.CreditPill
import com.example.ui.components.GradientButton
import com.example.ui.components.StyleChipsRow
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.StudioOutline
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletPrimary
import com.example.viewmodel.ActiveScreen
import com.example.viewmodel.CineCraftViewModel

@Composable
fun TextToVideoStudioScreen(
    viewModel: CineCraftViewModel,
    onBack: () -> Unit
) {
    val prompt by viewModel.textPrompt.collectAsState()
    val negativePrompt by viewModel.negativePrompt.collectAsState()
    val selectedStyle by viewModel.selectedStyle.collectAsState()
    val selectedAspectRatio by viewModel.selectedAspectRatio.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val selectedDuration by viewModel.selectedDuration.collectAsState()
    val selectedResolution by viewModel.selectedResolution.collectAsState()
    val seed by viewModel.promptSeed.collectAsState()
    val isEnhancing by viewModel.isEnhancingPrompt.collectAsState()
    val credits by viewModel.totalCredits.collectAsState()

    var showNegativePrompt by remember { mutableStateOf(false) }
    var showAdvancedSettings by remember { mutableStateOf(false) }

    val examplePrompts = listOf(
        "Cyberpunk neon street at midnight in the rain, hyperrealistic reflections",
        "Majestic golden eagle soaring above snowy alpine mountain peaks at sunrise",
        "Futuristic bioluminescent alien jungle with floating glowing spores",
        "Studio Ghibli style train crossing a calm ocean on crystal tracks at dusk"
    )

    val estimatedCost = viewModel.getEstimatedCreditCost(CreationType.TEXT_TO_VIDEO)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("text_studio_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Text(
                        text = "Text to Video Studio",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                CreditPill(
                    credits = credits ?: 0,
                    onClick = { viewModel.navigateTo(ActiveScreen.CREDIT_VAULT) }
                )
            }
        }

        // Prompt input area
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Prompt Input",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Gemini Magic Enhance Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = VioletPrimary.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VioletPrimary),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(enabled = !isEnhancing) { viewModel.enhanceCurrentPrompt() }
                            .testTag("magic_enhance_prompt_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            if (isEnhancing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = CyanSecondary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Enhancing...", color = CyanSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Magic Enhance",
                                    tint = CyanSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Magic Enhance", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = prompt,
                    onValueChange = { viewModel.textPrompt.value = it },
                    placeholder = { Text("Describe the cinematic scene in detail...", color = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("prompt_text_field"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = VioletPrimary,
                        unfocusedBorderColor = StudioOutline,
                        focusedContainerColor = StudioSurfaceVariant,
                        unfocusedContainerColor = StudioSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Example prompt suggestions
                Text(text = "Try an inspiration:", color = TextMuted, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(examplePrompts) { example ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = StudioSurfaceElevated,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.textPrompt.value = example }
                        ) {
                            Text(
                                text = example.take(28) + "...",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Style Selector
        item {
            Text(text = "Visual Aesthetic & Style", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            StyleChipsRow(
                selectedStyle = selectedStyle,
                onSelect = { viewModel.selectedStyle.value = it }
            )
        }

        // Aspect Ratio Selector
        item {
            Text(text = "Aspect Ratio", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            AspectRatioSelector(
                selectedRatio = selectedAspectRatio,
                onSelect = { viewModel.selectedAspectRatio.value = it }
            )
        }

        // AI Video Model Selector
        item {
            Text(text = "AI Model Engine", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VideoModelProvider.entries.forEach { model ->
                    val isSelected = model == selectedModel
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) VioletPrimary.copy(alpha = 0.25f) else StudioSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) VioletPrimary else StudioOutline
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.selectedModel.value = model }
                            .testTag("model_selector_${model.tag}")
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = model.displayName,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${model.baseCost} pts",
                                color = AmberTertiary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Duration & Resolution
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Duration
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Duration", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(4, 6, 8, 10).forEach { dur ->
                            val isSel = dur == selectedDuration
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) CyanSecondary.copy(alpha = 0.2f) else StudioSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) CyanSecondary else StudioOutline),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.selectedDuration.value = dur }
                            ) {
                                Text(
                                    text = "${dur}s",
                                    color = if (isSel) CyanSecondary else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Resolution
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Resolution", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        VideoResolution.entries.forEach { res ->
                            val isSel = res == selectedResolution
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) VioletPrimary.copy(alpha = 0.2f) else StudioSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) VioletPrimary else StudioOutline),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.selectedResolution.value = res }
                            ) {
                                Text(
                                    text = res.label.take(4),
                                    color = if (isSel) VioletPrimary else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // Advanced Settings & Negative Prompt toggle
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = StudioSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAdvancedSettings = !showAdvancedSettings }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Advanced Parameters (Seed & Negative Prompt)",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (showAdvancedSettings) "▲ Hide" else "▼ Expand",
                        color = VioletPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            AnimatedVisibility(visible = showAdvancedSettings) {
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = negativePrompt,
                        onValueChange = { viewModel.negativePrompt.value = it },
                        label = { Text("Negative Prompt (Elements to avoid)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = VioletPrimary,
                            unfocusedBorderColor = StudioOutline
                        )
                    )

                    OutlinedTextField(
                        value = seed,
                        onValueChange = { viewModel.promptSeed.value = it },
                        label = { Text("Random Seed (Optional numeric seed)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = VioletPrimary,
                            unfocusedBorderColor = StudioOutline
                        )
                    )
                }
            }
        }

        // Generate Video Action Button
        item {
            GradientButton(
                text = "Generate AI Video",
                onClick = { viewModel.startVideoGeneration(CreationType.TEXT_TO_VIDEO) },
                creditCost = estimatedCost,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                },
                testTag = "generate_text_to_video_button"
            )
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ImageToVideoStudioScreen(
    viewModel: CineCraftViewModel,
    onBack: () -> Unit
) {
    val prompt by viewModel.imagePrompt.collectAsState()
    val selectedCameraMotion by viewModel.selectedCameraMotion.collectAsState()
    val motionIntensity by viewModel.motionIntensity.collectAsState()
    val firstAndLastFrame by viewModel.firstAndLastFrame.collectAsState()
    val credits by viewModel.totalCredits.collectAsState()

    val sampleImages = listOf(
        Pair("Cyber Samurai", 0xFF1E1B4B),
        Pair("Fantasy Castle", 0xFF064E3B),
        Pair("Neon Supercar", 0xFF451A03),
        Pair("Cosmic Astronaut", 0xFF311042)
    )

    var selectedSampleIndex by remember { mutableStateOf(0) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Text(
                        text = "Image to Video Studio",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                CreditPill(
                    credits = credits ?: 0,
                    onClick = { viewModel.navigateTo(ActiveScreen.CREDIT_VAULT) }
                )
            }
        }

        // Image Selector / Preview
        item {
            Text(text = "Source Image", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(sampleImages[selectedSampleIndex].second))
                    .border(1.5.dp, CyanSecondary, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Selected Photo",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = sampleImages[selectedSampleIndex].first,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Tap presets below to switch source",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sample image selectors
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(sampleImages.indices.toList()) { index ->
                    val isSel = index == selectedSampleIndex
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(sampleImages[index].second),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSel) CyanSecondary else StudioOutline
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedSampleIndex = index }
                    ) {
                        Text(
                            text = sampleImages[index].first,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Motion prompt
        item {
            Text(text = "Motion Prompt & Animation Intent", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = prompt,
                onValueChange = { viewModel.imagePrompt.value = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = CyanSecondary,
                    unfocusedBorderColor = StudioOutline
                )
            )
        }

        // Camera Motion Presets
        item {
            Text(text = "Camera Movement Presets", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            CameraMotionGrid(
                selectedMotion = selectedCameraMotion,
                onSelect = { viewModel.selectedCameraMotion.value = it }
            )
        }

        // Motion Intensity Slider
        item {
            Text(
                text = "Motion Strength: ${(motionIntensity * 100).toInt()}%",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Slider(
                value = motionIntensity,
                onValueChange = { viewModel.motionIntensity.value = it },
                valueRange = 0.1f..1.0f,
                colors = SliderDefaults.colors(
                    thumbColor = CyanSecondary,
                    activeTrackColor = CyanSecondary,
                    inactiveTrackColor = StudioOutline
                )
            )
        }

        // First/Last Frame option
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Seamless Loop Mode", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Matches end frame back to initial frame for perfect looping", color = TextMuted, fontSize = 11.sp)
                }
                Switch(
                    checked = firstAndLastFrame,
                    onCheckedChange = { viewModel.firstAndLastFrame.value = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = CyanSecondary)
                )
            }
        }

        // Action button
        item {
            GradientButton(
                text = "Animate Image with AI",
                onClick = { viewModel.startVideoGeneration(CreationType.IMAGE_TO_VIDEO) },
                creditCost = viewModel.getEstimatedCreditCost(CreationType.IMAGE_TO_VIDEO),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                },
                testTag = "generate_image_to_video_button"
            )
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun StoryMakerStudioScreen(
    viewModel: CineCraftViewModel,
    onBack: () -> Unit
) {
    val storyIdea by viewModel.storyIdea.collectAsState()
    val storyGenre by viewModel.storyGenre.collectAsState()
    val storyVoice by viewModel.storyVoice.collectAsState()
    val backgroundMusic by viewModel.backgroundMusic.collectAsState()
    val scenes by viewModel.storyScenes.collectAsState()
    val isGeneratingScenes by viewModel.isGeneratingScenes.collectAsState()
    val credits by viewModel.totalCredits.collectAsState()

    val genres = listOf("Sci-Fi Thriller", "Cyberpunk", "Epic Fantasy", "Documentary", "Mystery Noir", "Anime Lore")
    val voices = listOf("Deep Cinematic Male (Kore)", "Warm Narrative Female (Puck)", "British Storyteller", "Epic Announcer")
    val musics = listOf("Epic Orchestral", "Cyberpunk Synthwave", "Atmospheric Ambient", "Heroic Brass")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Text(
                        text = "AI Story-to-Video Studio",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                CreditPill(
                    credits = credits ?: 0,
                    onClick = { viewModel.navigateTo(ActiveScreen.CREDIT_VAULT) }
                )
            }
        }

        // Story idea input
        item {
            Text(text = "Story Idea or Plot Concept", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = storyIdea,
                onValueChange = { viewModel.storyIdea.value = it },
                placeholder = { Text("Enter your plot idea or premise...", color = TextMuted) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .testTag("story_idea_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = Color(0xFFEC4899),
                    unfocusedBorderColor = StudioOutline
                )
            )
        }

        // Genre Chips
        item {
            Text(text = "Story Genre", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(genres) { genre ->
                    val isSel = genre == storyGenre
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSel) Color(0xFFEC4899).copy(alpha = 0.25f) else StudioSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) Color(0xFFEC4899) else StudioOutline),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { viewModel.storyGenre.value = genre }
                    ) {
                        Text(
                            text = genre,
                            color = if (isSel) Color.White else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Voice and Music Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Voice Narrator
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StudioSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioOutline),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.RecordVoiceOver, contentDescription = null, tint = AmberTertiary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Narration Voice", color = TextMuted, fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(storyVoice, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Music
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StudioSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioOutline),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.MusicNote, contentDescription = null, tint = CyanSecondary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Soundtrack", color = TextMuted, fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(backgroundMusic, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Button to Generate Script with Gemini
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF1E1B4B),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEC4899)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isGeneratingScenes) { viewModel.generateStoryBreakdown() }
                    .testTag("generate_story_script_button")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isGeneratingScenes) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color(0xFFEC4899), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Writing Screenplay with Gemini AI...", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    } else {
                        Icon(imageVector = Icons.Default.Movie, contentDescription = null, tint = Color(0xFFEC4899))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (scenes.isEmpty()) "Generate Multi-Scene Script with Gemini" else "Regenerate Full Script",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Rendered Scene Breakdown Timeline
        if (scenes.isNotEmpty()) {
            item {
                Text(
                    text = "Screenplay Scenes (${scenes.size} Scenes)",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(scenes) { scene ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = StudioSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioOutline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("scene_card_${scene.sceneNumber}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = VioletPrimary.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "SCENE 0${scene.sceneNumber} • ${scene.durationSeconds}s",
                                    color = VioletPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }

                            // Regenerate Scene Button
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { viewModel.regenerateScene(scene.sceneNumber) }
                                    .testTag("regenerate_scene_${scene.sceneNumber}")
                            ) {
                                if (scene.isRegenerating) {
                                    CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 1.5.dp, color = CyanSecondary)
                                } else {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Regenerate", tint = CyanSecondary, modifier = Modifier.size(14.dp))
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Refine", color = CyanSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Narration Script:", color = AmberTertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(scene.narration, color = TextPrimary, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Visual Direction:", color = CyanSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(scene.visualDescription, color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }

            // Master Render Film Button
            item {
                GradientButton(
                    text = "Render Complete Story Film",
                    onClick = { viewModel.startVideoGeneration(CreationType.STORY_TO_VIDEO) },
                    creditCost = viewModel.getEstimatedCreditCost(CreationType.STORY_TO_VIDEO),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Movie, contentDescription = null, tint = Color.White)
                    },
                    testTag = "render_story_film_button"
                )
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
