import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import com.gamzecoskun.liquidbottomnavigation.R
import com.gamzecoskun.liquidbottomnavigation.ui.theme.DEFAULT_PADDING
import com.gamzecoskun.liquidbottomnavigation.ui.theme.LiquidBottomNavigationTheme
import kotlin.math.PI
import kotlin.math.sin


@RequiresApi(Build.VERSION_CODES.S)
private fun getRenderEffect(): android.graphics.RenderEffect {
    val blurEffect = android.graphics.RenderEffect.createBlurEffect(80f, 80f, android.graphics.Shader.TileMode.MIRROR)

    val colorMatrix = android.graphics.ColorMatrix().apply {
        setScale(1f, 1f, 1f, 0.2f) // Adjusts alpha only
    }
    val alphaEffect = android.graphics.RenderEffect.createColorFilterEffect(android.graphics.ColorMatrixColorFilter(colorMatrix))

    return android.graphics.RenderEffect.createChainEffect(alphaEffect, blurEffect)
}

@Composable
fun MainScreen() {
    val isMenuExtended = remember { mutableStateOf(false) }

    val fabAnimationProgress by animateFloatAsState(
        targetValue = if (isMenuExtended.value) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearEasing
        )
    )

    val clickAnimationProgress by animateFloatAsState(
        targetValue = if (isMenuExtended.value) 1f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = LinearEasing
        )
    )

    val renderEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getRenderEffect().asComposeRenderEffect()
    } else {
        null
    }

    MainScreen(
        renderEffect = renderEffect,
        fabAnimationProgress = fabAnimationProgress,
        clickAnimationProgress = clickAnimationProgress
    ) {
        isMenuExtended.value = isMenuExtended.value.not()
    }
}

@Composable
fun MainScreen(
    renderEffect: androidx.compose.ui.graphics.RenderEffect?,
    fabAnimationProgress: Float = 0f,
    clickAnimationProgress: Float = 0f,
    toggleAnimation: () -> Unit = { }
) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        CustomBottomNavigation()
        Circle(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            animationProgress = 0.5f
        )

        FabGroup(renderEffect = renderEffect, animationProgress = fabAnimationProgress)
        FabGroup(
            renderEffect = null,
            animationProgress = fabAnimationProgress,
            toggleAnimation = toggleAnimation
        )
        Circle(
            color = Color.White,
            animationProgress = clickAnimationProgress
        )
    }
}

@Composable
fun Circle(color: Color, animationProgress: Float) {
    val animationValue = sin(PI * animationProgress).toFloat()

    Box(
        modifier = Modifier
            .padding(DEFAULT_PADDING.dp)
            .size(56.dp)
            .scale(2 - animationValue)
            .border(
                width = 2.dp,
                color = color.copy(alpha = color.alpha * animationValue),
                shape = CircleShape
            )
    )
}

@Composable
fun CustomBottomNavigation() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(80.dp)
            .paint(
                painter = painterResource(R.drawable.bottom_navigation),
                contentScale = ContentScale.FillHeight
            )
            .padding(horizontal = 40.dp)
    ) {
        listOf(Icons.Filled.Call, Icons.Filled.Favorite).map { image ->
            IconButton(onClick = { }) {
                Icon(imageVector = image, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
fun FabGroup(
    animationProgress: Float = 0f,
    renderEffect: androidx.compose.ui.graphics.RenderEffect? = null,
    toggleAnimation: () -> Unit = { }
) {
    Box(
        Modifier
            .fillMaxSize()
            .graphicsLayer { this.renderEffect = renderEffect }
            .padding(bottom = DEFAULT_PADDING.dp),
        contentAlignment = Alignment.BottomCenter
    ) {

        AnimatedFab(
            icon = Icons.Default.Settings,
            modifier = Modifier.padding(
                bottom = lerp(0.dp, 72.dp, FastOutSlowInEasing.transform(animationProgress)),
                end = lerp(0.dp, 210.dp, FastOutSlowInEasing.transform(animationProgress))
            ),
            opacity = lerp(0.2f, 0.7f, LinearEasing.transform(animationProgress))
        )

        AnimatedFab(
            icon = Icons.Default.Settings,
            modifier = Modifier.padding(
                bottom = lerp(0.dp, 88.dp, FastOutSlowInEasing.transform(mapProgress(animationProgress, 0.1f, 0.9f)))
            ),
            opacity = lerp(0.3f, 0.8f, LinearEasing.transform(mapProgress(animationProgress, 0.3f, 0.8f)))
        )

        AnimatedFab(
            icon = Icons.Default.ShoppingCart,
            modifier = Modifier.padding(
                bottom = lerp(0.dp, 72.dp, FastOutSlowInEasing.transform(mapProgress(animationProgress, 0.2f, 1.0f))),
                start = lerp(0.dp, 210.dp, FastOutSlowInEasing.transform(mapProgress(animationProgress, 0.2f, 1.0f)))
            ),
            opacity = lerp(0.4f, 0.9f, LinearEasing.transform(mapProgress(animationProgress, 0.4f, 0.9f)))
        )

        AnimatedFab(
            modifier = Modifier
                .scale(lerp(1f, 0.15f, LinearEasing.transform(mapProgress(animationProgress, 0.5f, 0.85f))))
        )

        AnimatedFab(
            icon = Icons.Default.Add,
            modifier = Modifier
                .rotate(
                    lerp(0f, 225f, FastOutSlowInEasing.transform(mapProgress(animationProgress, 0.35f, 0.65f)))
                ),
            onClick = toggleAnimation,
            backgroundColor = Color.Transparent
        )
    }
}

private fun mapProgress(
    progress: Float,
    start: Float,
    end: Float
): Float {
    return ((progress - start) / (end - start)).coerceIn(0f, 1f)
}

@Composable
fun AnimatedFab(
    modifier: Modifier,
    icon: ImageVector? = null,
    opacity: Float = 1f,
    backgroundColor: Color = MaterialTheme.colorScheme.secondary,
    onClick: () -> Unit = {}
) {
    FloatingActionButton(
        onClick = onClick,
        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
        containerColor = backgroundColor,
        modifier = modifier.scale(1.25f)
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = Color.White.copy(alpha = opacity)
            )
        }
    }
}


@Composable
@Preview(device = "id:pixel_4a", showBackground = true, backgroundColor = 0xFF3A2F6E)
private fun MainScreenPreview() {
    LiquidBottomNavigationTheme {
        MainScreen()
    }
}