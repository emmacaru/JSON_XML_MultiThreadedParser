package com.peopleCO.processor.main;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.peopleCO.processor.fileTypeProcessor.StarterThread;
import com.peopleCO.processor.utils.HibernateSessionFactory;



public class MainRunner {

    /**
     * logger
     */
    private final static Logger logger = Logger.getLogger(MainRunner.class.toString());

    /**
	 * 
	 */
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    /**
	 * 
	 */
    public static boolean running = true;



    /**
     * Main method
     * 
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args)
            throws IOException, InterruptedException {

        if (testDBConnection() == false)
            return;

        // starts the working thread - which creates new threads
        StarterThread st = new StarterThread(executorService,true ,false);
        executorService.execute(st);

        
        StarterThread st2 = new StarterThread(executorService, false, true);
        executorService.execute(st2);

        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String userInput = "";

        // wait for exit to be typed
        while ((userInput = br.readLine()) != null) {

            if (userInput.equalsIgnoreCase("exit"))
                break;

            System.out.println("Type 'exit' to close the program.");
        }

        running = false;

        logger.info("Main thread - closing down....");

    }



    /**
     * Test DB Connection
     * 
     * @return boolean
     */
    private static boolean testDBConnection() {

        try {
            logger.info("Testing DB Connection before starting up");
            Session session = HibernateSessionFactory.getSessionFactory().openSession();
            session.beginTransaction();
            session.getTransaction().commit();
            logger.info("Testing DB Connection before starting up - SUCCESS");
        }
        catch (Exception e) {
            logger.info("Testing DB Connection before starting up - FAILED");
            logger.error(e.getMessage());
            return false;
        }

        return true;

    }

}
