package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.VideoProjectEntity
import com.example.ui.components.CinematicVideoPlayerCanvas
import com.example.ui.components.CreditPill
import com.example.ui.components.GradientButton
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.StudioOutline
import com.example.ui.theme.StudioSurfaceElevated
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletPrimary
import com.example.viewmodel.ActiveScreen
import com.example.viewmodel.CineCraftViewModel

@Composable
fun ExploreScreen(
    viewModel: CineCraftViewModel,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Cinematic", "Cyberpunk", "Anime", "Fantasy", "Photoreal", "Vintage")
    val allTemplates = viewModel.templates
    val credits by viewModel.totalCredits.collectAsState()

    val filteredTemplates = allTemplates.filter { template ->
        (selectedCategory == "All" || template.category.equals(selectedCategory, ignoreCase = true)) &&
                (searchQuery.isBlank() || template.title.contains(searchQuery, ignoreCase = true) || template.prompt.contains(searchQuery, ignoreCase = true))
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Explore Templates",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Viral prompts ready to generate in 1 tap",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                CreditPill(credits = credits ?: 0, onClick = { viewModel.navigateTo(ActiveScreen.CREDIT_VAULT) })
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search templates, styles, genres...", color = TextMuted) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextMuted)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("explore_search_bar"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = CyanSecondary,
                    unfocusedBorderColor = StudioOutline,
                    focusedContainerColor = StudioSurfaceVariant,
                    unfocusedContainerColor = StudioSurfaceVariant
                )
            )
        }

        // Category pills
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    val isSel = category == selectedCategory
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSel) CyanSecondary.copy(alpha = 0.25f) else StudioSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) CyanSecondary else StudioOutline),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { selectedCategory = category }
                            .testTag("category_pill_$category")
                    ) {
                        Text(
                            text = category,
                            color = if (isSel) CyanSecondary else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Template List Items
        items(filteredTemplates) { template ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = StudioSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioOutline),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .testTag("explore_item_${template.id}")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(template.previewGradientColors.map { Color(it) })),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
                        ) {
                            Text(
                                text = "${template.category} • ${template.aspectRatio.label}",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White)
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
                        ) {
                            Text(
                                text = "${template.durationSeconds}s • ${template.viewsCount} views",
                                color = Color.White,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = template.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = template.prompt, color = TextSecondary, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(template.tags) { tag ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = StudioSurfaceElevated
                                ) {
                                    Text(
                                        text = "#$tag",
                                        color = TextMuted,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = VioletPrimary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.applyTemplate(template) }
                                .testTag("use_template_button_${template.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Use Template", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun MyVideosScreen(
    viewModel: CineCraftViewModel,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.allProjects.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Completed", "Generating")
    val credits by viewModel.totalCredits.collectAsState()

    val filteredProjects = projects.filter {
        when (selectedFilter) {
            "Completed" -> it.status == "COMPLETED"
            "Generating" -> it.status == "GENERATING"
            else -> true
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Studio Projects",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${projects.size} total videos created",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                CreditPill(credits = credits ?: 0, onClick = { viewModel.navigateTo(ActiveScreen.CREDIT_VAULT) })
            }
        }

        // Filter pills
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                filters.forEach { filter ->
                    val isSel = filter == selectedFilter
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSel) VioletPrimary.copy(alpha = 0.25f) else StudioSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) VioletPrimary else StudioOutline),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedFilter = filter }
                            .testTag("filter_$filter")
                    ) {
                        Text(
                            text = filter,
                            color = if (isSel) VioletPrimary else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        if (filteredProjects.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Videocam, contentDescription = null, tint = TextMuted, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No projects in this category yet", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Start creating from Text, Image, or Story maker", color = TextMuted, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(filteredProjects) { project ->
                VideoProjectCard(
                    project = project,
                    onPlay = { viewModel.playVideo(project) },
                    onDelete = { viewModel.deleteProject(project.id) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun VideoProjectCard(
    project: VideoProjectEntity,
    onPlay: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = StudioSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioOutline),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onPlay)
            .testTag("project_card_${project.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                // Interactive dynamic canvas simulating video content
                CinematicVideoPlayerCanvas(
                    prompt = project.prompt,
                    style = project.style,
                    aspectRatio = project.aspectRatio,
                    isPlaying = project.status == "COMPLETED"
                )

                if (project.status == "GENERATING") {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Black.copy(alpha = 0.8f),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { project.progressPercent / 100f },
                                modifier = Modifier.size(18.dp),
                                color = CyanSecondary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Rendering ${project.progressPercent}%",
                                color = CyanSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play Video", tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.title,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${project.creationType.replace("_", " ")} • ${project.durationSeconds}s • ${project.resolution}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_project_${project.id}")
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun FullscreenPlayerScreen(
    viewModel: CineCraftViewModel,
    onBack: () -> Unit
) {
    val project = viewModel.playingProject.collectAsState().value ?: return
    var isPlaying by remember { mutableStateOf(true) }
    var scrubPosition by remember { mutableFloatStateOf(0.4f) }
    var showPromptInfo by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030712))
            .padding(16.dp)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("player_close_button")) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
            Text(
                text = project.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            )
            IconButton(onClick = { showPromptInfo = !showPromptInfo }) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "Prompt Info", tint = CyanSecondary)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main Video Canvas Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, StudioOutline, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            CinematicVideoPlayerCanvas(
                prompt = project.prompt,
                style = project.style,
                aspectRatio = project.aspectRatio,
                isPlaying = isPlaying,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Prompt Info drawer
        if (showPromptInfo) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = StudioSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Generation Prompt:", color = CyanSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(project.prompt, color = TextPrimary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Model: ${project.modelUsed} • Style: ${project.style} • Ratio: ${project.aspectRatio}", color = TextMuted, fontSize = 10.sp)
                }
            }
        }

        // Timeline scrubber
        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = scrubPosition,
                onValueChange = { scrubPosition = it },
                colors = SliderDefaults.colors(
                    thumbColor = VioletPrimary,
                    activeTrackColor = VioletPrimary,
                    inactiveTrackColor = StudioOutline
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = String.format("00:%02d", (scrubPosition * project.durationSeconds).toInt()),
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = String.format("00:%02d", project.durationSeconds),
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Media controls row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { scrubPosition = 0f }) {
                Icon(imageVector = Icons.Default.Replay, contentDescription = "Restart", tint = Color.White)
            }

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(VioletPrimary)
                    .clickable { isPlaying = !isPlaying },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            IconButton(onClick = { viewModel.showToast("Exporting MP4 to Gallery...") }) {
                Icon(imageVector = Icons.Default.Download, contentDescription = "Download MP4", tint = Color.White)
            }

            IconButton(onClick = { viewModel.showToast("Sharing video link...") }) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ProfileScreen(
    viewModel: CineCraftViewModel,
    modifier: Modifier = Modifier
) {
    val credits by viewModel.totalCredits.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()
    val userTier by viewModel.userTier.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Profile & Studio Vault",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // User Avatar & Plan Card
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = StudioSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioOutline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(VioletPrimary, AmberTertiary))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("JD", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Creator Account", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("jobaj8411@gmail.com", color = TextMuted, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = VioletPrimary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = userTier,
                                color = VioletPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Daily Bonus Claim Card
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E1B4B),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberTertiary),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.claimDailyBonus() }
                    .testTag("claim_daily_bonus_button")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AmberTertiary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = AmberTertiary)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Daily Creator Bonus", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Claim +25 Free credits every 24 hours", color = AmberTertiary, fontSize = 11.sp)
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AmberTertiary
                    ) {
                        Text(
                            text = "Claim",
                            color = Color(0xFF451A03),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Credit Packs Purchase
        item {
            Text("Recharge Studio Credits", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple(100, "$4.99", "Starter"),
                    Triple(350, "$12.99", "Popular"),
                    Triple(1000, "$29.99", "Best Value")
                ).forEach { pack ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = StudioSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioOutline),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { viewModel.purchaseCredits(pack.first, pack.second) }
                            .testTag("credit_pack_${pack.first}")
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(pack.third, color = CyanSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("+${pack.first}", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Black)
                            Text("credits", color = TextMuted, fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = VioletPrimary
                            ) {
                                Text(
                                    text = pack.second,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Subscription Plans
        item {
            Text("Creator Membership Tiers", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(
                    Pair("Pro Creator Plan", "500 Credits/mo • 1080p Export • Faster Queue ($19/mo)"),
                    Pair("Studio VIP Plan", "1,500 Credits/mo • 4K Ultra HD • Commercial License ($49/mo)")
                ).forEach { plan ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = StudioSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioOutline),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { viewModel.selectSubscriptionTier(plan.first) }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(plan.first, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(plan.second, color = TextSecondary, fontSize = 11.sp)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = VioletPrimary.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, VioletPrimary)
                            ) {
                                Text("Subscribe", color = VioletPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                            }
                        }
                    }
                }
            }
        }

        // Recent Credit Transactions History
        if (transactions.isNotEmpty()) {
            item {
                Text("Credit Ledger History", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(6.dp))
            }

            items(transactions.take(6)) { tx ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = StudioSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(tx.description, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text(tx.type, color = TextMuted, fontSize = 10.sp)
                        }
                        Text(
                            text = if (tx.amount > 0) "+${tx.amount}" else "${tx.amount}",
                            color = if (tx.amount > 0) Color(0xFF10B981) else Color(0xFFEF4444),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
