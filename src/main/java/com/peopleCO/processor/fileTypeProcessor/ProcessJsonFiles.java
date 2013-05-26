package com.peopleCO.processor.fileTypeProcessor;


import java.io.File;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;



/**
 * @author ecaruana
 * 
 */
public class ProcessJsonFiles extends ProcessFiles {

    /**
	 * 
	 */
    private final static Logger logger = Logger.getLogger(ProcessJsonFiles.class.toString());



    private static String PROCESSOR_EXT = "json";


    /**
     * @param processPath
     * @param exec
     */
    public ProcessJsonFiles(String processPath, ExecutorService exec) {
        this.setProcessPath(processPath);
        this.setExec(exec);
        this.setProcessorFileExt(PROCESSOR_EXT);
    }

    @Override
    public void mapToPojo(File jsonFile) {
        try {
            // Creates a thread that does the following work:
            // Parse JSON using gson
            // Convert it to PoJo
            // Convert Pojo in Hibernate PoJo
            // Write to Mysql using Hibernate
            JsonFileThread jsonFileThread = new JsonFileThread(jsonFile);
            this.getExec().execute(jsonFileThread);
        }
        catch (Exception e) {
            logger.error("ProcessJsonFiles.mapToPojo - error while reading file and converting from json to pojo");
            logger.error(e.getStackTrace());
        }

    }

}
