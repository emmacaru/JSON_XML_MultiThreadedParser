package com.peopleCO.processor.converters;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.peopleCO.processor.TO.PersonTO;
import com.peopleCO.processor.TO.PersonsTO;
import com.peopleCO.processor.hibernate.data.PersonData;
import com.peopleCO.processor.hibernate.data.ProcessedFiles;

/**
 * @author ecaruana
 *
 */
public class TOMappers {
	
	/**
	 * 
	 * @param persons
	 * @return
	 */
	public ProcessedFiles toProcessedFiles(PersonsTO persons)
	{
		//0 == xml
		//1 == json
		
		byte filetype = 0;
		
		String filename = persons.getFilename();

		if (filename.contains(".xml"))
		{
			filetype = 0;
		}
		else if (filename.contains(".json"))
		{
			filetype = 1;
		}
		
		filename = filename.replace(".processing", "");
		
		ProcessedFiles retVal = new ProcessedFiles(filename,filetype);
		retVal.setPersonDatas(toPersonData(persons.getAllPerson(),retVal));

		
		return retVal;
	}
	
	
	/**
	 * @param persons
	 * @param processedFile
	 * @return
	 */
	private Set<PersonData> toPersonData(List<PersonTO> persons, ProcessedFiles processedFile)
	{
		Set<PersonData> retVal = new HashSet<PersonData>();
		
		for (PersonTO person : persons) {
			
			PersonData personData = new PersonData();
			
			personData.setLocation(person.getLocation());
			personData.setName(person.getName());
			personData.setSurname(person.getSurname());
			
			personData.setProcessedFiles(processedFile);
			
			retVal.add(personData);
		}
		
		return retVal;
	}

}
