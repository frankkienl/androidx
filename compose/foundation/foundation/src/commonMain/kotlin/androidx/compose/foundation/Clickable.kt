/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.foundation

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.gesture.doubleTapGestureFilter
import androidx.compose.ui.gesture.longPressGestureFilter
import androidx.compose.ui.gesture.pressIndicatorGestureFilter
import androidx.compose.ui.gesture.tapGestureFilter
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics

/**
 * Configure component to receive clicks via input or accessibility "click" event.
 *
 * Add this modifier to the element to make it clickable within its bounds.
 *
 * This version has no [InteractionState] or [Indication] parameters, default indication from
 * [LocalIndication] will be used. To specify [InteractionState] or [Indication], use another
 * overload.
 *
 * @sample androidx.compose.foundation.samples.ClickableSample
 *
 * @param enabled Controls the enabled state. When `false`, [onClick], [onLongClick] or
 * [onDoubleClick] won't be invoked
 * @param onClickLabel semantic / accessibility label for the [onClick] action
 * @param role the type of user interface element. Accessibility services might use this
 * to describe the element or do customizations
 * @param onLongClick will be called when user long presses on the element
 * @param onDoubleClick will be called when user double clicks on the element
 * @param onClick will be called when user clicks on the element
 */
fun Modifier.clickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onLongClickLabel: String? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    onClick: () -> Unit
) = composed(
    inspectorInfo = debugInspectorInfo {
        name = "clickable"
        properties["enabled"] = enabled
        properties["onClickLabel"] = onClickLabel
        properties["role"] = role
        properties["onClick"] = onClick
        properties["onDoubleClick"] = onDoubleClick
        properties["onLongClick"] = onLongClick
        properties["onLongClickLabel"] = onLongClickLabel
    }
) {
    Modifier.clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        onLongClickLabel = onLongClickLabel,
        onLongClick = onLongClick,
        onDoubleClick = onDoubleClick,
        onClick = onClick,
        role = role,
        indication = LocalIndication.current(),
        interactionState = remember { InteractionState() }
    )
}

/**
 * Configure component to receive clicks via input or accessibility "click" event.
 *
 * Add this modifier to the element to make it clickable within its bounds.
 *
 * @sample androidx.compose.foundation.samples.ClickableSample
 *
 * @param enabled Controls the enabled state. When `false`, [onClick], [onLongClick] or
 * [onDoubleClick] won't be invoked
 * @param interactionState [InteractionState] that will be updated when this Clickable is
 * pressed, using [Interaction.Pressed]. Only initial (first) press will be recorded and added to
 * [InteractionState]
 * @param indication indication to be shown when modified element is pressed. Be default,
 * indication from [LocalIndication] will be used. Pass `null` to show no indication, or
 * current value from [LocalIndication] to show theme default
 * @param onClickLabel semantic / accessibility label for the [onClick] action
 * @param role the type of user interface element. Accessibility services might use this
 * to describe the element or do customizations
 * @param onLongClick will be called when user long presses on the element
 * @param onDoubleClick will be called when user double clicks on the element
 * @param onClick will be called when user clicks on the element
 */
@Suppress("DEPRECATION")
fun Modifier.clickable(
    enabled: Boolean = true,
    interactionState: InteractionState,
    indication: Indication?,
    onClickLabel: String? = null,
    role: Role? = null,
    onLongClickLabel: String? = null,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    onClick: () -> Unit
) = composed(
    factory = {
        val semanticModifier = Modifier.semantics(mergeDescendants = true) {
            if (role != null) {
                this.role = role
            }
            // b/156468846:  add long click semantics and double click if needed
            onClick(action = { onClick(); true }, label = onClickLabel)
            if (onLongClick != null) {
                onLongClick(action = { onLongClick(); true }, label = onLongClickLabel)
            }
            if (!enabled) {
                disabled()
            }
        }
        val interactionUpdate =
            if (enabled) {
                Modifier.pressIndicatorGestureFilter(
                    onStart = { interactionState.addInteraction(Interaction.Pressed, it) },
                    onStop = { interactionState.removeInteraction(Interaction.Pressed) },
                    onCancel = { interactionState.removeInteraction(Interaction.Pressed) }
                )
            } else {
                Modifier
            }
        val tap = if (enabled) tapGestureFilter(onTap = { onClick() }) else Modifier
        val longTap = if (enabled && onLongClick != null) {
            longPressGestureFilter(onLongPress = { onLongClick() })
        } else {
            Modifier
        }
        val doubleTap =
            if (enabled && onDoubleClick != null) {
                doubleTapGestureFilter(onDoubleTap = { onDoubleClick() })
            } else {
                Modifier
            }
        DisposableEffect(interactionState) {
            onDispose {
                interactionState.removeInteraction(Interaction.Pressed)
            }
        }
        semanticModifier
            .then(interactionUpdate)
            .indication(interactionState, indication)
            .then(tap)
            .then(longTap)
            .then(doubleTap)
    },
    inspectorInfo = debugInspectorInfo {
        name = "clickable"
        properties["enabled"] = enabled
        properties["onClickLabel"] = onClickLabel
        properties["role"] = role
        properties["onClick"] = onClick
        properties["onDoubleClick"] = onDoubleClick
        properties["onLongClick"] = onLongClick
        properties["onLongClickLabel"] = onLongClickLabel
        properties["indication"] = indication
        properties["interactionState"] = interactionState
    }
)