package com.saveql.backend.entity.system;

import com.saveql.backend.entity.singleton.SingletonEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "system_info")
public class SystemInfo extends SingletonEntity {

    private String applicationName = "SaveQL";
    private String applicationColor = "#000000";

}
