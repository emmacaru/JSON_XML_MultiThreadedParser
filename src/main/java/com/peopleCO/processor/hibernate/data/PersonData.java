package com.peopleCO.processor.hibernate.data;

import java.io.Serializable;


/**
 * @author ecaruana
 * 
 */
public class PersonData
        implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5524710566462591690L;
    
    /**
     *  Presons ID
     */
    private Integer id;
    /**
     *  ProcessedFiles
     */
    private ProcessedFiles processedFiles;
    /**
     * Person Name
     */
    private String name;
    /**
     * Person Surname
     */
    private String surname;
    /**
     * Person Location
     */
    private String location;



    /**
     * 
     */
    public PersonData() {
    }



    /**
     * @param processedFiles
     * @param name
     * @param surname
     * @param location
     */
    public PersonData(ProcessedFiles processedFiles, String name, String surname, String location) {
        this.processedFiles = processedFiles;
        this.name = name;
        this.surname = surname;
        this.location = location;
    }



    
    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }



    
    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }



    
    /**
     * @return the processedFiles
     */
    public ProcessedFiles getProcessedFiles() {
        return processedFiles;
    }



    
    /**
     * @param processedFiles the processedFiles to set
     */
    public void setProcessedFiles(ProcessedFiles processedFiles) {
        this.processedFiles = processedFiles;
    }



    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }



    
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }



    
    /**
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }



    
    /**
     * @param surname the surname to set
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }



    
    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }



    
    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }




}
