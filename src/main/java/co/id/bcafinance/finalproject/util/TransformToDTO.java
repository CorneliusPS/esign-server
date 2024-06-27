package co.id.bcafinance.finalproject.util;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public class TransformToDTO {
    private String sortBy = "";
    private String sort = "";

    public Map<String,Object> transformObject(Map<String,Object> mapz, List ls, Page page
            ,String columnFirst,String valueFirst)//<PENAMBAHAN 21-12-2023>
    {
        sortBy = page.getSort().toString().split(":")[0];
        sortBy = sortBy.equals("UNSORTED")?"id":sortBy;
        sort   = sortBy.equals("UNSORTED")?"asc":page.getSort().toString().split(":")[1];
        mapz.put("content",ls);
        mapz.put("totalItems",page.getTotalElements());
        mapz.put("totalPages",page.getTotalPages());
        mapz.put("sort",sort.trim().toLowerCase());
        mapz.put("numberOfElements",page.getNumberOfElements());
        mapz.put("filter-by",columnFirst);
        mapz.put("value",valueFirst);

        return mapz;
    }
    public Map<String,Object> transformObject(Map<String,Object> mapz, List ls, Page page
            ,String columnFirst,String valueFirst,List componentFiltering)//<PENAMBAHAN 21-12-2023>
    {
        sortBy = page.getSort().toString().split(":")[0];
        sortBy = sortBy.equals("UNSORTED")?"id":sortBy;
        sort   = sortBy.equals("UNSORTED")?"asc":page.getSort().toString().split(":")[1];
        mapz.put("content",ls);
        mapz.put("totalItems",page.getTotalElements());
        mapz.put("totalPages",page.getTotalPages());
        mapz.put("sort",sort.trim().toLowerCase());
        mapz.put("numberOfElements",page.getNumberOfElements());
        mapz.put("filter-by",(columnFirst==null||columnFirst.equals("")||columnFirst.equals("id"))?"id":columnFirst);
        mapz.put("component-filter",componentFiltering);
        mapz.put("value",valueFirst);

        return mapz;
    }

    public Map<String,Object> transformObjectLong(Map<String,Object> mapz, List ls, Page page
            ,String columnFirst,Long valueFirst,List componentFiltering)//<PENAMBAHAN 21-12-2023>
    {
        sortBy = page.getSort().toString().split(":")[0];
        sortBy = sortBy.equals("UNSORTED")?"id":sortBy;
        sort   = sortBy.equals("UNSORTED")?"asc":page.getSort().toString().split(":")[1];
        mapz.put("content",ls);
        mapz.put("totalItems",page.getTotalElements());
        mapz.put("totalPages",page.getTotalPages());
        mapz.put("sort",sort.trim().toLowerCase());
        mapz.put("numberOfElements",page.getNumberOfElements());
        mapz.put("filter-by",(columnFirst==null||columnFirst.equals("")||columnFirst.equals("id"))?"id":columnFirst);
        mapz.put("component-filter",componentFiltering);
        mapz.put("value",valueFirst);

        return mapz;
    }
}