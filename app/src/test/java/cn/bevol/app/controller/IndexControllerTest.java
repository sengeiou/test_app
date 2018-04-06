package cn.bevol.app.controller;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class IndexControllerTest {

    @Autowired
    private MockMvc mvc;

    /**
     * 开启app(init,index,openApp)
     * @throws Exception
     */
    @Test
    public void testIndex7() throws Exception {
        List<Map<String,String>> uriList=new ArrayList<Map<String,String>>();
        Map<String,String> uriMap=new HashMap<String,String>();
        uriMap.put("/open/app2","?open_o=0&open_v=3.2");
        uriList.add(uriMap);

        uriMap=new HashMap<String,String>();
        uriMap.put("/index7","?position_type=1&pager=1");
        uriList.add(uriMap);

        uriMap=new HashMap<String,String>();
        uriMap.put("/init8","");
        uriList.add(uriMap);

        for(Map<String,String> map:uriList){
            for(Map.Entry<String,String> entry:map.entrySet()){
                String uri=entry.getKey();
                if(StringUtils.isNotBlank(entry.getValue())){
                    uri=entry.getKey()+entry.getValue();
                }
                mvc.perform(post(uri))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                        .andExpect(jsonPath("$.ret").value(1));
            }
        }
    }
}
