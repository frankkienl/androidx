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

package androidx.ui.core.adapter

import androidx.ui.core.Density
import com.google.r4a.Children
import com.google.r4a.Composable

// Ignore that the IDEA cannot resolve this.
import androidx.ui.core.DensityConsumer
import androidx.ui.core.CraneWrapperComposable

/**
 * All this module is needed to work around b/120971484
 *
 * For the original logic:
 * @see androidx.ui.core.DensityConsumer
 */
@Composable
@Suppress("PLUGIN_ERROR")
fun DensityConsumer(@Children children: (density: Density) -> Unit) {
    // Ignore that the IDEA cannot resolve this.
    DensityConsumer(children)
}

@Composable
@Suppress("PLUGIN_ERROR")
fun CraneWrapper(@Children children: () -> Unit) {
    CraneWrapperComposable(children)
}