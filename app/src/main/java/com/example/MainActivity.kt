package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.FullscreenPlayerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ImageToVideoStudioScreen
import com.example.ui.screens.MyVideosScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.StoryMakerStudioScreen
import com.example.ui.screens.TextToVideoStudioScreen
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.CineCraftTheme
import com.example.ui.theme.CyanSecondary
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioOutline
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletPrimary
import com.example.viewmodel.ActiveScreen
import com.example.viewmodel.CineCraftViewModel
import com.example.viewmodel.MainTab

class MainActivity : ComponentActivity() {

    private val viewModel: CineCraftViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CineCraftTheme(darkTheme = true) {
                val context = LocalContext.current
                val activeScreen by viewModel.activeScreen.collectAsState()
                val currentTab by viewModel.currentTab.collectAsState()
                val toastMessage by viewModel.toastMessage.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                // Show feedback toasts
                LaunchedEffect(toastMessage) {
                    toastMessage?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        viewModel.clearToast()
                    }
                }

                // Handle back press
                BackHandler(enabled = activeScreen != ActiveScreen.TABS_CONTAINER) {
                    viewModel.navigateBack()
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(StudioBackground),
                    containerColor = StudioBackground,
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    contentWindowInsets = WindowInsets.statusBars,
                    bottomBar = {
                        if (activeScreen == ActiveScreen.TABS_CONTAINER) {
                            StudioBottomNavigationBar(
                                currentTab = currentTab,
                                onTabSelect = { tab ->
                                    if (tab == MainTab.CREATE) {
                                        viewModel.navigateTo(ActiveScreen.TEXT_TO_VIDEO_STUDIO)
                                    } else {
                                        viewModel.setTab(tab)
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (activeScreen) {
                            ActiveScreen.TABS_CONTAINER -> {
                                when (currentTab) {
                                    MainTab.HOME -> HomeScreen(viewModel = viewModel)
                                    MainTab.EXPLORE -> ExploreScreen(viewModel = viewModel)
                                    MainTab.CREATE -> CreateHubLauncher(viewModel = viewModel)
                                    MainTab.MY_VIDEOS -> MyVideosScreen(viewModel = viewModel)
                                    MainTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                                }
                            }
                            ActiveScreen.TEXT_TO_VIDEO_STUDIO -> {
                                TextToVideoStudioScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateBack() }
                                )
                            }
                            ActiveScreen.IMAGE_TO_VIDEO_STUDIO -> {
                                ImageToVideoStudioScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateBack() }
                                )
                            }
                            ActiveScreen.STORY_MAKER_STUDIO -> {
                                StoryMakerStudioScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateBack() }
                                )
                            }
                            ActiveScreen.FULLSCREEN_PLAYER -> {
                                FullscreenPlayerScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateBack() }
                                )
                            }
                            ActiveScreen.CREDIT_VAULT -> {
                                ProfileScreen(
                                    viewModel = viewModel,
                                    modifier = Modifier
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateHubLauncher(
    viewModel: CineCraftViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Choose Creation Studio",
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Select your workflow to produce cinematic AI videos",
            color = TextSecondary,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        CreationModeCard(
            title = "Text-to-Video Studio",
            description = "Craft stunning videos from text prompts with Gemini Magic Enhancement",
            icon = Icons.Default.Videocam,
            accentColor = VioletPrimary,
            onClick = { viewModel.navigateTo(ActiveScreen.TEXT_TO_VIDEO_STUDIO) },
            testTag = "launcher_text_to_video"
        )

        Spacer(modifier = Modifier.height(14.dp))

        CreationModeCard(
            title = "Image-to-Video Animator",
            description = "Animate still portraits and images with 3D camera motion presets",
            icon = Icons.Default.Image,
            accentColor = CyanSecondary,
            onClick = { viewModel.navigateTo(ActiveScreen.IMAGE_TO_VIDEO_STUDIO) },
            testTag = "launcher_image_to_video"
        )

        Spacer(modifier = Modifier.height(14.dp))

        CreationModeCard(
            title = "AI Story-to-Video Maker",
            description = "Turn story ideas into full multi-scene short films with narration and script breakdown",
            icon = Icons.Default.Movie,
            accentColor = Color(0xFFEC4899),
            onClick = { viewModel.navigateTo(ActiveScreen.STORY_MAKER_STUDIO) },
            testTag = "launcher_story_to_video"
        )
    }
}

@Composable
fun CreationModeCard(
    title: String,
    description: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = StudioSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun StudioBottomNavigationBar(
    currentTab: MainTab,
    onTabSelect: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .border(1.dp, StudioOutline, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        containerColor = StudioSurface,
        tonalElevation = 8.dp
    ) {
        // Home
        NavigationBarItem(
            selected = currentTab == MainTab.HOME,
            onClick = { onTabSelect(MainTab.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = VioletPrimary,
                selectedTextColor = VioletPrimary,
                indicatorColor = VioletPrimary.copy(alpha = 0.2f),
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_tab_home")
        )

        // Explore
        NavigationBarItem(
            selected = currentTab == MainTab.EXPLORE,
            onClick = { onTabSelect(MainTab.EXPLORE) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.EXPLORE) Icons.Filled.Explore else Icons.Outlined.Explore,
                    contentDescription = "Explore"
                )
            },
            label = { Text("Explore", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CyanSecondary,
                selectedTextColor = CyanSecondary,
                indicatorColor = CyanSecondary.copy(alpha = 0.2f),
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_tab_explore")
        )

        // Center Create Action
        NavigationBarItem(
            selected = currentTab == MainTab.CREATE,
            onClick = { onTabSelect(MainTab.CREATE) },
            icon = {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(VioletPrimary, CyanSecondary))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Create",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            label = { Text("Create", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedTextColor = VioletPrimary,
                unselectedTextColor = TextMuted,
                indicatorColor = Color.Transparent
            ),
            modifier = Modifier.testTag("nav_tab_create")
        )

        // My Videos
        NavigationBarItem(
            selected = currentTab == MainTab.MY_VIDEOS,
            onClick = { onTabSelect(MainTab.MY_VIDEOS) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.MY_VIDEOS) Icons.Filled.VideoLibrary else Icons.Outlined.VideoLibrary,
                    contentDescription = "My Videos"
                )
            },
            label = { Text("My Videos", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = VioletPrimary,
                selectedTextColor = VioletPrimary,
                indicatorColor = VioletPrimary.copy(alpha = 0.2f),
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_tab_my_videos")
        )

        // Profile
        NavigationBarItem(
            selected = currentTab == MainTab.PROFILE,
            onClick = { onTabSelect(MainTab.PROFILE) },
            icon = {
                Icon(
                    imageVector = if (currentTab == MainTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AmberTertiary,
                selectedTextColor = AmberTertiary,
                indicatorColor = AmberTertiary.copy(alpha = 0.2f),
                unselectedIconColor = TextMuted,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("nav_tab_profile")
        )
    }
}
