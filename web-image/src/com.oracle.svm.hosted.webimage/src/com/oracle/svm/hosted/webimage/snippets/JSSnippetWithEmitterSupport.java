/*
 * Copyright (c) 2025, 2025, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.oracle.svm.hosted.webimage.snippets;

import java.util.List;

import com.oracle.svm.webimage.hightiercodegen.CodeGenTool;
import com.oracle.svm.webimage.hightiercodegen.Emitter;

import jdk.graal.compiler.debug.GraalError;

/**
 * A JSSnippet that is more powerful that {@link JSSnippet} because it allows the usage of
 * {@link Emitter}. The formatted string needs {@link #EmitterPlaceHolder} placeholders that are not
 * emitted. Instead, the corresponding {@link Emitter} is invoked at that place.
 */
public class JSSnippetWithEmitterSupport extends JSSnippet {

    public static final String EmitterPlaceHolder = "##arg##";
    private final List<Emitter> emitters;

    public JSSnippetWithEmitterSupport(String formattedString, List<Emitter> emitters) {
        super(formattedString);
        this.emitters = emitters;
    }

    @Override
    public void lower(CodeGenTool tool) {
        int emitterIndex = 0;
        for (String line : formattedString.split("\n")) {
            // lower the snippet line by line
            int current = 0;
            int placeHolderStart;
            while ((placeHolderStart = line.indexOf(EmitterPlaceHolder, current)) != -1) {
                tool.getCodeBuffer().emitText(line.substring(current, placeHolderStart));
                emitters.get(emitterIndex).lower(tool);
                emitterIndex++;
                current = placeHolderStart + EmitterPlaceHolder.length();
            }
            tool.getCodeBuffer().emitText(line.substring(current));
            tool.getCodeBuffer().emitNewLine();
        }
    }

    @Override
    public String asString() {
        throw GraalError.shouldNotReachHere("Cannot convert a " + this.getClass().getName() + " to a string because it contains emitters"); // ExcludeFromJacocoGeneratedReport
    }
}
