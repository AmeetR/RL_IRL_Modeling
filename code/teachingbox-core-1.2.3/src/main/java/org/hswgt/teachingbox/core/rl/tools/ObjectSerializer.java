/**
 * 
 * $$Id: ObjectSerializer.java 988 2015-06-17 19:48:01Z micheltokic $$
 * 
 * @version $$Rev: 988 $$
 * @author $$Author: micheltokic $$
 * @date $$Date: 2015-06-17 21:48:01 +0200 (Wed, 17 Jun 2015) $$
 * 
 */

package org.hswgt.teachingbox.core.rl.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * This is a helper class to save and load objects
 */
public class ObjectSerializer
{
    static Logger log4j = Logger.getLogger("ObjectSerializer");

    /**
     * Save Object to OutputStream
     * 
     * @param stream The output stream
     * @param obj The object
     */
    public static void save(OutputStream stream, Serializable obj)
    {
        try
        {
            ObjectOutputStream output = new ObjectOutputStream(stream);
            try
            {
                output.writeObject(obj);
            }
            finally
            {
                output.close();
            }
        }
        catch (IOException e)
        {
            log4j.error("Unable to write: " + e.toString());
        }
    }

    /**
     * Saves an object to a file
     * 
     * @param filename The filename
     * @param obj The object
     */
    public static void save(String filename, Serializable obj)
    {
        try
        {
            OutputStream file = new FileOutputStream(filename);
            OutputStream buffer = new BufferedOutputStream(file);
            ObjectSerializer.save(buffer, obj);
        }
        catch (FileNotFoundException e)
        {
            log4j.error("Unable to open file: " + filename);
        }

    }

    /**
     * loads an object from file
     * 
     * @param <T> The type
     * 
     * @param filename The filename
     * @return The casted object
     */
    @SuppressWarnings("unchecked")
    public static <T> T load(String filename)
    {
        try
        {
            InputStream file = new FileInputStream(filename);
            InputStream buffer = new BufferedInputStream(file);
            return (T) ObjectSerializer.load(buffer);
        }
        catch (FileNotFoundException e)
        {
            log4j.error("Unable to open file: " + filename);
        }
        return null;
    }

    /**
     * loads an object from InputStream
     * 
     * @param <T> Tge type
     * 
     * @param stream The stream to load from
     * @return The casted object
     */
    @SuppressWarnings("unchecked")
    public static <T> T load(InputStream stream)
    {
        try
        {
            ObjectInput input = new ObjectInputStream(stream);
            try
            {
                return (T) input.readObject();
            }
            finally
            {
                input.close();
            }
        }
        catch (IOException e)
        {
            log4j.error("Unable to read Object: " + e);
        }

        catch (ClassNotFoundException e)
        {
            log4j.error("Unable to read Object: " + e);
        }
        return null;
    }
}
