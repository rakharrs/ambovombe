package com.etu1999.test;

import java.sql.Connection;
import java.util.List;

import com.etu1999.ambovombe.core.process.query.QueryForge;
import com.etu1999.test.model.District;
import com.etu1999.test.model.Region;

public class Test {
    public static void main(String[] args) throws Exception {
        Region r = new Region();
        Connection c = r.createConnection();
        District d = new District();
        /*List<District> districts = r.findAll(c);
        for (District district : districts) {
            System.out.println(district.getNomDistrict() + " - " +district.getRegion().getNomRegion());
        }*/

        List<Region> regions = r.findAll(c);
        System.out.println(QueryForge.insert(d));
        for (Region region : regions) {
            System.out.println(region.getNomRegion());
            //region.initForeignKey(c, "districts");
            System.out.println();
        }
        c.commit();
        c.close();
    }
}
