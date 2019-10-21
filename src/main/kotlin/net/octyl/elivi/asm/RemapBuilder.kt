/*
 * This file is part of elivi-code-compressor, licensed under the MIT License (MIT).
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
import net.octyl.elivi.CompressOption.*
import net.octyl.elivi.NameCounter
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class RemapBuilder(
    private val flags: Set<CompressOption>
) : ClassVisitor(Opcodes.ASM7) {
    val map = mutableMapOf<String, String>()
    private val fieldNames = NameCounter()
    private val methodNames = NameCounter()

    private lateinit var className: String

    private fun Int.isPrivate() = (this and Opcodes.ACC_PRIVATE) != 0

    override fun visit(version: Int, access: Int, name: String, signature: String?, superName: String?, interfaces: Array<out String>?) {
        className = name
    }

    override fun visitField(access: Int, name: String?, descriptor: String?, signature: String?, value: Any?): FieldVisitor? {
        if (RENAME_PRIVATE_FIELDS in flags && access.isPrivate()) {
            map["$className.$name"] = fieldNames.getAndIncrement()
        }
        return super.visitField(access, name, descriptor, signature, value)
    }

    override fun visitMethod(access: Int, name: String?, descriptor: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor? {
        if (RENAME_PRIVATE_METHODS in flags && access.isPrivate()) {
            map["$className.$name$descriptor"] = methodNames.getAndIncrement()
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }
}