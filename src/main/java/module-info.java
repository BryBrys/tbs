module com.login.apilogin {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
	requires org.apache.poi.poi;
	requires org.apache.poi.ooxml;
	requires org.controlsfx.controls;
	requires org.apache.httpcomponents.client5.httpclient5;
	requires org.apache.httpcomponents.core5.httpcore5;
	requires com.google.gson;
	requires java.net.http;
	requires braintree.java;
	requires javafx.graphics;
	requires datetime.picker.javafx;

    opens classEntities to org.hibernate.orm.core; // Open the entities package to Hibernate
    opens com.login.apilogin to javafx.fxml; // Open controllers to JavaFX
    exports com.login.apilogin;
    
    
}
