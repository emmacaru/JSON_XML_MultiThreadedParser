package com.peopleCO.processor.test;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import com.peopleCO.processor.fileTypeProcessor.JsonFileThread;
import com.peopleCO.processor.hibernate.data.ProcessedFiles;
import com.peopleCO.processor.utils.HibernateSessionFactory;



/**
 * @author ecaruana
 * 
 */
public class JsonFileParserTest {

    @Before
    public void testClean() {
        TestHelper.dbCleaning();
        TestHelper.folderCleaning();
    }



    /**
     * Read singlerecord.json 
     * A single record VALID json file.  The test will add a person to the table and file will be marked as processed file.  
     * The file will be moved to archive folder
     */
    @Test
    public void simpleJsonFile()
            throws IOException {
        URL url = this.getClass().getResource("/singleRecord.json");
        File f1 = new File(url.getFile());
        File pendingFile = new File("pending/" + f1.getName());
        FileUtils.copyFile(f1, pendingFile);
        JsonFileThread jsonT = new JsonFileThread(pendingFile);
        jsonT.run();

        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Query query = session.createQuery("from ProcessedFiles where filename = :filenameValue ");
        query.setParameter("filenameValue", "singleRecord.json");
        List list = query.list();

        Assert.assertEquals(list.size(), 1);

        ProcessedFiles pf = (ProcessedFiles) list.get(0);

        Assert.assertEquals(pf.getFilename(), "singleRecord.json");
        Assert.assertEquals(pf.getFiletype(), 1); // json
        Assert.assertEquals(pf.getPersonDatas().size(), 1);

        Assert.assertEquals(new File("archive", "singleRecord.json").exists(), true);
        Assert.assertEquals(new File("pending", "singleRecord.json").exists(), false);
    }



    /**
     *  test empty file.
     * Parse error and file will be moved to invalid folder.
     * 
     */
    @Test
    public void empyJsonFile()
            throws IOException {
        URL url = this.getClass().getResource("/emptyFile.json");
        File f1 = new File(url.getFile());

        File pendingFile = new File("pending/" + f1.getName());
        FileUtils.copyFile(f1, pendingFile);

        JsonFileThread jsonT = new JsonFileThread(pendingFile);
        jsonT.run();

        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Query query = session.createQuery("from ProcessedFiles where filename = :filenameValue ");
        query.setParameter("filenameValue", "emptyFile.json");
        List list = query.list();

        Assert.assertEquals(list.size(), 0);

        Assert.assertEquals(new File("invalid", "emptyFile.json").exists(), true);
        Assert.assertEquals(new File("pending", "emptyFile.json").exists(), false);
    }



    /**
     * A json file with some records out of which one of the record is invalid.  
     * The file will not be read and moved to invalid.  Non of the records will be inserted to the DB
     */
    @Test
    public void invalidJsonFileAtEnd()
            throws IOException {
        URL url = this.getClass().getResource("/invalidFileStructure.json");
        File f1 = new File(url.getFile());

        File pendingFile = new File("pending/" + f1.getName());
        FileUtils.copyFile(f1, pendingFile);

        JsonFileThread jsonT = new JsonFileThread(pendingFile);
        jsonT.run();

        Session session = HibernateSessionFactory.getSessionFactory().openSession();

        Query query = session.createQuery("from ProcessedFiles where filename = :filenameValue ");
        query.setParameter("filenameValue", "invalidFileStructure.json");
        List list = query.list();

        Assert.assertEquals(list.size(), 0);
        Assert.assertEquals(new File("invalid", "invalidFileStructure.json").exists(), true);
    }



    /**
     * jsonFile1 and jsonFile2 are moved one a time to pending folder with same name --- same.json
     * They have different content hence they should be renamed. same.json jsonFile-1.json BOTH as a
     * file and even in DB.
     * 
     * 
     */
    @Test
    public void sameFileNameDifferentContent()
            throws IOException {
        URL url = this.getClass().getResource("/jsonFile1.json");
        File f1 = new File(url.getFile());
        File pendingFile = new File("pending", "jsonFile.json");
        FileUtils.copyFile(f1, pendingFile);
        JsonFileThread jsonT = new JsonFileThread(pendingFile);
        jsonT.run();
        url = this.getClass().getResource("/jsonFile2.json");
        f1 = new File(url.getFile());
        pendingFile = new File("pending", "jsonFile.json");
        FileUtils.copyFile(f1, pendingFile);
       
        jsonT = new JsonFileThread(pendingFile);
        jsonT.run();
       
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        // there should be one processed file called same.json and one called same-1.json
        Query query = session.createQuery("from ProcessedFiles where filename = :filenameValue ");
        query.setParameter("filenameValue", "jsonFile.json");
        List list = query.list();

        Assert.assertEquals(list.size(), 1);

        ProcessedFiles processedFiles1 = (ProcessedFiles) list.get(0);

        Assert.assertEquals(processedFiles1.getFiletype(), 1); // json
        Assert.assertEquals(processedFiles1.getPersonDatas().size(), 1);

        query = session.createQuery("from ProcessedFiles where filename = :filenameValue ");
        query.setParameter("filenameValue", "jsonFile-1.json");
        list = query.list();

        Assert.assertEquals(list.size(), 1);

        ProcessedFiles processedFiles2 = (ProcessedFiles) list.get(0);

        Assert.assertEquals(processedFiles2.getFiletype(), 1); // json
        Assert.assertEquals(processedFiles2.getPersonDatas().size(), 1);

        Assert.assertEquals(new File("pending", "jsonFile.json").exists(), false);
        Assert.assertEquals(new File("archive", "jsonFile.json").exists(), true);
        Assert.assertEquals(new File("archive", "jsonFile-1.json").exists(), true);
    }



    /**
     * several records - contains 4 people It must be successfully processed.
     * 
     */
    @Test
    public void severalrecords()
            throws IOException {
        URL url = this.getClass().getResource("/someRecords.json");
        File f1 = new File(url.getFile());
        File pendingFile = new File("pending", "someRecords.json");
        FileUtils.copyFile(f1, pendingFile);
        JsonFileThread jsonT = new JsonFileThread(pendingFile);
        jsonT.run();

        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        // there should be one processed file called same.json and one called same-1.json
        Query query = session.createQuery("from ProcessedFiles where filename = :filenameValue ");
        query.setParameter("filenameValue", "someRecords.json");
        List list = query.list();

        Assert.assertEquals(list.size(), 1);

        ProcessedFiles processedFiles = (ProcessedFiles) list.get(0);

        Assert.assertEquals(processedFiles.getFiletype(), 1); // json
        Assert.assertEquals(processedFiles.getPersonDatas().size(), 4);


        Assert.assertEquals(new File("pending", "someRecords.json").exists(), false);
        Assert.assertEquals(new File("archive", "someRecords.json").exists(), true);
    }


}
