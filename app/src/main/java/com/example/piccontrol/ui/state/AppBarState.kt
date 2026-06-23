package com.example.piccontrol.ui.state

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

data class AppBarState(
    val title: (@Composable () -> Unit)? = null,
    val actions: (@Composable RowScope.() -> Unit)? = null
) { companion object }
