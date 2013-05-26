package com.peopleCO.processor.test;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import junit.framework.Assert;

import org.junit.Test;

import com.peopleCO.processor.utils.FileMovingUtil;



/**
 * @author ecaruana
 *
 */
public class FileMovingUtilTest {

    private static final String PENDING = "pending";
    private static final String ARCHIVE = "archive";
    private static final String INVALID = "invalid";

    @Test
    public void simpleFileMovementArchive()
            throws IOException {
        File file1 = new File(PENDING, "file.xml.processing");
        file1.createNewFile();
        new FileMovingUtil(file1, true).fileMove();
        File pendingFile = new File(PENDING, "file.xml.processing");
        File archivedFile = new File(ARCHIVE, "file.xml");
        Assert.assertEquals(pendingFile.exists(), false);
        Assert.assertEquals(archivedFile.exists(), true);
        archivedFile.delete();
    }



    @Test
    public void simpleFileMovementInvalid()
            throws IOException {
        File file1 = new File(PENDING, "file.xml.processing");
        file1.createNewFile();
        new FileMovingUtil(file1, false).fileMove();
        File pendingFile = new File(PENDING, "file.xml.processing");
        File invalidFile = new File(INVALID, "file.xml");
        Assert.assertEquals(pendingFile.exists(), false);
        Assert.assertEquals(invalidFile.exists(), true);
        invalidFile.delete();
    }



    @Test
    public void recursiveFileMovementSuccess()
            throws IOException {
        File aDir = new File(PENDING, "a");
        aDir.mkdir();
        File bDir = new File(PENDING + "/a", "b");
        bDir.mkdir();
        File file1 = new File(PENDING + "/a/b", "file.xml.processing");
        file1.createNewFile();
        new FileMovingUtil(file1, true).fileMove();
        File pendingFile = new File(PENDING, "file.xml.processing");
        File archiveFile = new File(ARCHIVE, "file.xml");
        Assert.assertEquals(pendingFile.exists(), false);
        Assert.assertEquals(archiveFile.exists(), true);
        archiveFile.delete();
        bDir.delete();
        aDir.delete();
    }



    @Test
    public void recursiveFileMovementInvalid()
            throws IOException {
        File aDir = new File(PENDING, "a");
        aDir.mkdir();
        File bDir = new File(PENDING + "/a", "b");
        bDir.mkdir();
        File file1 = new File(PENDING + "/a/b", "file.xml.processing");
        file1.createNewFile();
        new FileMovingUtil(file1, false).fileMove();
        File pendingFile = new File(PENDING, "file.xml.processing");
        File invalidFile = new File(INVALID, "file.xml");
        Assert.assertEquals(pendingFile.exists(), false);
        Assert.assertEquals(invalidFile.exists(), true);
        invalidFile.delete();
        bDir.delete();
        aDir.delete();
    }



    @Test
    public void sameFileMovementSuccess()
            throws IOException {
        // create file with "abc" in it
        File file1 = new File(PENDING, "file.xml.processing");
        file1.createNewFile();
        Writer out1 = new OutputStreamWriter(new FileOutputStream(file1));
        try {
            out1.write("abc");
        }
        finally {
            out1.close();
        }
        // performs logic ... file ends up in archive
        new FileMovingUtil(file1, true).fileMove();
        File pendingFile = new File(PENDING, "file.xml.processing");
        File archiveFile = new File(ARCHIVE, "file.xml");
        Assert.assertEquals(pendingFile.exists(), false);
        Assert.assertEquals(archiveFile.exists(), true);
        // write again same file
        file1.createNewFile();
        out1 = new OutputStreamWriter(new FileOutputStream(file1));
        try {
            out1.write("abc");
        }
        finally {
            out1.close();
        }
        // performs logic AGAIN ... no files are to be added
        new FileMovingUtil(file1, true).fileMove();
        pendingFile = new File(PENDING, "file.xml.processing");
        archiveFile = new File(ARCHIVE, "file.xml");
        Assert.assertEquals(pendingFile.exists(), false);
        Assert.assertEquals(archiveFile.exists(), true);
    }



    @Test
    public void differentFileMovementSuccess()
            throws IOException {
        // create file with "abc" in it
        File file1 = new File(PENDING, "file.xml.processing");
        file1.createNewFile();
        Writer out1 = new OutputStreamWriter(new FileOutputStream(file1));
        try {
            out1.write("abc");
        }
        finally {
            out1.close();
        }
        // performs logic ... file ends up in archive
        new FileMovingUtil(file1, true).fileMove();
        File pendingFile = new File(PENDING, "file.xml.processing");
        File archiveFile = new File(ARCHIVE, "file.xml");
        Assert.assertEquals(pendingFile.exists(), false);
        Assert.assertEquals(archiveFile.exists(), true);
        // write again DIFFERENT file -- but same name
        file1.createNewFile();
        out1 = new OutputStreamWriter(new FileOutputStream(file1));
        try {
            out1.write("abcABC");
        }
        finally {
            out1.close();
        }
        // performs logic AGAIN ... no files are to be added
        new FileMovingUtil(file1, true).fileMove();
        pendingFile = new File(PENDING, "file.xml.processing");
        archiveFile = new File(ARCHIVE, "file-1.xml");
        Assert.assertEquals(pendingFile.exists(), false);
        Assert.assertEquals(archiveFile.exists(), true);
        // write again DIFFERENT file -- but same name
        file1.createNewFile();
        out1 = new OutputStreamWriter(new FileOutputStream(file1));
        try {
            out1.write("abcABCabc");
        }
        finally {
            out1.close();
        }
        // performs logic AGAIN ... no files are to be added
        new FileMovingUtil(file1, true).fileMove();
        pendingFile = new File(PENDING, "file.xml.processing");
        archiveFile = new File(ARCHIVE, "file-2.xml");
        Assert.assertEquals(pendingFile.exists(), false);
        Assert.assertEquals(archiveFile.exists(), true);
        archiveFile = new File(ARCHIVE, "file.xml");
        archiveFile.delete();
        archiveFile = new File(ARCHIVE, "file-1.xml");
        archiveFile.delete();
        archiveFile = new File(ARCHIVE, "file-2.xml");
        archiveFile.delete();
    }


}
