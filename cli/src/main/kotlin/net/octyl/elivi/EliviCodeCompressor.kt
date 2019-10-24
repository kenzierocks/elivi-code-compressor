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

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.split
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.path
import net.octyl.elivi.asm.AsmCodeCompressor
import java.nio.file.Files
import java.util.EnumSet

class EliviCodeCompressor : CliktCommand(
    name = "elivi",
    help = "Compress class file SOURCE, output to DEST."
) {

    private val flags by option(help = "Flags for the compressor")
        .enum<CompressOption>().split(",").multiple()
    private val source by argument(help = "Class file(s) to process.")
        .path(exists = true).multiple()
    private val dest by argument(help = "Destination directory for compressed file.")
        .path(fileOkay = false)
    private val compressor = AsmCodeCompressor()

    override fun run() {
        Files.createDirectories(dest)
        val flagList = flags.flatten()
        val flagSet: Set<CompressOption> = when {
            flagList.isEmpty() -> setOf()
            else -> EnumSet.copyOf(flagList)
        }
        compressor.compress(source, dest, flagSet)
    }
}
