/*
 * This file is part of PaperclipGradle, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016, Jamie Mansfield <https://www.jamierocks.uk/>
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

package com.destroystokyo.paperclip.gradle.task

import com.destroystokyo.paperclip.gradle.PaperclipExtension
import com.destroystokyo.paperclip.gradle.PaperclipGradlePlugin
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
/**
 * The generatePatchData task.
 */
class GeneratePatchDataTask extends DefaultTask {

    PaperclipExtension extension

    @TaskAction
    void doTask() {
        final Path patch = PaperclipGradlePlugin.GENERATED_SOURCES.resolve('paperMC.patch')
        final Path json = PaperclipGradlePlugin.GENERATED_SOURCES.resolve('patch.json')
        final Path vanillaMinecraft = Paths.get(extension.vanillaMinecraft)
        final Path paperMinecraft = Paths.get(extension.paperMinecraft)

        if (Files.notExists(PaperclipGradlePlugin.GENERATED_SOURCES)) {
            Files.createDirectory(PaperclipGradlePlugin.GENERATED_SOURCES)
        }

        if (Files.notExists(vanillaMinecraft)) {
            throw new RuntimeException('Vanilla Minecraft jar not found!')
        }

        if (Files.notExists(paperMinecraft)) {
            throw new RuntimeException('Paper Minecraft jar not found!')
        }

        logger.lifecycle(':reading jars into memory')
        final byte[] vanillaMinecraftBytes = Files.readAllBytes(vanillaMinecraft)
        final byte[] paperMinecraftBytes = Files.readAllBytes(paperMinecraft)

        logger.lifecycle(':creating patch')
        Files.newOutputStream(vanillaMinecraft).withStream {

        }
    }

}
