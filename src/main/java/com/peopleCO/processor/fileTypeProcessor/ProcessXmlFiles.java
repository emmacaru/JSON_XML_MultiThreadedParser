package com.peopleCO.processor.fileTypeProcessor;


import java.io.File;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import com.peopleCO.processor.TO.PersonTO;
import com.peopleCO.processor.TO.PersonsTO;
import com.thoughtworks.xstream.XStream;



/**
 * @author ecaruana
 * 
 */
public class ProcessXmlFiles extends ProcessFiles {

    /**
	 * 
	 */
    private final static Logger logger = Logger.getLogger(ProcessXmlFiles.class.toString());


    private XStream xstream;

    private static String PROCESSOR_EXT = "xml";



    /**
     * @param processPath
     * @param exec
     */
    public ProcessXmlFiles(String processPath, ExecutorService exec) {
        this.setProcessPath(processPath);
        this.setExec(exec);
        this.setProcessorFileExt(PROCESSOR_EXT);

        // init XStream to parse XML file
        this.xstream = new XStream();
        xstream.alias("persons", PersonsTO.class);
        xstream.alias("person", PersonTO.class);
        xstream.aliasField("first", PersonTO.class, "name");
        xstream.aliasField("last", PersonTO.class, "surname");
        xstream.addImplicitCollection(PersonsTO.class, "allPerson");
    }



    @Override
    public void mapToPojo(File file) {
        try {
            XmlFileThread xmlFileThread = new XmlFileThread(file, this.xstream);
            this.getExec().execute(xmlFileThread);
        }
        catch (Exception e) {
            logger.error("ProcessXmlFiles.mapToPojo - error while reading file and converting from xml to pojo");
            logger.error(e);
        }
    }


}
