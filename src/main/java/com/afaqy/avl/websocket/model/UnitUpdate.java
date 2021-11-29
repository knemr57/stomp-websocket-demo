package com.afaqy.avl.websocket.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class UnitUpdate implements Serializable {

    private static final long serialVersionUID = 9113797720139981345L;

    private String id;
    private String message;

}
