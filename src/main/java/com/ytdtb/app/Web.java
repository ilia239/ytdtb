package com.ytdtb.app;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

@RestController
public class Web {
    Logger log = LoggerFactory.getLogger(Web.class);

    @Autowired
    Bot bot;

    @Value( "${data.dir}" )
    public String dataDirectory;

    @RequestMapping(value = "/v/{youtube_id}", method = RequestMethod.GET, produces = {"video/mp4"})
    @ResponseBody
    public FileSystemResource getV(@PathVariable("youtube_id") String youtubeId, HttpServletResponse response) {
        log.info("getV: "+ youtubeId);
        String filename = youtubeId+".mp4";
        File file = new File(dataDirectory + filename);
        return new FileSystemResource(file);
    }

    @RequestMapping(value = "/lv", method = RequestMethod.GET, produces = {"video/mp4"})
    public FileSystemResource getLastV(HttpServletResponse response) throws IOException {
        return getV(bot.lastYouTubeId, response);
    }

    @RequestMapping(value = "/dl/{youtube_id}", method = RequestMethod.GET, produces = {"video/mp4"})
    public FileSystemResource getDl(@PathVariable("youtube_id")String youtubeId, HttpServletResponse response) {
        String filename = youtubeId+".mp4";
        File file = new File(dataDirectory + filename);
        HttpHeaders headers = new HttpHeaders();
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
        return new FileSystemResource(file);
    }

    @RequestMapping(value = "/ldl", method = RequestMethod.GET, produces = {"video/mp4"})
    public FileSystemResource getLastDl(HttpServletResponse response) throws IOException {
        return getDl(bot.lastYouTubeId, response);
    }
}
