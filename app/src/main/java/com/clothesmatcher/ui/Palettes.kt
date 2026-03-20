package com.clothesmatcher.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

@Immutable
data class ColorPalette(
    val id: String,
    val name: String,
    val description: String,
    val colors: List<Color>,
    val isSpicy: Boolean = false
)

object CategoryOrder {
    private val order = listOf("shirts", "trousers", "shoes", "accessories")
    
    fun sort(categories: List<String>): List<String> {
        return categories.sortedBy { order.indexOf(it) }
    }
}

object ContrastRules {
    /**
     * Returns an appropriate background color for an icon with the given [iconColor].
     */
    fun getBackgroundColorForIcon(iconColor: Color): Color {
        val luminance = iconColor.luminance()
        return if (luminance > 0.45f) {
            // A refined dark charcoal instead of pure black. 
            // Better for modern UI/UX as it feels less "heavy".
            Color(0xFF2D2D2D) 
        } else {
            // A soft off-white/cool grey that's easier on the eyes than pure white.
            Color(0xFFF8F9FA) 
        }
    }

    /**
     * Returns an appropriate border color for an icon with the given [iconColor].
     */
    fun getBorderColorForIcon(iconColor: Color): Color {
        val luminance = iconColor.luminance()
        return if (luminance > 0.45f) {
            Color(0xFF424242) // Subtle border for dark background
        } else {
            Color(0xFFE9ECEF) // Subtle border for light background
        }
    }
}

object Palettes {
    val all = listOf(
        ColorPalette("timeless_ivy", "Timeless Ivy", "Navy shirt, khaki trousers, and dark brown leather.", listOf(Color(0xFF1B263B), Color(0xFFC3B091), Color(0xFF5D4037), Color(0xFF3E2723))),
        ColorPalette("monochrome_office", "Monochrome Office", "Crisp white shirt with professional grey and black.", listOf(Color(0xFFFFFFFF), Color(0xFF808080), Color(0xFF000000), Color(0xFF1A1A1A))),
        ColorPalette("executive_blue", "Executive Blue", "Light blue and navy paired with rich cognac leather.", listOf(Color(0xFFADD8E6), Color(0xFF000080), Color(0xFF4E342E), Color(0xFF8B4513))),
        ColorPalette("urban_rugged", "Urban Rugged", "Olive green and black for a sharp, utilitarian look.", listOf(Color(0xFF556B2F), Color(0xFF121212), Color(0xFFBDBDBD), Color(0xFF000000))),
        ColorPalette("earthy_casual", "Earthy Casual", "Cream top and olive trousers with warm tan accents.", listOf(Color(0xFFFAF9F6), Color(0xFF4B5320), Color(0xFFD2B48C), Color(0xFF5D4037))),
        ColorPalette("modern_minimal", "Modern Minimal", "Charcoal and light gray anchored by clean white shoes.", listOf(Color(0xFF333333), Color(0xFFD3D3D3), Color(0xFF000000), Color(0xFFF5F5F5))),
        ColorPalette("evening_smart", "Evening Smart", "Deep burgundy and midnight navy for a sophisticated night out.", listOf(Color(0xFF800020), Color(0xFF0D1B2A), Color(0xFF1A1A1A), Color(0xFF000000))),
        ColorPalette("weekend_clean", "Weekend Clean", "Light gray and deep indigo denim with classic white leather.", listOf(Color(0xFFE0E0E0), Color(0xFF1A237E), Color(0xFF6D4C41), Color(0xFFFFFFFF))),
        ColorPalette("cyber_street", "Cyber Street", "High-contrast neon and deep purples.", listOf(Color(0xFFCCFF00), Color(0xFF301934), Color(0xFF000000), Color(0xFF1A1A1A)), isSpicy = true),
        ColorPalette("sunset_fire", "Sunset Fire", "Bold pink and orange highlights over a black base.", listOf(Color(0xFFFF69B4), Color(0xFFFF8C00), Color(0xFF000000), Color(0xFFFFFFFF)), isSpicy = true)
    )

    fun getById(id: String): ColorPalette? = all.find { it.id == id }
}
