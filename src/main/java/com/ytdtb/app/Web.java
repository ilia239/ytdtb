package com.ytdtb.app;

import jakarta.servlet.http.HttpServletResponse;
import javassist.bytecode.SignatureAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class Web {
    Logger log = Logger.getLogger(Web.class.getName());


    @Autowired
    Bot bot;

    @Value( "${data.dir}" )
    public String dataDirectory;

    @RequestMapping(value = "/v/{youtube_id}", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getData(@PathVariable("youtube_id") String youtubeId, HttpServletResponse response) {
        log.log(Level.INFO, "getData: "+ youtubeId);
        String filename = youtubeId+".mp4";
        File file = new File(dataDirectory + filename);
        response.setContentType("video/mp4");
        return new FileSystemResource(file);
    }

    @RequestMapping(value = "/dl/{youtube_id}", method = RequestMethod.GET)
    public FileSystemResource getContentDisposition(@PathVariable("youtube_id")String youtubeId, HttpServletResponse response) throws IOException {
        String filename = youtubeId+".mp4";
        File file = new File(dataDirectory + filename);
        HttpHeaders headers = new HttpHeaders();
        response.setContentType("video/mp4");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        return new FileSystemResource(file);
    }

    @RequestMapping(value = "/lv", method = RequestMethod.GET)
    public FileSystemResource getLastV(HttpServletResponse response) throws IOException {

        String filename = bot.lastYouTubeId+".mp4";
        File file = new File(dataDirectory + filename);

        HttpHeaders headers = new HttpHeaders();
        response.setContentType("video/mp4");
        return new FileSystemResource(file);
    }

    @RequestMapping(value = "/ldl", method = RequestMethod.GET)
    public FileSystemResource getLastDl(HttpServletResponse response) throws IOException {

        String filename = bot.lastYouTubeId+".mp4";
        File file = new File(dataDirectory + filename);

        HttpHeaders headers = new HttpHeaders();
        response.setContentType("video/mp4");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        return new FileSystemResource(file);
    }
}
