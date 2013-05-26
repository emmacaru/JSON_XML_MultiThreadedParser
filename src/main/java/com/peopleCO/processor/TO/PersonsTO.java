package com.peopleCO.processor.TO;


import java.util.List;



/**
 * @author ecaruana
 * 
 */
public class PersonsTO {

    /**
     * property
     */
    private List<PersonTO> allPerson;

    /**
     * property
     */
    private String filename;



    /**
     * @return the allPerson
     */
    public List<PersonTO> getAllPerson() {
        return allPerson;
    }



    /**
     * @param allPerson the allPerson to set
     */
    public void setAllPerson(List<PersonTO> allPerson) {
        this.allPerson = allPerson;
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


}
