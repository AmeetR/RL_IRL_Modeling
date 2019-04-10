/**
 *
 * $$Id: PropertyLoader.java 475 2009-12-15 09:10:57Z Markus Schneider $$
 *
 * @version   $$Rev: 475 $$
 * @author    $$Author: Markus Schneider $$
 * @date      $$Date: 2009-12-15 10:10:57 +0100 (Tue, 15 Dec 2009) $$
 *
 */

package org.hswgt.teachingbox.core.rl.tools;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Load Properties from a file
 */
public abstract class PropertyLoader
{
    /**
     * Looks up a resource named 'name' in the classpath. The resource must map
     * to a file with .properties extention. The name is assumed to be absolute
     * and can use either "/" or "." for package segment separation with an
     * optional leading "/" and optional ".properties" suffix. Thus, the
     * following names refer to the same resource:
     * 
     * <pre>
     * some.pkg.Resource
     * some.pkg.Resource.properties
     * some/pkg/Resource
     * some/pkg/Resource.properties
     * /some/pkg/Resource
     * /some/pkg/Resource.properties
     * </pre>
     * 
     * @param filename classpath resource name [may not be null]
     * @param loader classloader through which to load the resource [null is
     *        equivalent to the application loader]
     * 
     * @return resource converted to java.util.Properties [may be null if the
     *         resource was not found and THROW_ON_LOAD_FAILURE is false]
     * @throws IllegalArgumentException if the resource was not found and
     *         THROW_ON_LOAD_FAILURE is true
     */
    public static Properties loadProperties(String filename, ClassLoader loader)
    {
        if (filename == null)
            throw new IllegalArgumentException("null input: filename");

        if (filename.startsWith("/"))
            filename = filename.substring(1);

        if (filename.endsWith(SUFFIX))
            filename = filename.substring(0, filename.length()
                    - SUFFIX.length());

        Properties result = null;

        InputStream in = null;
        try
        {
            if (loader == null)
                loader = ClassLoader.getSystemClassLoader();

            if (LOAD_AS_RESOURCE_BUNDLE)
            {
                filename = filename.replace('/', '.');
                
                // Throws MissingResourceException on lookup failures:
                final ResourceBundle rb = ResourceBundle.getBundle(filename,
                        Locale.getDefault(), loader);

                result = new Properties();
                
                for (Enumeration<String> keys = rb.getKeys(); keys
                        .hasMoreElements();)
                {
                    final String key = keys.nextElement();
                    final String value = rb.getString(key);

                    result.put(key, value);
                }
            }
            else
            {
                filename = filename.replace('.', '/');

                if (!filename.endsWith(SUFFIX))
                    filename = filename.concat(SUFFIX);

                // Returns null on lookup failures:
                in = loader.getResourceAsStream(filename);
                if (in != null)
                {
                    result = new Properties();
                    result.load(in); // Can throw IOException
                }
            }
        }
        catch (Exception e)
        {
            result = null;
        }
        finally
        {
            if (in != null)
                try
                {
                    in.close();
                }
                catch (Throwable ignore)
                {
                }
        }

        if (THROW_ON_LOAD_FAILURE && (result == null))
        {
            throw new IllegalArgumentException("could not load ["
                    + filename
                    + "]"
                    + " as "
                    + (LOAD_AS_RESOURCE_BUNDLE ? "a resource bundle"
                            : "a classloader resource"));
        }

        return result;
    }

    /**
     * A convenience overload of {@link #loadProperties(String, ClassLoader)}
     * that uses the current thread's context classloader.
     * 
     * @param filename classpath resource name [may not be null]
     * @return resource converted to java.util.Properties [may be null if the
     *         resource was not found and THROW_ON_LOAD_FAILURE is false]
     */
    public static Properties loadProperties(final String filename)
    {
        return loadProperties(filename, Thread.currentThread()
                .getContextClassLoader());
    }

    private static final boolean THROW_ON_LOAD_FAILURE   = true;
    private static final boolean LOAD_AS_RESOURCE_BUNDLE = false;
    private static final String  SUFFIX                  = ".properties";
} // End of class