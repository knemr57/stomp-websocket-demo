package com.afaqy.avl.websocket.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class HelloMessage implements Serializable {

    private static final long serialVersionUID = -1917531881067399801L;

    private String name;

}
