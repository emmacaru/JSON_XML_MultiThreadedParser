package com.peopleCO.processor.fileTypeProcessor;


import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.peopleCO.processor.TO.PersonTO;
import com.peopleCO.processor.TO.PersonsTO;
import com.peopleCO.processor.utils.DBServices;
import com.peopleCO.processor.utils.FileMovingUtil;



/**
 * @author ecaruana
 *
 */
public class JsonFileThread
        implements Runnable {


    /**
     * logger
     */
    private final static Logger logger = Logger.getLogger(JsonFileThread.class.toString());

    /**
     * file property
     */
    private File file;



    public JsonFileThread(File file) {
        this.file = file;
    }



    public void run() {
        try {
            logger.info("JsonFileThread.run - JSON Thread started");

            // create a gson parser and gson object
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();

            // read json content using Apache Common IO
            String jsonFileContents = FileUtils.readFileToString(this.file);

            logger.info("JsonFileThread.run - JSON content read from file " + this.file.getName());

            // create Persons object
            PersonsTO persons = new PersonsTO();

            try {
                // parse file content to get a Json Array
                JsonArray personArray = parser.parse(jsonFileContents).getAsJsonArray();

                logger.info("JsonFileThread.run - JSON Array Parsed");

                persons.setFilename(this.file.getName());
                persons.setAllPerson(new ArrayList<PersonTO>());

                for (JsonElement jsonElement : personArray) {
                    PersonTO person = gson.fromJson(jsonElement, PersonTO.class);

                    logger.debug("JsonFileThread.run - Validating properties for NULLs");
                    
                    if (person.getLocation() == null || person.getName() == null || person.getSurname() == null) {
                        logger.info("JsonFileThread.run - Invalid data in file.  JSON Thread ended");
                        throw new Exception("was unable to load all properties");
                    }

                    logger.info("JsonFileThread.run - JSON converted to pojos successfully");

                    persons.getAllPerson().add(person);
                }

            }
            catch (Exception e) {
                logger.error("JsonFileThread.run- Failed to parse json file moving file to invalid folder.");
                logger.error(e.getStackTrace());
                new FileMovingUtil(this.file, false).fileMove();
                logger.info("JsonFileThread.run - JSON Thread ended");
                return;
            }

            DBServices persistLogic = new DBServices(persons);
            try {
                persistLogic.persistToDB();
            }
            catch (Exception e) {
                logger.error("JsonFileThread.run - Failed to persist pojos");
                logger.error(e.getStackTrace());
                logger.info("JsonFileThread.run - JSON Thread ended");
                return;
            }
            // if persists was successful then transfer files
            FileMovingUtil fml = new FileMovingUtil(this.file, true, persistLogic);
            fml.fileMove();
        }
        catch (Exception e) {
            logger.error("JsonFileThread.run - failed to parse json and transform in pojo");
            logger.error(e.getStackTrace());
        }
        logger.info("JsonFileThread.run - JSON Thread ended");
    }

}
