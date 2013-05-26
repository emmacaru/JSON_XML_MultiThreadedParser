package com.peopleCO.processor.test;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import com.peopleCO.processor.TO.PersonTO;
import com.peopleCO.processor.TO.PersonsTO;
import com.peopleCO.processor.fileTypeProcessor.XmlFileThread;
import com.peopleCO.processor.hibernate.data.ProcessedFiles;
import com.peopleCO.processor.utils.HibernateSessionFactory;
import com.thoughtworks.xstream.XStream;



/**
 * @author ecaruana
 * 
 */
public class XmlFileParserTest {

    private final static Logger logger = Logger.getLogger(XmlFileParserTest.class.toString());

    private XStream xstream;

    private void setXStream() {
        // init XStream to parse XML file
        this.xstream = new XStream();
        xstream.alias("persons", PersonsTO.class);
        xstream.alias("person", PersonTO.class);
        xstream.aliasField("first", PersonTO.class, "name");
        xstream.aliasField("last", PersonTO.class, "surname");
        xstream.addImplicitCollection(PersonsTO.class, "allPerson");
    }



    @Before
    public void testClean() {
        TestHelper.dbCleaning();
        TestHelper.folderCleaning();
    }



    /**
     * onerecord.xml A One record VALID xml file A person must be added to the table and file should
     * be marked as processed File must be moved to archive folder.
     * 
     */
    @Test
    public void simpleXmlFile()
            throws IOException {

        setXStream();

        URL url = this.getClass().getResource("/onerecord.xml");
        File f1 = new File(url.getFile());

        File pendingFile = new File("pending/" + f1.getName());
        FileUtils.copyFile(f1, pendingFile);


        XmlFileThread xmlT = new XmlFileThread(pendingFile, this.xstream);
        xmlT.run();

        // checking...

        Session session = HibernateSessionFactory.getSessionFactory().openSession();

        Query query = session.createQuery("from ProcessedFiles where filename = :filenameValue ");
        query.setParameter("filenameValue", "onerecord.xml");
        List list = query.list();

        Assert.assertEquals(list.size(), 1);

        ProcessedFiles pf = (ProcessedFiles) list.get(0);

        Assert.assertEquals(pf.getFilename(), "onerecord.xml");
        Assert.assertEquals(pf.getFiletype(), 0); // xml

        Assert.assertEquals(pf.getPersonDatas().size(), 1);

        // check file logic
        Assert.assertEquals(new File("archive", "onerecord.xml").exists(), true);
        Assert.assertEquals(new File("pending", "onerecord.xml").exists(), false);
    }



    /**
     * empty.xml Simple empty xml file. Parse error hence file should be moved in the invalid
     * folder. No records in db should be added.
     * 
     */
    @Test
    public void empyXmlFile()
            throws IOException {

        setXStream();

        URL url = this.getClass().getResource("/empty.xml");
        File f1 = new File(url.getFile());

        File pendingFile = new File("pending/" + f1.getName());
        FileUtils.copyFile(f1, pendingFile);

        XmlFileThread xmlT = new XmlFileThread(pendingFile, this.xstream);
        xmlT.run();

        // checking...

        Session session = HibernateSessionFactory.getSessionFactory().openSession();

        Query query = session.createQuery("from ProcessedFiles where filename = :filenameValue ");
        query.setParameter("filenameValue", "empty.xml");
        List list = query.list();

        // no file has been added to the db
        Assert.assertEquals(list.size(), 0);

        // check file logic
        Assert.assertEquals(new File("invalid", "empty.xml").exists(), true);
        Assert.assertEquals(new File("pending", "empty.xml").exists(), false);
    }



