package com.alibaba.android.arouter.register.core;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author: created by leilu
 * email: lu.lei@hsbc.com
 */
public class IOUtils11 {

    public static byte[] toByteArray(InputStream is) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            while ((len = is.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bos.flush();
            bos.close();
        } catch (Exception e) {

        }
        return bos.toByteArray();
    }
}
