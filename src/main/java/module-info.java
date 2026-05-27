module org.example.lfrs_group_4_oop {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens org.example.lfrs_group_4_oop to javafx.fxml;
    opens org.example.lfrs_group_4_oop.controller to javafx.fxml;
    opens org.example.lfrs_group_4_oop.dto to javafx.base;
    
    exports org.example.lfrs_group_4_oop;
    exports org.example.lfrs_group_4_oop.controller;
    exports org.example.lfrs_group_4_oop.dao;
    exports org.example.lfrs_group_4_oop.entity;
    exports org.example.lfrs_group_4_oop.repository;
    exports org.example.lfrs_group_4_oop.model;
    exports org.example.lfrs_group_4_oop.database;
    exports org.example.lfrs_group_4_oop.exception;
    exports org.example.lfrs_group_4_oop.validator;
    exports org.example.lfrs_group_4_oop.dto;
    exports org.example.lfrs_group_4_oop.service;
}