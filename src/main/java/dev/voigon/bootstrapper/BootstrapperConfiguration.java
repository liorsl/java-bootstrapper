/*
 * MIT License
 *
 * Copyright (c) 2021 Lior Slakman
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.voigon.bootstrapper;

import lombok.Getter;

import java.io.*;
import java.time.Instant;
import java.util.Properties;

@Getter
public class BootstrapperConfiguration {

    public static BootstrapperConfiguration load(File file) throws IOException {
        Properties properties = new Properties();
        loadProperties(file, properties);
        return new BootstrapperConfiguration(properties);
    }

    /**
     * Specifies the main jar file name for the application
     */
    private final String mainJarFileName;

    private BootstrapperConfiguration(Properties properties) {
        this.mainJarFileName = properties.getProperty("mainJarFileName", "application.jar");

    }

    private static void loadProperties(File file, Properties properties) throws IOException {
        if (!file.exists()) {
            InputStream defaultPropertiesFile = Main.class.getClassLoader().getResourceAsStream("bootstrapper.properties");
            properties.load(defaultPropertiesFile);
            properties.store(new FileWriter(file), String.format("Generated by JavaBootstrapper version %s at %s", VersionInformation.VERSION, Instant.now()));
        } else
            properties.load(new FileInputStream(file));

    }
}
