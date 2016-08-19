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
import com.destroystokyo.paperclip.gradle.data.PatchData
import io.sigpipe.jbsdiff.Diff
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.com.google.gson.Gson

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest

/**
 * The generatePatchData task.
 */
class GeneratePatchDataTask extends DefaultTask {

    PaperclipExtension extension

    @TaskAction
    void doTask() {
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
        Files.newOutputStream(vanillaMinecraft).withStream { s ->
            Diff.diff(vanillaMinecraftBytes, paperMinecraftBytes, s);
        }

        // Add the SHA-256 hashes for the files
        final MessageDigest digest = MessageDigest.getInstance("SHA-256")

        logger.lifecycle(':hashing files')
        final byte[] vanillaMinecraftHash = digest.digest(vanillaMinecraftBytes)
        final byte[] paperMinecraftHash = digest.digest(paperMinecraftBytes)

        final PatchData data = new PatchData()
        data.setOriginalHash(toHex(vanillaMinecraftHash))
        data.setPatchedHash(toHex(paperMinecraftHash))
        data.setPatch('paperMC.patch')
        data.setSourceUrl("https://s3.amazonaws.com/Minecraft.Download/versions/${extension.minecraftVersion}/minecraft_server.${extension.minecraftVersion}.jar")
        data.setVersion(extension.minecraftVersion)

        logger.lifecycle(':writing json file')
        final Gson gson = new Gson()
        final String jsonString = gson.toJson(data)

        Files.newBufferedWriter(json, Charset.forName('UTF-8')).withWriter {
            write(jsonString)
        }
    }

    private static String toHex(final byte[] hash) {
        final StringBuilder sb = new StringBuilder(hash.length * 2);
        for (byte aHash : hash) {
            sb.append(String.format("%02X", aHash & 0xFF));
        }
        return sb.toString();
    }

}
