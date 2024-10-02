package com.ytdtb.app;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class Web {
    Logger log = Logger.getLogger(Web.class.getName());

    @Value( "${data.dir}" )
    public String dataDirectory;

    @RequestMapping(value = "/data/{youtube_id}", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getData(@PathVariable("youtube_id") String youtubeId, HttpServletResponse response) {
        log.log(Level.INFO, "getData: "+ youtubeId);
        response.setContentType("video/mp4");
        return new FileSystemResource(new File(dataDirectory + youtubeId+".mp4"));
    }
}
