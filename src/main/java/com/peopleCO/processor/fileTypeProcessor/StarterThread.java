package com.peopleCO.processor.fileTypeProcessor;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import com.peopleCO.processor.main.MainRunner;



/**
 * @author ecaruana
 * 
 */
public class StarterThread
        implements Runnable {

    /**
	 * 
	 */
    private final static Logger logger = Logger.getLogger(StarterThread.class.toString());


    /**
     * debugEnabled boolean.
     */
    private final boolean debugEnabled = logger.isDebugEnabled();


    private final static Integer INTERVAL = 5000;

    private ExecutorService executorService;
    private ProcessFiles processXml;
    private ProcessFiles processJson;
    private Integer numberOfFilesFound = 0;

    private static String processPath = "pending";



    /**
     * @param exec
     */
    public StarterThread(ExecutorService executorService, Boolean bxml, Boolean bjson) {
        this.executorService = executorService;

        if (bxml)
            processXml = new ProcessXmlFiles(this.processPath, this.executorService);
        if (bjson)
            processJson = new ProcessJsonFiles(this.processPath, this.executorService);
    }



    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {

        while (MainRunner.running) {


            try {
                logger.info("StarterThread.run - starting to process xml files");
                if (processXml != null)
                    processXml.process();
                logger.info("StarterThread.run - starting to process json files");
                if (processJson != null)
                    processJson.process();
            }
            catch (IOException e) {
                logger.error(e.getStackTrace());
            }


            try {
                while (true) {
                    Thread.sleep(INTERVAL);
                    numberOfFilesFound = 0;
                    if (processXml != null){
                        List<File> xmlProcessFiles = processXml.listFilesWithExt(this.processPath, "xml.processing");
                        numberOfFilesFound = numberOfFilesFound  + xmlProcessFiles.size(); 
                    }

                    if (processXml  != null){
                        List<File> jsonProcessFiles = processXml.listFilesWithExt(this.processPath, "json.processing");
                        numberOfFilesFound = numberOfFilesFound  + jsonProcessFiles.size();
                    }


                    if (numberOfFilesFound == 0)
                        break;

                    logger.info("StarterThread.run  - files found. Waiting for them to get processed");

                    if (debugEnabled) {
                        logger.debug("StarterThread.run  - Number of files found = " + numberOfFilesFound.toString());
                    }
                    Thread.sleep(INTERVAL);
                }
            }
            catch (InterruptedException e) {
                logger.error(e.getStackTrace());
            }
        }
        logger.info("StarterThread.run - no more new threads can be created");
        executorService.shutdown();

        logger.info("StarterThread.run - Starter thread - closing down....");

    }
}
