package com.peopleCO.processor.utils;


import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.log4j.Logger;



/**
 * @author ecaruana
 * 
 */
public class FileMovingUtil {


    private final static Logger logger = Logger.getLogger(FileMovingUtil.class.toString());

    /**
     * debugEnabled boolean.
     */
    private final boolean debugEnabled = logger.isDebugEnabled();

    /**
     * Archive folder
     */
    private static final String ARCHIVE = "archive";

    /**
     * Invalid folder
     */
    private static final String INVALID = "invalid";

    /**
     * 
     */
    private File file;

    /**
     * 
     */
    private boolean success;

    /**
     * 
     */
    private DBServices dbServices;



    /**
     * @param file
     * @param success
     */
    public FileMovingUtil(File file, boolean success) {
        this.file = file;
        this.success = success;
    }



    /**
     * @param file
     * @param success
     * @param dbServices
     */
    public FileMovingUtil(File file, boolean success, DBServices dbServices) {
        this.file = file;
        this.success = success;
        this.dbServices = dbServices;
    }



    /**
     * Method to perform file Logic
     * 
     * @throws IOException
     */
    public void fileMove()
            throws IOException {

        logger.debug("FileMovingLogic.fileMove in");

        File directory;

        if (debugEnabled)
            logger.debug("FileMovingLogic.fileMove with success = " + this.success + " for FILE = " + file.getName());

        if (this.success) {
            logger.info("Send " + file.getName() + "to " + ARCHIVE);
            directory = new File(ARCHIVE);
        }
        else {
            logger.info("Send " + file.getName() + "to " + INVALID);
            directory = new File(INVALID);
        }


        String filename = file.getName().replace(".processing", "");

        logger.debug("FileMovingLogic.fileMove - Verifiying if other files exists with same name ");

        Collection<File> files = FileUtils.listFiles(directory, new RegexFileFilter("^" + filename + "$", IOCase.INSENSITIVE), DirectoryFileFilter.DIRECTORY);

        if (files == null || files.size() == 0) {
            // safe to move file since no such file exists
            logger.debug("FileMovingLogic.fileMove No other files found starting moving file ");

            this.file.renameTo(new File(directory, filename));

            logger.info("performLogic Moving " + filename + " sucessfull");
        }
        else {
            // check content - if not the same, append number
            logger.debug("FileMovingLogic.fileMove other files found ");
            File[] filesArray = files.toArray(new File[files.size()]);

            String contentOfProcessed = FileUtils.readFileToString(filesArray[0]);
            String contentOfJustProcessed = FileUtils.readFileToString(this.file);

            if (!contentOfProcessed.equalsIgnoreCase(contentOfJustProcessed)) {
                // loop until a unique filename is found.
                logger.info("FileMovingLogic.fileMove Contents of file is not found to be identical.");

                String tempFilename = filename;
                int postFix = 1;

                while (files != null && files.size() > 0) {
                    tempFilename = filename.replace(".", "-" + postFix + ".");

                    logger.debug("FileMovingLogic.fileMove replace " + filename + " to " + tempFilename);

                    files = FileUtils.listFiles(directory, new RegexFileFilter("^" + tempFilename + "$", IOCase.INSENSITIVE), DirectoryFileFilter.DIRECTORY);

                    postFix++;
                }

                this.file.renameTo(new File(directory, tempFilename));

                if (dbServices != null) {
                    this.dbServices.updateFileName(tempFilename);
                }

                logger.info("Moving " + tempFilename);
            }
            else {
                logger.info("FileMovingLogic.fileMove Contents of file is found to be identical - " + file.getName() + " will be DELETED.");
                // just trash the file
                this.file.delete();
            }
        }
    }


}
