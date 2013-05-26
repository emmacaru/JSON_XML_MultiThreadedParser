package com.peopleCO.processor.hibernate.data;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;



public class ProcessedFiles
        implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1638092279896538474L;
    
    
    /**
     * unique id
     */
    private Integer id;
    /**
     * filename
     */
    private String filename;
    /**
     * fileType (0 xml, 1 json)
     */
    private byte filetype;
    /**
     * Reference to set of Person (One to Many) 
     */
    private Set personDatas = new HashSet(0);



    public ProcessedFiles() {
    }



    /**
     * @param filename
     * @param filetype
     */
    public ProcessedFiles(String filename, byte filetype) {
        this.filename = filename;
        this.filetype = filetype;
    }



    /**
     * @param filename
     * @param filetype
     * @param personDatas
     */
    public ProcessedFiles(String filename, byte filetype, Set personDatas) {
        this.filename = filename;
        this.filetype = filetype;
        this.personDatas = personDatas;
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
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }



    
    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }



    
    /**
     * @return the filetype
     */
    public byte getFiletype() {
        return filetype;
    }



    
    /**
     * @param filetype the filetype to set
     */
    public void setFiletype(byte filetype) {
        this.filetype = filetype;
    }



    
    /**
     * @return the personDatas
     */
    public Set getPersonDatas() {
        return personDatas;
    }



    
    /**
     * @param personDatas the personDatas to set
     */
    public void setPersonDatas(Set personDatas) {
        this.personDatas = personDatas;
    }



}
