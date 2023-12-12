package com.etu1999.test.model;

import java.util.List;

import com.etu1999.ambovombe.core.exception.DAOException;
import com.etu1999.ambovombe.core.process.DAO;
import com.etu1999.ambovombe.mapping.annotation.data.Column;
import com.etu1999.ambovombe.mapping.annotation.data.ForeignKey;
import com.etu1999.ambovombe.mapping.annotation.data.Id;
import com.etu1999.ambovombe.mapping.annotation.data.Table;
import com.etu1999.ambovombe.mapping.annotation.data.UnitSource;
import com.etu1999.ambovombe.mapping.annotation.more.Inherit;
import com.etu1999.ambovombe.mapping.conf.ForeignType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@UnitSource("connection-1")
@Table("region")
@Getter @Setter 
@NoArgsConstructor @AllArgsConstructor
public class Region extends DAO{

    @Id
    @Column @Inherit
    private int id;

    @ForeignKey(
        mappedBy = "id_region",
        foreignType = ForeignType.OneToMany
    )
    private List<District> districts;

    @Column("nom_region")
    private String nomRegion;
}
