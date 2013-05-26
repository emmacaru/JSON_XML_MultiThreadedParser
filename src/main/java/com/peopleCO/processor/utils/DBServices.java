package com.peopleCO.processor.utils;


import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import com.peopleCO.processor.TO.PersonsTO;
import com.peopleCO.processor.converters.TOMappers;
import com.peopleCO.processor.hibernate.data.ProcessedFiles;



public class DBServices {


    private final static Logger logger = Logger.getLogger(DBServices.class.toString());

    private TOMappers toMappers;
    private PersonsTO personsTO;

    private Integer id = 0;



    /**
     * @param personsTO
     */
    public DBServices(PersonsTO personsTO) {
        this.personsTO = personsTO;
        this.toMappers = new TOMappers();
    }



    /**
     * persists data
     * 
     * @throws Exception
     */
    public void persistToDB()
            throws Exception {
        ProcessedFiles processedFiles = this.toMappers.toProcessedFiles(this.personsTO);
        logger.info("PersisUtil.persist Writing " + processedFiles.getFilename() + " to db");
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        try {
            logger.debug("DBServices.persistToDB - Open DB Transaction");
            
            session.beginTransaction();
            session.saveOrUpdate(processedFiles);
            session.getTransaction().commit();
            
            logger.debug("DBServices.persistToDB -Commit DB Transaction");
        }
        catch (Exception e) {
            logger.error("PersisUtil.persist failed to save ProcessedFiles to DB - rollback...");
            logger.error(e.getStackTrace());
            // handles any exceptions and rollback in case there is an exception
            session.getTransaction().rollback();
            // throw back exception for upper classes
            throw e;
        }
        this.id = processedFiles.getId();
    }



    /**
     * Update FileName
     * 
     * @param filename
     */
    public void updateFileName(String filename) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("from ProcessedFiles where id = :idValue ");
        query.setParameter("idValue", this.id);
        List list = query.list();
        ProcessedFiles pf = (ProcessedFiles) list.get(0);
        pf.setFilename(filename);
        session.saveOrUpdate(pf);
        session.getTransaction().commit();
    }

}
