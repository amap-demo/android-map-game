package com.amap.map3d.demo.opengl.ms3dmodel.util;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TextResourceReader {
    /**
     * Reads in text from a resource file and returns a String containing the
     * text.
     */
    public static String readTextFileFromResource(Context context,
        int resourceId) {
        StringBuilder body = new StringBuilder();
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        try {
            inputStream = context.getResources()
                .openRawResource(resourceId);
            
            inputStreamReader = new InputStreamReader(
                inputStream, "utf-8");
            
            bufferedReader = new BufferedReader(
                inputStreamReader);
            String nextLine;

            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(
                "Could not open resource: " + resourceId, e);
        } catch (Resources.NotFoundException nfe) {
            throw new RuntimeException("Resource not found: "
                + resourceId, nfe);
		} finally {
			try {
				if (inputStreamReader != null) {
					inputStreamReader.close();
					inputStreamReader = null;
				}
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
				if (bufferedReader != null) {
					bufferedReader.close();
					bufferedReader = null;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

        return body.toString();
    }
}
