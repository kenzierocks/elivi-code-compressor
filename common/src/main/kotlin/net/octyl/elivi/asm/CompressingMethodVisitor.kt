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

import net.octyl.elivi.CompressOption
import net.octyl.elivi.CompressOption.REMOVE_LNT
import net.octyl.elivi.CompressOption.REMOVE_LVT
import net.octyl.elivi.CompressOption.REMOVE_SIGNATURE
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class CompressingMethodVisitor(
    private val flags: Set<CompressOption>,
    delegate: MethodVisitor? = null
) : MethodVisitor(Opcodes.ASM7, delegate) {

    override fun visitLocalVariable(name: String, descriptor: String?, signature: String?, start: Label?, end: Label?, index: Int) {
        if (REMOVE_LVT !in flags) {
            val realSignature = signature.takeUnless { REMOVE_SIGNATURE in flags }
            return super.visitLocalVariable(name, descriptor, realSignature, start, end, index)
        }
    }

    override fun visitLineNumber(line: Int, start: Label?) {
        if (REMOVE_LNT !in flags) {
            return super.visitLineNumber(line, start)
        }
    }
}