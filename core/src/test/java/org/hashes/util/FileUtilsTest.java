/**
 *    Copyright 2012 Pedro Ribeiro
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.hashes.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;

/**
 * File utils tests.
 * 
 * @author pedroribeiro
 */
@Test(groups = "functional", testName = "util.FileUtilsTest")
public class FileUtilsTest {

    private static final String RESOURCES_FOLDER = new StringBuilder().append("src").append(File.separator)
            .append("test").append(File.separator).append("resources").append(File.separator).toString();

    private static final String DUMMY_FILE = "dummy.txt";

    private static final String NO_FILE = "268c41b9-078a-4196-8bbf-3ddb1a416e82.txt";

    /**
     * Tests whether a file exists on classpath.
     * 
     * @throws Exception in case of a problem
     */
    public void testFileLookupFromClasspath() throws Exception {

        InputStream input = null;
        try {
            input = FileUtils.lookupFile(DUMMY_FILE);
            Assert.assertNotNull(input);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {}
            }
        }
    }

    /**
     * Tests whether a file exists on file system.
     * 
     * @throws Exception in case of a problem
     */
    public void testFileLookupFromFileSystem() throws Exception {

        InputStream input = null;

        try {
            input = FileUtils.lookupFile(RESOURCES_FOLDER + DUMMY_FILE);
            Assert.assertNotNull(input);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {}
            }
        }
    }

    /**
     * Tests if an exception is thrown if the file does not exist.
     * 
     * @throws Exception in case of a problem
     */
    @Test(expectedExceptions = { FileNotFoundException.class })
    public void testFileNotFound() throws Exception {
        InputStream input = null;

        try {
            input = FileUtils.lookupFile(NO_FILE);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {}
            }
        }
    }

    /**
     * Test if a collection of lines are well written to a temporary file and subsequently loaded.
     * 
     * @throws Exception in case of a problem
     */
    public void testWriteAndReadLines() throws Exception {

        Charset charset = Charsets.UTF_8;

        File tempFile = File.createTempFile("FileUtils", "testWriteLines");

        List<String> write = ImmutableList.of(//
                "xwxwxwxwxwxwxwxwxwxwxw", //
                "xwxwxwxwxwxwxwxwxwxwyX", //
                "xwxwxwxwxwxwxwxwxwxwz9", //
                "xwxwxwxwxwxwxwxwxwyXxw", //
                "xwxwxwxwxwxwxwxwxwyXyX").asList();

        FileUtils.writeLines(tempFile, write, charset);

        List<String> read = FileUtils.readLines(tempFile.getPath(), write.size(), charset);

        Assert.assertEquals(write, read);
    }
}
