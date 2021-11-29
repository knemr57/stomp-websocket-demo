package com.afaqy.avl.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Greeting implements Serializable {

    private static final long serialVersionUID = 1790204258270614208L;

    private String content;

}
