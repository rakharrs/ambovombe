package com.etu1999.test;

import java.sql.Connection;
import java.util.List;

import com.etu1999.ambovombe.core.process.query.QueryForge;
import com.etu1999.ambovombe.utils.Dhelper;
import com.etu1999.test.model.District;
import com.etu1999.test.model.Region;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class Test {
    public static void main(String[] args) throws Exception {
        Region r = new Region();
        Connection c = r.createConnection();
        District d = new District();
        String[] dd = Dhelper.getFieldsName(d);
            System.out.println(QueryForge.insert(r));
        List<District> districts = d.findAll(c);
        for (District district : districts) {
            
        }


        /*List<Region> regions = r.findAll(c);
        for (Region region : regions) {
            System.out.println(region.getNomRegion());
            region.initForeignKey(c, "districts");
            System.out.println();
            System.out.println(QueryForge.insert(region));
            break;
        }*/
        //c.commit();
        c.close();

    }
}
