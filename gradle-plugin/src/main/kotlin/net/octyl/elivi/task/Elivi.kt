/*
 * This file is part of elivi, licensed under the MIT License (MIT).
 *
 * Copyright (c) Octavia Togami <https://octyl.net>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.octyl.elivi.task

import net.octyl.elivi.ApplyElivi
import net.octyl.elivi.EliviProcessingSpec
import org.gradle.api.file.FileTreeElement
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.util.PatternFilterable
import org.gradle.api.tasks.util.PatternSet
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.setValue
import org.gradle.kotlin.dsl.submit
import org.gradle.workers.WorkerExecutor
import java.nio.file.Files
import javax.inject.Inject

/**
 * Apply Elivi code compression to class files in the source,
 * copy into destination directory.
 */
open class Elivi @Inject constructor(
    private val workerExecutor: WorkerExecutor
) : SourceTask() {

    init {
        // Only class files by default
        include("**/*.class")
        // Filter to only configured elements
        include { element ->
            filterKeys.get().isSatisfiedBy(element)
        }
    }

    @Internal
    val specs = project.objects.mapProperty<PatternFilterable, EliviProcessingSpec>()
        .convention(mutableMapOf())
    private val filterKeys = specs.keySet().map { filters ->
        val filterSpecs = filters.map { PatternSet().copyFrom(it).asSpec }
        Spec<FileTreeElement> { element ->
            filterSpecs.any { it.isSatisfiedBy(element) }
        }
    }

    @OutputDirectory
    val destDirectoryProperty = project.objects.directoryProperty()
    @get:Internal
    var destDirectory by destDirectoryProperty

    @TaskAction
    fun eliviJar() {
        val workQueue = workerExecutor.noIsolation()

        val dest = destDirectory.asFile.toPath()
        if (!Files.exists(dest)) {
            Files.createDirectories(dest)
        }

        for ((filter, spec) in specs.get()) {
            workQueue.submit(ApplyElivi::class) {
                inputFiles.set(source.matching(filter).map { it.toPath() })
                outputDir.set(destDirectoryProperty)
                this.spec.set(spec)
            }
        }
    }

}