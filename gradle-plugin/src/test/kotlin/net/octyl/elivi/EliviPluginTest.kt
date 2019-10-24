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

package net.octyl.elivi

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import java.nio.file.Files
import java.nio.file.Path


class EliviPluginTest {
    companion object {
        @JvmStatic
        @TempDir
        lateinit var sharedTempDir: Path

        @JvmStatic
        private fun writeBuildGradle(prefix: String = "", content: String) {
            Files.write(sharedTempDir.resolve("build.gradle.kts"), (prefix + "\n" + """
                plugins {
                    id("net.octyl.elivi")
                }
            """.trimIndent() + "\n" + content).toByteArray())
        }
    }

    @Test
    fun noSource() {
        writeBuildGradle(
            prefix = """
                import org.gradle.kotlin.dsl.register
                import net.octyl.elivi.task.Elivi
            """.trimIndent(),
            content = """
                tasks.register<Elivi>("elivi")
            """.trimIndent())

        val result = GradleRunner.create()
            .withProjectDir(sharedTempDir.toFile())
            .withArguments("elivi")
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.NO_SOURCE, result.task(":elivi")?.outcome)
    }

    @Test
    fun singleSpec() {
        val classInput = sharedTempDir.resolve("Test.class")
        val classBytes = ClassWriter(ClassWriter.COMPUTE_FRAMES or ClassWriter.COMPUTE_MAXS).run {
            visit(Opcodes.V1_8, 0, "Test", null, null, emptyArray())
            visitSource("TestSourceFile.java", null)
            this.toByteArray()
        }
        Files.write(classInput, classBytes)
        writeBuildGradle(
            prefix = """
                import org.gradle.kotlin.dsl.register
                import net.octyl.elivi.task.Elivi
                import net.octyl.elivi.CompressOption
            """.trimIndent(),
            content = """
                tasks.register<Elivi>("elivi") {
                    source(".")
                    spec {
                        flags.add(CompressOption.REMOVE_SOURCEFILE)
                    }
                }
            """.trimIndent())

        val result = GradleRunner.create()
            .withProjectDir(sharedTempDir.toFile())
            .withArguments("elivi")
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":elivi")?.outcome)
        val output = sharedTempDir.resolve("build/elivi/Test.class")
        assertTrue(Files.exists(output))
        val classNode = ClassNode().also { node ->
            Files.newInputStream(output).use {
                ClassReader(it).accept(node, 0)
            }
        }
        assertNull(classNode.sourceFile)
    }

    @Test
    fun splitSpecs() {
        val classInputRemSrc = sharedTempDir.resolve("TestRemSrc.class")
        val classBytesRemSrc = ClassWriter(ClassWriter.COMPUTE_FRAMES or ClassWriter.COMPUTE_MAXS).run {
            visit(Opcodes.V1_8, 0, "TestRemSrc", null, null, emptyArray())
            visitSource("TestSourceFile.java", null)
            this.toByteArray()
        }
        Files.write(classInputRemSrc, classBytesRemSrc)
        val classInputRenameField = sharedTempDir.resolve("TestRenameField.class")
        val classBytesRenameField = ClassWriter(ClassWriter.COMPUTE_FRAMES or ClassWriter.COMPUTE_MAXS).run {
            visit(Opcodes.V1_8, 0, "TestRenameField", null, null, emptyArray())
            visitField(Opcodes.ACC_PRIVATE, "preciselyNotShort", "Z", null, null)?.visitEnd()
            this.toByteArray()
        }
        Files.write(classInputRenameField, classBytesRenameField)
        writeBuildGradle(
            prefix = """
                import org.gradle.kotlin.dsl.register
                import net.octyl.elivi.task.Elivi
                import net.octyl.elivi.CompressOption
            """.trimIndent(),
            content = """
                tasks.register<Elivi>("elivi") {
                    source(".")
                    spec("TestRemSrc.class") {
                        flags.add(CompressOption.REMOVE_SOURCEFILE)
                    }
                    spec("TestRenameField.class") {
                        flags.add(CompressOption.RENAME_PRIVATE_FIELDS)
                    }
                }
            """.trimIndent())

        val result = GradleRunner.create()
            .withProjectDir(sharedTempDir.toFile())
            .withArguments("elivi")
            .withPluginClasspath()
            .build()

        assertEquals(TaskOutcome.SUCCESS, result.task(":elivi")?.outcome)
        val outputRemSrc = sharedTempDir.resolve("build/elivi/TestRemSrc.class")
        assertTrue(Files.exists(outputRemSrc))
        val classNodeRemSrc = ClassNode().also { node ->
            Files.newInputStream(outputRemSrc).use {
                ClassReader(it).accept(node, 0)
            }
        }
        assertNull(classNodeRemSrc.sourceFile)

        val outputRenameField = sharedTempDir.resolve("build/elivi/TestRenameField.class")
        assertTrue(Files.exists(outputRenameField))
        val classNodeRenameField = ClassNode().also { node ->
            Files.newInputStream(outputRenameField).use {
                ClassReader(it).accept(node, 0)
            }
        }
        assertEquals(1, classNodeRenameField.fields.size)
        assertEquals("a", classNodeRenameField.fields[0].name)
    }
}