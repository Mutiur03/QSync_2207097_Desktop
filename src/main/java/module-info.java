module com.example.qsync_2207097_desktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    requires java.sql;
    requires java.prefs;
    opens com.example.qsync_2207097_desktop to javafx.fxml;
    opens com.example.qsync_2207097_desktop.admin to javafx.fxml;
    exports com.example.qsync_2207097_desktop;
    exports com.example.qsync_2207097_desktop.admin;
    exports com.example.qsync_2207097_desktop.user;
    opens com.example.qsync_2207097_desktop.user to javafx.fxml;
}