    /**
     * invalidlastrecord.xml contains 3 records with the last record having a wrong property name It
     * is an invalid file and can never succeed so move to invalid folder File should be moved to
     * invalid folder and no records should be put into db
     */
    @Test
    public void invalidXmlFileAtEnd()
            throws IOException {

        setXStream();

        URL url = this.getClass().getResource("/invalidlastrecord.xml");
        File f1 = new File(url.getFile());

        File pendingFile = new File("pending/" + f1.getName());
        FileUtils.copyFile(f1, pendingFile);

        XmlFileThread xmlT = new XmlFileThread(pendingFile, this.xstream);
        xmlT.run();

        // checking...

        Session session = HibernateSessionFactory.getSessionFactory().openSession();

        Query query = session.createQuery("from ProcessedFiles where filename = :filenameValue ");
        query.setParameter("filenameValue", "invalidlastrecord.xml");
        List list = query.list();

        // no file has been added to the db - because one of the records will fail
        Assert.assertEquals(list.size(), 0);

        // check file logic - file should remain in pending
        Assert.assertEquals(new File("invalid", "invalidlastrecord.xml").exists(), true);
    }



    /**
     * same1 and same2 are moved one a time to pending folder with same name --- same.xml They have
     * different content hence they should be renamed. same.xml same-1.xml BOTH as a file and even
     * in DB.
     * 
     * 
     */
    @Test
    public void sameFileNameDifferentContent()
            throws IOException {

        setXStream();

        URL url = this.getClass().getResource("/same1.xml");
        File f1 = new File(url.getFile());

        File pendingFile = new File("pending", "same.xml");
        FileUtils.copyFile(f1, pendingFile);

        XmlFileThread xmlT = new XmlFileThread(pendingFile, this.xstream);
        xmlT.run();


        url = this.getClass().getResource("/same2.xml");
        f1 = new File(url.getFile());

        pendingFile = new File("pending", "same.xml");
        FileUtils.copyFile(f1, pendingFile);

        // re run xmlThread
        xmlT = new XmlFileThread(pendingFile, this.xstream);
        xmlT.run();

        // checking...

        Session session = HibernateSessionFactory.getSessionFactory().openSession();

        // there should be one processed file called same.xml and one called same-1.xml
        Query query = session.createQuery("from ProcessedFiles where filename = :filenameValue ");
        query.setParameter("filenameValue", "same.xml");
        List list = query.list();

        Assert.assertEquals(list.size(), 1);

        ProcessedFiles pf = (ProcessedFiles) list.get(0);
        Assert.assertEquals(pf.getFiletype(), 0); // xml
        Assert.assertEquals(pf.getPersonDatas().size(), 1);

        query = session.createQuery("from ProcessedFiles where filename = :filenameValue ");
        query.setParameter("filenameValue", "same-1.xml");
        list = query.list();

        Assert.assertEquals(list.size(), 1);

        ProcessedFiles pf1 = (ProcessedFiles) list.get(0);
        Assert.assertEquals(pf1.getFiletype(), 0); // xml
        Assert.assertEquals(pf1.getPersonDatas().size(), 1);

        // check file logic

        // same.xml must be moved
        Assert.assertEquals(new File("pending", "same.xml").exists(), false);

        // same.xml and same-1.xml must exists
        Assert.assertEquals(new File("archive", "same.xml").exists(), true);
        Assert.assertEquals(new File("archive", "same-1.xml").exists(), true);
    }



    /**
     * several records - contains 4 people It must be successfully processed.
     * 
     */
    @Test
    public void severalrecords()
            throws IOException {

        setXStream();

        URL url = this.getClass().getResource("/severalrecords.xml");
        File f1 = new File(url.getFile());

        File pendingFile = new File("pending", "severalrecords.xml");
        FileUtils.copyFile(f1, pendingFile);

        XmlFileThread xmlT = new XmlFileThread(pendingFile, this.xstream);
        xmlT.run();

        // checking...

        Session session = HibernateSessionFactory.getSessionFactory().openSession();

        // there should be one processed file called same.xml and one called same-1.xml
        Query query = session.createQuery("from ProcessedFiles where filename = :filenameValue ");
        query.setParameter("filenameValue", "severalrecords.xml");
        List list = query.list();

        Assert.assertEquals(list.size(), 1);

        ProcessedFiles pf = (ProcessedFiles) list.get(0);
        Assert.assertEquals(pf.getFiletype(), 0); // xml
        Assert.assertEquals(pf.getPersonDatas().size(), 4);

        // check file logic

        // file must be moved
        Assert.assertEquals(new File("pending", "severalrecords.xml").exists(), false);
        Assert.assertEquals(new File("archive", "severalrecords.xml").exists(), true);
    }

}
