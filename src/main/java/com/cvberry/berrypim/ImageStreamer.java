package com.cvberry.berrypim;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

/**
 * Created by vancan1ty on 1/5/2016.
 */
public class ImageStreamer {

    public Map<String, BufferedImage> unservedImages;
    public LinkedList<String> queue;
    public static final int MAX_CACHE_SIZE = 25;
    private Random rand;

    public ImageStreamer() {
        this.unservedImages = new HashMap<>(MAX_CACHE_SIZE);
        this.queue = new LinkedList<>();
        rand = new Random();
    }

    public String enqueueImage(BufferedImage image) {
        long nextNumber = rand.nextLong();
        String fileID = nextNumber+".png";
        enqueueImage(image,fileID);
        return fileID;
    }

    public void enqueueImage(BufferedImage image, String imageID) {
        if (unservedImages.size() < MAX_CACHE_SIZE) {
            unservedImages.put(imageID, image);
            queue.addFirst(imageID);
        } else { //then we'll have to evict the oldest one
            String lastKey = queue.removeLast();
            unservedImages.remove(lastKey);

            unservedImages.put(imageID, image);
            queue.addFirst(imageID);
        }
    }

    public void getImage(String imageID, HttpServletResponse response) {
        BufferedImage renderedImage = unservedImages.get(imageID);
        //CB TODO figure out how to evict image after use
        sendImage(renderedImage,"image/png","png",response);
    }

    public void sendImage(BufferedImage image, String mimeType, String imageIOType, HttpServletResponse response) {
        if (image != null) {
            OutputStream output = null;
            try {
                output = response.getOutputStream();
                response.setContentType(mimeType);
                ImageIO.write(image, imageIOType, output);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (output != null) {
                try {
                    output.flush();
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
