package com.ytdtb.ytdownloadbot;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class Web

{
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();


    private String getYoutubeId(String link) {
        //https://youtu.be/WidNsNk8vGE?si=drepREIB3iK8tu-4
        int lastId = link.lastIndexOf("v=");
        String id = link.substring(lastId+2);
        return id;
    }


    @RequestMapping(value = "/test/{file_name}", method = RequestMethod.GET)
    public String getTest(@PathVariable("file_name") String fileName, HttpServletResponse response) {
        return "test:"+getYoutubeId(fileName);
    }

    @RequestMapping(value = "/view/{file_name}", method = RequestMethod.GET)
    @ResponseBody
    public String getPage(@PathVariable("file_name") String fileName, HttpServletResponse response) {




        StringBuffer sb = new StringBuffer();
        sb.append("<video>\n");
        sb.append("  <source src=\"../data/"+fileName+"\" type=\"video/mp4\" />\n");
        sb.append("</video>");
        return sb.toString();
    }

    @RequestMapping(value = "/data/{youtube_id}", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getData(@PathVariable("youtube_id") String youtubeId, HttpServletResponse response) {
        response.setContentType("video/mp4");
        return new FileSystemResource(new File("/home/ilia/youtube/"+youtubeId+".mp4"));
    }


    @RequestMapping(value = "/files/{file_name}", method = RequestMethod.GET)
    @ResponseBody
    public String getFile(@PathVariable("file_name") String fileName, HttpServletResponse response) {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=somefile.pdf");
        return "a";
    }

}
