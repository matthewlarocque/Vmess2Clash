package com.clash.vmess2clash.service;

import com.clash.vmess2clash.entity.BaseEntity;
import com.clash.vmess2clash.entity.ShadowSocksEntity;
import com.clash.vmess2clash.entity.VmessEntity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MainService {
    @Value("${subscription.url}")
    private String subUrl;

    @Value("${api.url}")
    private String apiUrl;

    @Autowired
    private ResourceLoader resourceLoader;

    Logger logger = LoggerFactory.getLogger(MainService.class);

    public String deBase64(String cipher) {
        byte[] decodedBytes = Base64.getDecoder().decode(cipher);
        return new String(decodedBytes);
    }

    public String[] fetchPreLinks() {
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = template.getForEntity(this.subUrl, String.class);
        String res = deBase64(resp.getBody());
        String[] links = res.split("\n");
        return links;
    }

    public List<BaseEntity> procLinks(String[] links) {
        List<BaseEntity> entityList = new ArrayList<>();
        for(String link : links) {
            if(link.startsWith("ss://")) {
                ShadowSocksEntity entity = new ShadowSocksEntity();
                String compConf = link.split("ss://")[1];
                String realConf = deBase64(compConf.split("#")[0]);
                String[] splits = realConf.split(":");
                entity.setPs(compConf.split("#")[1]); // name
                entity.setEncMethod(splits[0]);
                entity.setPort(Integer.parseInt(splits[2]));
                entity.setId(splits[1].split("@")[0]);
                entity.setAdd(splits[1].split("@")[1]);
                entityList.add(entity);
            }
            else if(link.startsWith("vmess://")) {
                String realConf = deBase64(link.split("vmess://")[1]);
                Gson gson = new Gson();
                VmessEntity entity = gson.fromJson(realConf, VmessEntity.class);
                entityList.add(entity);
            }
        }
        return entityList;
    }

    public Map<String, Object> parseConf() {
        try {
            Resource resource = resourceLoader.getResource("classpath:static/sub.yaml");
            InputStream inputStream = resource.getInputStream();
            Yaml yaml = new Yaml();
            Map<String, Object> map = yaml.load(inputStream);
            return map;
        } catch (IOException e) {
            logger.error(e.toString());
            return new HashMap<>();
        }
    }

    public void updateConf(Map<String, Object> conf, List<BaseEntity> entityList) {
        // add proxy names
        List<String> proxyNames = entityList.stream().map(BaseEntity::getPs).collect(Collectors.toList());
        ArrayList<HashMap<String, Object>> gps = (ArrayList<HashMap<String, Object>>) conf.getOrDefault("proxy-groups", new ArrayList<HashMap<String, Object>>());
        gps.forEach(gp -> {
            ArrayList<String> lists = (ArrayList<String>) gp.get("proxies");
            if(lists == null) {
                gp.replace("proxies", new ArrayList<String>());
                lists = (ArrayList<String>) gp.get("proxies");
            }
            lists.addAll(proxyNames);
        });
        // add proxy configurations
        ArrayList<HashMap<String, Object>> proxies = entityList.stream().map(be -> {
            if (be instanceof VmessEntity) {
                VmessEntity ve = (VmessEntity) be;
                return new HashMap<String, Object>() {{
                    put("name", ve.getPs());
                    put("server", ve.getAdd());
                    put("port", ve.getPort());
                    put("type", "vmess");
                    put("uuid", ve.getId());
                    put("alterId", ve.getAid());
                    put("cipher", "auto");
                    put("tls", !ve.getTls().equals("none"));
                }};
            } else if (be instanceof ShadowSocksEntity) {
                ShadowSocksEntity se = (ShadowSocksEntity) be;
                return new HashMap<String, Object>() {{
                    put("name", se.getPs());
                    put("server", se.getAdd());
                    put("port", se.getPort());
                    put("type", "ss");
                    put("cipher", se.getEncMethod());
                    put("password", se.getId());
                }};
            } else {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));
        conf.put("proxies", proxies);
    }

    public long getDueTimeStamp(int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, n);
        Date date = calendar.getTime();
        long timestamp = date.getTime() / 1000L;
        return timestamp;
    }

    public String readDataUsage() {
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = template.getForEntity(this.apiUrl, String.class);
        Gson gson = new Gson();
        String respStr = resp.getBody();
        JsonObject jsonObject = gson.fromJson(respStr, JsonObject.class);
        long bwCounter = jsonObject.get("bw_counter_b").getAsLong();
        bwCounter /= Math.pow(1000, 3);
        bwCounter *= Math.pow(1024, 3);
        long monthlyBwLimit = jsonObject.get("monthly_bw_limit_b").getAsLong();
        monthlyBwLimit /= Math.pow(1000, 3);
        monthlyBwLimit *= Math.pow(1024, 3);
        int dueDate = jsonObject.get("bw_reset_day_of_month").getAsInt();
        StringBuilder result = new StringBuilder();
        result.append("upload=" + bwCounter / 2);
        result.append("; ");
        result.append("download=" + bwCounter / 2);
        result.append("; ");
        result.append("total=" + monthlyBwLimit);
        result.append("; ");
        result.append("expire=" + getDueTimeStamp(dueDate));
        return result.toString();
    }

}
