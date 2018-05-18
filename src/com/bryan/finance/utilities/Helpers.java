package com.bryan.finance.utilities;

import com.bryan.finance.literals.ApplicationLiterals;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Helpers extends ApplicationLiterals {

    private static Logger logger = Logger.getLogger(Helpers.class);

    public static String[] getSystemInfo() {
        InetAddress ip;
        String hostname;
        String username;
        String[] data = new String[3];
        try {
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
            username = System.getProperty(USER_NAME);
            data[0] = ip.toString();
            data[1] = hostname;
            data[2] = username;
        } catch (UnknownHostException e) {
            logger.warn("Unable to log system info");
        }
        return data;
    }

    public static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDirectory(new File(dir, child));
                if (!success)
                    return false;
            }
        }
        return dir.delete();
    }
}
