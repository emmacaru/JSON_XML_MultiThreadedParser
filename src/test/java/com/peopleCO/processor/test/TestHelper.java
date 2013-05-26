package com.peopleCO.processor.test;

import java.io.File;

import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Ignore;

import com.peopleCO.processor.utils.HibernateSessionFactory;

@Ignore
public class TestHelper {
	
		
	/**
	 * Helper functions
	 *  
	 */			
	
	public static void dbCleaning()
	{
		Session session = HibernateSessionFactory.getSessionFactory().openSession();
		
		session.beginTransaction();
		
		Query q = session.createQuery("delete from PersonData");
		q.executeUpdate();
		
		q = session.createQuery("delete from ProcessedFiles");
		q.executeUpdate();
		
		
		session.getTransaction().commit();
	}
	
	public static void folderCleaning()
	{
		folderCleaning("archive");
		folderCleaning("pending");
		folderCleaning("invalid");
	}
	
	public static void folderCleaning(String folderName)
	{
		File directory = new File(folderName);
		// Get all files in directory
		File[] files = directory.listFiles();
		for (File file : files)
		{
		   // Delete each file
		   file.delete();
		}
	}
		

}
