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

package net.octyl.elivi.asm

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.octyl.elivi.CodeCompressor
import net.octyl.elivi.CompressOption
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.SimpleRemapper
import java.nio.file.Files
import java.nio.file.Path

class AsmCodeCompressor : CodeCompressor {
    override fun compress(source: List<Path>, dest: Path, flags: Set<CompressOption>) {
        runBlocking(Dispatchers.Default) {
            source.map { src ->
                launch {
                    val reader = withContext(Dispatchers.IO) {
                        Files.newInputStream(src).use { ClassReader(it) }
                    }
                    val classData = doCompress(reader, flags)
                    val className = ClassReader(classData).className
                    val destFile = dest.resolve("$className.class")
                    withContext(Dispatchers.IO) {
                        Files.createDirectories(destFile.parent)
                        Files.write(destFile, classData)
                    }
                }
            }.joinAll()
        }
    }

    private fun doCompress(reader: ClassReader, flags: Set<CompressOption>): ByteArray {
        val writer = ClassWriter(0)
        val remapper = RemapBuilder(flags)
        // Configure re-mapper
        reader.accept(remapper,
            ClassReader.SKIP_CODE or ClassReader.SKIP_FRAMES or ClassReader.SKIP_DEBUG)
        // Handle actual class-write, using first pass information
        reader.accept(
            ClassRemapper(
                CompressingClassVisitor(
                    flags, writer
                ),
                SimpleRemapper(remapper.map)
            ),
            0
        )
        return writer.toByteArray()
    }
}

