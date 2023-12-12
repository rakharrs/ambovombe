package com.etu1999.test.model;

import com.etu1999.ambovombe.core.process.DAO;
import com.etu1999.ambovombe.mapping.annotation.data.Column;
import com.etu1999.ambovombe.mapping.annotation.data.ForeignKey;
import com.etu1999.ambovombe.mapping.annotation.data.Id;
import com.etu1999.ambovombe.mapping.annotation.data.UnitSource;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@UnitSource("connection-1")
@Getter @Setter @NoArgsConstructor
public class District extends DAO{

    @Id
    @Column
    private int id;

    @ForeignKey(mappedBy = "id_region")
    private Region region;
    
    @Column("nom_district")
    private String nomDistrict;
}