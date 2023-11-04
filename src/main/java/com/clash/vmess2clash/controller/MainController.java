package com.clash.vmess2clash.controller;

import com.clash.vmess2clash.entity.BaseEntity;
import com.clash.vmess2clash.entity.ShadowSocksEntity;
import com.clash.vmess2clash.entity.VmessEntity;
import com.clash.vmess2clash.service.MainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/main")
public class MainController {

    @Autowired
    private MainService mainService;
    @Autowired
    private ResourceLoader resourceLoader;
    Logger logger = LoggerFactory.getLogger(MainController.class);

    // only pure text/json requests
    @ResponseBody
    @RequestMapping("/doConvert")
    public String doConvert() {
        String[] links = mainService.fetchPreLinks();
        List<String> results = new ArrayList<>();
        List<BaseEntity> bes = mainService.procLinks(links);
        for(BaseEntity be : bes) {
            if(be instanceof VmessEntity) {
                VmessEntity ve = (VmessEntity) be;
                results.add(ve.toString());
            } else if(be instanceof ShadowSocksEntity) {
                ShadowSocksEntity se = (ShadowSocksEntity) be;
                results.add(se.toString());
            }
        }
        if(results.size() > 0) {
            return Arrays.toString(results.toArray());
        } else return String.valueOf(links.length);
    }

    @RequestMapping("/viewConfig")
    public void viewConfig(HttpServletResponse response) {
        try {
            Yaml yaml = new Yaml();
            Map<String, Object> map = mainService.parseConf();
            String[] links = mainService.fetchPreLinks();
            mainService.updateConf(map, mainService.procLinks(links));
            String opt = yaml.dumpAsMap(map);
            logger.info(opt);
            Resource resource = resourceLoader.getResource("classpath:static/rules.yaml");
            InputStream inputStream = resource.getInputStream();
            String result = opt + "\n" + new String(inputStream.readAllBytes());
            response.addHeader("Content-Disposition", "attachment; filename=JMSConfig.yaml");
            response.addHeader("Subscription-Userinfo", mainService.readDataUsage());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(result);
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }
}
