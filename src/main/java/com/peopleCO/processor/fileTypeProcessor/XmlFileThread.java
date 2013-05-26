package com.peopleCO.processor.fileTypeProcessor;


import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.peopleCO.processor.TO.PersonsTO;
import com.peopleCO.processor.utils.DBServices;
import com.peopleCO.processor.utils.FileMovingUtil;
import com.thoughtworks.xstream.XStream;



/**
 * @author ecaruana
 *
 */
public class XmlFileThread
        implements Runnable {


    private final static Logger logger = Logger.getLogger(XmlFileThread.class.toString());

    /**
     * 
     */
    private XStream xstream;
    /**
     * 
     */
    private File file;



    public XmlFileThread(File file, XStream xstream) {
        this.file = file;
        this.xstream = xstream;
    }



    public void run() {
        try {
            logger.info("XmlFileThread.run XML Thread started");
            // actual parsing - do it on separate threads
            PersonsTO personsTO;
            try {
                String xmlContent = FileUtils.readFileToString(this.file);
                personsTO = (PersonsTO) this.xstream.fromXML(xmlContent);
            }
            catch (Exception e) {
                logger.error("XmlFileThread.run parsing failed - move to invalid folder.");
                logger.error(e.getStackTrace());
                new FileMovingUtil(this.file, false).fileMove();
                logger.info("XmlFileThread.run XML Thread ended");
                return;
            }

            personsTO.setFilename(this.file.getName());

            DBServices dbServices = new DBServices(personsTO);

            try {
                dbServices.persistToDB();
            }
            catch (Exception e) {
                logger.error("XmlFileThread.run failed to persist pojos");
                logger.error(e.getStackTrace());
                logger.info("XmlFileThread.run XML Thread ended");
                return;
            }

            // if persists was successful then transfer files
            FileMovingUtil fml = new FileMovingUtil(this.file, true, dbServices);
            fml.fileMove();

            logger.info("XmlFileThread.run XML Thread ended");
        }
        catch (Exception e) {
            logger.error("XmlFileThread.run failed to parse XML and transform in pojo");
            logger.error(e.getStackTrace());
        }
    }

}
