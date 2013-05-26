package com.peopleCO.processor.fileTypeProcessor;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.log4j.Logger;



/**
 * @author ecaruana
 * 
 */
public abstract class ProcessFiles {


    /**
	 * 
	 */
    private final static Logger logger = Logger.getLogger(ProcessFiles.class.toString());

    private ExecutorService exec;
    
    private String processorFileExt;
    
    private String processPath;

    
    /**
     * @throws IOException
     */
    public void process() throws IOException {
        List<File> files = listFilesWithExt(this.getProcessPath(), this.getProcessorFileExt());
        for (File file : files) {
            FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
            try {
                // Get an exclusive lock on the whole file
                FileLock lock = channel.lock();
                try {
                    mapToPojo(file);
                } finally {
                    lock.release();
                }
            } finally {
                channel.close();
            }
        }
    }


    /**
     * List files with @param ext in @path
     * 
     * @param path
     * @param ext
     * @return
     */
    public List<File> listFilesWithExt(String path, String ext) {
        File directory = new File(path);

        Collection<File> files = FileUtils.listFiles(directory, new RegexFileFilter("^(.*)" + ext + "$", IOCase.INSENSITIVE), DirectoryFileFilter.DIRECTORY);

        if (files != null)
            return new ArrayList<File>(files);
        else
            return new ArrayList<File>();

    }

    public abstract void mapToPojo(File file) ;


    
    /**
     * @return the processPath
     */
    public String getProcessPath() {
        return processPath;
    }


    
    /**
     * @param processPath the processPath to set
     */
    public void setProcessPath(String processPath) {
        this.processPath = processPath;
    }
    
    
    /**
     * @return the processorFileExt
     */
    public String getProcessorFileExt() {
        return processorFileExt;
    }




    
    /**
     * @param processorFileExt the processorFileExt to set
     */
    public void setProcessorFileExt(String processorFileExt) {
        this.processorFileExt = processorFileExt;
    }




    /**
     * @return the exec
     */
    public ExecutorService getExec() {
        return exec;
    }



    
    /**
     * @param exec the exec to set
     */
    public void setExec(ExecutorService exec) {
        this.exec = exec;
    }


}
