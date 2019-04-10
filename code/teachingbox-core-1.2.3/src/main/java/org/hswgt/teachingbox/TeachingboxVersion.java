package org.hswgt.teachingbox;

import java.io.InputStream;
import java.util.Properties;

public class TeachingboxVersion {
	public synchronized static String getVersion() {
	    String version = null;

	    // try to load from maven properties
	    try {
	        Properties p = new Properties();
	        InputStream is = TeachingboxVersion.class.getResourceAsStream("/teachingbox-version.txt");
	        if (is != null) {
	            p.load(is);
	            version = p.getProperty("version", "");
	            version += ", build: " + p.getProperty("build.date", "");
	        }
	    } catch (Exception e) {
	        // ignore
	    }

	    if (version == null) {
	        // we could not compute the version so use a blank
	        version = "no version";
	    }

	    return version;
	} 

    
    public static void main(String[] args) {    	
		System.out.println("Teaching-Box Version: " + TeachingboxVersion.getVersion());
	}
}
