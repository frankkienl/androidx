/*
 * Copyright 2020 The Android Open Source Project
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

package androidx.compose.material

import androidx.compose.animation.core.ManualAnimationClock
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertTopPositionInRootIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalMaterialApi::class)
class ModalBottomSheetTest {

    @get:Rule
    val rule = createComposeRule()

    private val sheetHeight = 256.dp
    private val sheetTag = "sheetContentTag"

    private lateinit var clock: ManualAnimationClock

    private fun advanceClock() {
        clock.clockTimeMillis += 100000L
    }

    @Before
    fun init() {
        clock = ManualAnimationClock(initTimeMillis = 0L)
    }

    @Test
    fun modalBottomSheet_testOffset_whenHidden() {
        rule.setMaterialContent {
            ModalBottomSheetLayout(
                sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden),
                content = {},
                sheetContent = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .preferredHeight(sheetHeight)
                            .testTag(sheetTag)
                    )
                }
            )
        }

        val height = rule.rootHeight()
        rule.onNodeWithTag(sheetTag)
            .assertTopPositionInRootIsEqualTo(height)
    }

    @Test
    fun modalBottomSheet_testOffset_whenExpanded() {
        rule.setMaterialContent {
            ModalBottomSheetLayout(
                sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Expanded),
                content = {},
                sheetContent = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .preferredHeight(sheetHeight)
                            .testTag(sheetTag)
                    )
                }
            )
        }

        val height = rule.rootHeight()
        rule.onNodeWithTag(sheetTag)
            .assertTopPositionInRootIsEqualTo(height - sheetHeight)
    }

    @Test
    fun modalBottomSheet_testDismissAction_whenExpanded() {
        val sheetState = ModalBottomSheetState(ModalBottomSheetValue.Expanded, clock)
        rule.setMaterialContent {
            ModalBottomSheetLayout(
                sheetState = sheetState,
                content = {},
                sheetContent = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .preferredHeight(sheetHeight)
                            .testTag(sheetTag)
                    )
                }
            )
        }

        val height = rule.rootHeight()
        rule.onNodeWithTag(sheetTag).onParent()
            .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.Collapse))
            .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.Expand))
            .assert(SemanticsMatcher.keyIsDefined(SemanticsActions.Dismiss))
            .performSemanticsAction(SemanticsActions.Dismiss)

        advanceClock()

        rule.onNodeWithTag(sheetTag)
            .assertTopPositionInRootIsEqualTo(height)
    }

    @Test
    fun modalBottomSheet_testOffset_tallBottomSheet_whenHidden() {
        rule.setMaterialContent {
            ModalBottomSheetLayout(
                sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden),
                content = {},
                sheetContent = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .testTag(sheetTag)
                    )
                }
            )
        }

        val height = rule.rootHeight()
        rule.onNodeWithTag(sheetTag)
            .assertTopPositionInRootIsEqualTo(height)
    }

    @Test
    fun modalBottomSheet_testOffset_tallBottomSheet_whenExpanded() {
        rule.setMaterialContent {
            ModalBottomSheetLayout(
                sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Expanded),
                content = {},
                sheetContent = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .testTag(sheetTag)
                    )
                }
            )
        }

        rule.onNodeWithTag(sheetTag)
            .assertTopPositionInRootIsEqualTo(0.dp)
    }

    @Test
    fun modalBottomSheet_testCollapseAction_tallBottomSheet_whenExpanded() {
        val sheetState = ModalBottomSheetState(ModalBottomSheetValue.Expanded, clock)
        rule.setMaterialContent {
            ModalBottomSheetLayout(
                sheetState = sheetState,
                content = {},
                sheetContent = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .testTag(sheetTag)
                    )
                }
            )
        }

        val height = rule.rootHeight()
        rule.onNodeWithTag(sheetTag).onParent()
            .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.Expand))
            .assert(SemanticsMatcher.keyIsDefined(SemanticsActions.Collapse))
            .assert(SemanticsMatcher.keyIsDefined(SemanticsActions.Dismiss))
            .performSemanticsAction(SemanticsActions.Collapse)

        advanceClock()

        rule.onNodeWithTag(sheetTag)
            .assertTopPositionInRootIsEqualTo(height / 2)
    }

    @Test
    fun modalBottomSheet_testDismissAction_tallBottomSheet_whenExpanded() {
        val sheetState = ModalBottomSheetState(ModalBottomSheetValue.Expanded, clock)
        rule.setMaterialContent {
            ModalBottomSheetLayout(
                sheetState = sheetState,
                content = {},
                sheetContent = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .testTag(sheetTag)
                    )
                }
            )
        }

        val height = rule.rootHeight()
        rule.onNodeWithTag(sheetTag).onParent()
            .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.Expand))
            .assert(SemanticsMatcher.keyIsDefined(SemanticsActions.Collapse))
            .assert(SemanticsMatcher.keyIsDefined(SemanticsActions.Dismiss))
            .performSemanticsAction(SemanticsActions.Dismiss)

        advanceClock()

        rule.onNodeWithTag(sheetTag)
            .assertTopPositionInRootIsEqualTo(height)
    }

    @Test
    fun modalBottomSheet_testOffset_tallBottomSheet_whenHalfExpanded() {
        rule.setMaterialContent {
            ModalBottomSheetLayout(
                sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.HalfExpanded),
                content = {},
                sheetContent = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .testTag(sheetTag)
                    )
                }
            )
        }

        val height = rule.rootHeight()
        rule.onNodeWithTag(sheetTag)
            .assertTopPositionInRootIsEqualTo(height / 2)
    }

    @Test
    fun modalBottomSheet_testExpandAction_tallBottomSheet_whenHalfExpanded() {
        val sheetState = ModalBottomSheetState(ModalBottomSheetValue.HalfExpanded, clock)
        rule.setMaterialContent {
            ModalBottomSheetLayout(
                sheetState = sheetState,
                content = {},
                sheetContent = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .testTag(sheetTag)
                    )
                }
            )
        }

        rule.onNodeWithTag(sheetTag).onParent()
            .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.Collapse))
            .assert(SemanticsMatcher.keyIsDefined(SemanticsActions.Expand))
            .assert(SemanticsMatcher.keyIsDefined(SemanticsActions.Dismiss))
            .performSemanticsAction(SemanticsActions.Expand)

        advanceClock()

        rule.onNodeWithTag(sheetTag)
            .assertTopPositionInRootIsEqualTo(0.dp)
    }

    @Test
    fun modalBottomSheet_testDismissAction_tallBottomSheet_whenHalfExpanded() {
        val sheetState = ModalBottomSheetState(ModalBottomSheetValue.HalfExpanded, clock)
        rule.setMaterialContent {
            ModalBottomSheetLayout(
                sheetState = sheetState,
                content = {},
                sheetContent = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .testTag(sheetTag)
                    )
                }
            )
        }

        val height = rule.rootHeight()
        rule.onNodeWithTag(sheetTag).onParent()
            .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.Collapse))
            .assert(SemanticsMatcher.keyIsDefined(SemanticsActions.Expand))
            .assert(SemanticsMatcher.keyIsDefined(SemanticsActions.Dismiss))
            .performSemanticsAction(SemanticsActions.Dismiss)

        advanceClock()

        rule.onNodeWithTag(sheetTag)
            .assertTopPositionInRootIsEqualTo(height)
    }

    @Test
    fun modalBottomSheet_showAndHide_manually() {
        val sheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden, clock)
        rule.setMaterialContent {
            ModalBottomSheetLayout(
                sheetState = sheetState,
                content = {},
                sheetContent = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .preferredHeight(sheetHeight)
                            .testTag(sheetTag)
                    )
                }
            )
        }

        val height = rule.rootHeight()

        rule.onNodeWithTag(sheetTag)
            .assertTopPositionInRootIsEqualTo(height)

        rule.runOnIdle {
            sheetState.show()
        }

        advanceClock()

        rule.onNodeWithTag(sheetTag)
            .assertTopPositionInRootIsEqualTo(height - sheetHeight)

        rule.runOnIdle {
            sheetState.hide()
        }

        advanceClock()

        rule.onNodeWithTag(sheetTag)
            .assertTopPositionInRootIsEqualTo(height)
    }

    @Test
    fun modalBottomSheet_showAndHide_manually_tallBottomSheet() {
        val sheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden, clock)
        rule.setMaterialContent {
            ModalBottomSheetLayout(
                sheetState = sheetState,
                content = {},
                sheetContent = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .testTag(sheetTag)
                    )
                }
            )
        }

        val height = rule.rootHeight()

        rule.onNodeWithTag(sheetTag)
            .assertTopPositionInRootIsEqualTo(height)

        rule.runOnIdle {
            sheetState.show()
        }

        advanceClock()

        rule.onNodeWithTag(sheetTag)
            .assertTopPositionInRootIsEqualTo(height / 2)

        rule.runOnIdle {
            sheetState.hide()
        }

        advanceClock()

        rule.onNodeWithTag(sheetTag)
            .assertTopPositionInRootIsEqualTo(height)
    }

    @Test
    fun modalBottomSheet_hideBySwiping() {
        val sheetState = ModalBottomSheetState(ModalBottomSheetValue.Expanded, clock)
        rule.setMaterialContent {
            ModalBottomSheetLayout(
                sheetState = sheetState,
                content = {},
                sheetContent = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .preferredHeight(sheetHeight)
                            .testTag(sheetTag)
                    )
                }
            )
        }

        rule.runOnIdle {
            assertThat(sheetState.value).isEqualTo(ModalBottomSheetValue.Expanded)
        }

        rule.onNodeWithTag(sheetTag)
            .performGesture { swipeDown() }

        advanceClock()

        rule.runOnIdle {
            assertThat(sheetState.value).isEqualTo(ModalBottomSheetValue.Hidden)
        }
    }

    @Test
    fun modalBottomSheet_hideBySwiping_tallBottomSheet() {
        val sheetState = ModalBottomSheetState(ModalBottomSheetValue.Expanded, clock)
        rule.setMaterialContent {
            ModalBottomSheetLayout(
                sheetState = sheetState,
                content = {},
                sheetContent = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .testTag(sheetTag)
                    )
                }
            )
        }

        rule.runOnIdle {
            assertThat(sheetState.value).isEqualTo(ModalBottomSheetValue.Expanded)
        }

        rule.onNodeWithTag(sheetTag)
            .performGesture { swipeDown() }

        advanceClock()

        rule.runOnIdle {
            assertThat(sheetState.value).isEqualTo(ModalBottomSheetValue.Hidden)
        }
    }

    @Test
    fun modalBottomSheet_expandBySwiping() {
        val sheetState = ModalBottomSheetState(ModalBottomSheetValue.HalfExpanded, clock)
        rule.setMaterialContent {
            ModalBottomSheetLayout(
                sheetState = sheetState,
                content = {},
                sheetContent = {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .testTag(sheetTag)
                    )
                }
            )
        }

        rule.runOnIdle {
            assertThat(sheetState.value).isEqualTo(ModalBottomSheetValue.HalfExpanded)
        }

        rule.onNodeWithTag(sheetTag)
            .performGesture { swipeUp() }

        advanceClock()

        rule.runOnIdle {
            assertThat(sheetState.value).isEqualTo(ModalBottomSheetValue.Expanded)
        }
    }
}